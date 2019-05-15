package com.plugin.tianxingzhex.hook.yunshangfu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.plugin.tianxingzhex.server.BroadCaseSendUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tianxingzhe.plugin.utils.Utils.LogUtil;

/**
 * Created by chenguowu on 2019/2/23.
 */
public class YunShanFuHook {
    public static final String YUNSHANFU_PACKAGE_NAME = "com.unionpay";
    public static boolean YUNSHANFU_ISHOOK = false;
    public static final String ACTION_GET_CODE = "action_get_qrcode";
    public static final String ACTION_YUN_SHAN_FU = "action_yun_shan_fu";
    private ExecutorService fixedThread = Executors.newFixedThreadPool(5);
    private ClassLoader mClassLoader;
    private Context mContext;
    private Class UPPushService;

    private Activity activity;


    public void hook(final ClassLoader classLoader, final Context context) {

        mClassLoader = classLoader;
        this.mContext = context;
        registReceive(context);


        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String cls_name = (String) param.args[0];
                if (cls_name.equals("com.unionpay.push.UPPushService")) {
                    if (UPPushService != null) return;
                    UPPushService = (Class) param.getResult();
                    hookPushService(UPPushService);
                }

            }
        });
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.thisObject.getClass().toString().contains("com.unionpay.activity.UPActivityMain")) {
                    activity = (Activity) param.thisObject;
                    LogUtil.e("========================UPActivityMain====UPActivityMain=: " + activity);
                }
            }
        });


        //收款记录
        try {
            Class<?> aClass = XposedHelpers.findClass("com.unionpay.network.model.UPID", classLoader);
            XposedHelpers.findAndHookMethod("com.unionpay.activity.payment.UPActivityP2PTransferRecord", classLoader, "a", aClass, String.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    LogUtil.e("a");
                    List c = (List) XposedHelpers.getObjectField(param.thisObject, "c");
                    LogUtil.e("c.size(): " + c.size());
                    for (Object o : c) {
                        //UPP2PTransferOrderInfo
                        Object a = XposedHelpers.callMethod(o, "a");
                        if (!(a instanceof String)) {
                            Object tn = XposedHelpers.callMethod(a, "getUrl");
                            LogUtil.e("item getUrl: " + tn);

                        }
                    }

                    super.afterHookedMethod(param);
                }

            });
        } catch (Throwable throwable) {
            LogUtil.e("UPReactNativeActivity Throwable");
        }

    }

    private void hookPushService(Class upPushService) {
        LogUtil.e("-----------------开始hookPushService------------------");
        XposedBridge.hookAllMethods(upPushService, "a", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(final MethodHookParam param) throws Throwable {
                LogUtil.e("UPPushService a");
                try {
                    //云闪付app 扫收款码提示信息
//                    {"mContent":"*国武通过扫码向您付款1.00元,您的收款卡尾号为招商银行借记卡(尾号2151)","mSpeakAllow":"1","mTitle":"动账通知"}
//                    {"mContent":"您尾号为3574的银行卡于12日11时34分消费1.00元","mSpeakAllow":"1","mTitle":"动账通知"}
                    //银行app扫码支付提示信息
//                    {"mContent":"*国武通过扫码向您付款0.25元,您的收款卡尾号为招商银行借记卡(尾号2151)","mSpeakAllow":"1","mTitle":"动账通知"}
                    //建行app 扫收款码提示信息
//                    {"mContent":"您尾号为2151的银行卡于12日11时41分入账1.00元","mSpeakAllow":"1","mTitle":"动账通知"}
                    if (param.args != null && param.args.length > 0) {
                        Object uPPushMessage = param.args[0];
                        Object mText = XposedHelpers.callMethod(uPPushMessage, "getText");
                        String re = JSON.toJSONString(mText);
                        LogUtil.e("hookPushServicemText =" + re);
                        if (TextUtils.isEmpty(re)) return;
                        JSONObject object = new JSONObject(re);

                        Iterator<String> keys = object.keys();
                        while (keys.hasNext()){
                            String mContent = object.getString(keys.next());
                            if (mContent.contains("通过扫码向您付款")) {
                                String[] split = mContent.split("元")[0].split("通过扫码向您付款");
                                if (null != split && split.length >= 2) {
                                    String name = split[0];
                                    String money = split[1];
                                    LogUtil.e("==========回调  name：" + name);
                                    LogUtil.e("==========回调  money：" + money);
                                    upYunShanFuPaySuccess(money,name);

                                }
                            } else if (mContent.contains("入账")) {
                                String[] split = mContent.split("元")[0].split("入账");
                                String money = split[1];
                                LogUtil.e("==========回调  money：" + money);
                                //您尾号为2151的银行卡于12日11时41分 裁切出账号
                                String[] accountSplit = split[0].split("尾号为");
                                String name = accountSplit[1].substring(0, 4);
                                LogUtil.e("==========回调  name：" + name);
                                upYunShanFuPaySuccess(money,name);

                            }
                        }

                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void upYunShanFuPaySuccess(String money,String name) {

        Intent intent = new Intent();
        intent.putExtra("money", money);
        intent.putExtra("name", name);
        BroadCaseSendUtil.backYunShanFuData(mContext, intent);

    }

//    public static Context getContext() {
//        try {
//            Class<?> activityThread = Class.forName("android.app.ActivityThread");
//            if (activityThread != null) {
//                Method method = activityThread.getMethod("currentActivityThread");
//                Object currentActivityThread = method.invoke(activityThread);//获取currentActivityThread 对象
//                Method method2 = currentActivityThread.getClass().getMethod("getApplication");
//                Context context = (Context) method2.invoke(currentActivityThread);//获取 Context对象
//                XposedBridge.log("Context " + context);
//                return context;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    private YunShanFuReceive mReceiver;

    private void registReceive(Context context) {
        if (null == context) return;
        LogUtil.e("registYunShanFuReceive: ");
        if (null != mReceiver) context.unregisterReceiver(mReceiver);
        mReceiver = new YunShanFuReceive();

        IntentFilter billReceiverIntentFilter = new IntentFilter();
        billReceiverIntentFilter.addAction(ACTION_GET_CODE);
        context.registerReceiver(mReceiver, billReceiverIntentFilter);

    }


    class YunShanFuReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) return;
            if (ACTION_GET_CODE.equals(intent.getAction())) {

                getVirtualCardNum(intent.getStringExtra("money"), intent.getStringExtra("mark"));
            }
        }

    }

    private void getVirtualCardNum(final String money, final String remark) {
        LogUtil.e("getVirtualCardNum");
        fixedThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String str2 = "https://pay.95516.com/pay-web/restlet/qr/p2pPay/getInitInfo?cardNo=&cityCode=" + Enc(getcityCd());
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(str2).header("X-Tingyun-Id", getXTid())
                            .header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis()).header("sid", getSid()).header("urid", geturid())
                            .header("cityCd", getcityCd()).header("locale", "zh-CN").header("User-Agent", "Android CHSP").header("dfpSessionId", getDfpSessionId())
                            .header("gray", getgray()).header("key_session_id", "").header("Host", "pay.95516.com").build();
                    Response response = client.newCall(request).execute();
                    LogUtil.e("client execute");
                    if (response != null && response.body() != null) {
                        String RSP = response.body().string();
                        LogUtil.e("GetVirtualCardNum str2=>" + str2 + " RSP=>" + RSP);
                        String Rsp = Dec(RSP);
                        LogUtil.e("GetVirtualCardNum str2=>" + str2 + " RSP=>" + Rsp);
                        try {
                            String encvirtualCardNo = Enc(new JSONObject(Rsp).getJSONObject("params").getJSONArray("cardList").getJSONObject(0).getString("virtualCardNo"));
                            LogUtil.e("encvirtualCardNo: " + encvirtualCardNo);

                            GenQrCode(money, remark, encvirtualCardNo);
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void GenQrCode(final String money, final String mark, final String encvirtualCardNo) {
        LogUtil.e("===============开始获取二维码  GenQrCode");
        fixedThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String money1 = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", "");
                    LogUtil.e("准备请求二维码： money:" + money1 + " mark:" + mark);
                    String str2 = "https://pay.95516.com/pay-web/restlet/qr/p2pPay/applyQrCode?txnAmt=" + Enc(money1) + "&cityCode=" + Enc(getcityCd()) + "&comments=" + Enc(mark) + "&virtualCardNo=" + encvirtualCardNo;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(str2).header("X-Tingyun-Id", getXTid()).header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis())
                            .header("sid", getSid()).header("urid", geturid()).header("cityCd", getcityCd()).header("locale", "zh-CN").header("User-Agent", "Android CHSP")
                            .header("dfpSessionId", getDfpSessionId()).header("gray", getgray()).header("key_session_id", "").header("Host", "pay.95516.com").build();
                    Response response = client.newCall(request).execute();
                    if (response != null && response.body() != null) {
                        String RSP = response.body().string();
                        LogUtil.e("获取到的二维码数据：" + str2 + " RSP=>" + RSP);
                        String Rsp = Dec(RSP);
                        LogUtil.e("获取到的二维码数据（解密）：" + Rsp);
                        String url = new JSONObject(Rsp).getJSONObject("params").getString("certificate");

                        Intent intent = new Intent();
                        intent.putExtra("money", money);
                        intent.putExtra("mark", mark);
                        intent.putExtra("qrcode", url);
                        BroadCaseSendUtil.backYunShanFuData(mContext, intent);
                    }
                } catch (Exception e) {
                    LogUtil.e("获取二维码 ：Exception" + e.getMessage());
                }
            }
        });
    }

    /**
     * 检查新订单
     *
     * @param user
     * @param money
     * @return
     */
    private String CheckNewOrder(final String user, final String money) {
        try {
            LogUtil.e("查询订单:" + user + " money:" + money);
            Callable<String> callable = new Callable<String>() {
                public String call() {
                    try {
                        String str2 = "https://wallet.95516.com/app/inApp/order/list?currentPage=" + Enc("1") + "&month=" + Enc("0") + "&orderStatus=" + Enc("0") + "&orderType=" + Enc("A30000") + "&pageSize=" + Enc("10") + "";
                        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(50, TimeUnit.SECONDS).writeTimeout(50, TimeUnit.SECONDS).readTimeout(50, TimeUnit.SECONDS).build();
                        Request request = new Request.Builder().url(str2).header("X-Tingyun-Id", getXTid()).header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis())
                                .header("sid", getSid()).header("urid", geturid()).header("cityCd", getcityCd()).header("locale", "zh-CN")
                                .header("User-Agent", "Android CHSP").header("dfpSessionId", getDfpSessionId())
                                .header("gray", getgray()).header("Accept", "*/*").header("key_session_id", "").header("Host", "wallet.95516.com").build();
                        Response response = client.newCall(request).execute();
                        if (response != null && response.body() != null) {
                            String RSP = response.body().string();
                            LogUtil.e("获取到的订单列表：" + str2 + " RSP=>" + RSP);
                            String DecRsp = Dec(RSP);
                            LogUtil.e("获取到的订单列表(解密订单):" + str2 + " DecRSP=>" + DecRsp);
                            JSONArray o = new JSONObject(DecRsp).getJSONObject("params").getJSONArray("uporders");
                            List<com.alibaba.fastjson.JSONObject> orderResultList = null;
                            for (int i = 0; i < o.length(); i++) {
                                JSONObject p = o.getJSONObject(i);
                                String orderid = p.getString("orderId");
                                LogUtil.e("订单数据:" + p.toString());
                                String amount = p.getString("amount");
                                LogUtil.e("判断金额： " + (amount.equals(money) && p.getString("title").contains(user)));
                                if (amount.equals(money) && p.getString("title").contains(user)) {
                                    LogUtil.e("找到订单，开始获取订单详情： " + orderid);
                                    return "getpayresult:" + DoOrderInfoGet(orderid);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.e("检查支付新订单异常:" + e.getMessage());
                    }
                    return "";
                }
            };

            //发起请求
            Future<String> future = fixedThread.submit(callable);
            String result = future.get();
            LogUtil.e("查询结果: " + result);
            return "5秒重新查询";
        } catch (Exception e) {
            return "ERR:" + e.getLocalizedMessage();
        }
    }

    private String getSid() {
        String sid = "";
        try {
            Object b = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.network.aa", mClassLoader), "b");
            sid = XposedHelpers.callMethod(b, "e").toString();
        } catch (Exception e) {
        }
        LogUtil.e("sid:" + sid + "");
        return sid;
    }

    private String geturid() {
        String Cacheurid = "";
        try {
            Class data_d = XposedHelpers.findClass("com.unionpay.data.d", mClassLoader);
            Object o = XposedHelpers.callStaticMethod(data_d, "a", new Class[]{Context.class}, activity);
            String v1_2 = XposedHelpers.callMethod(XposedHelpers.callMethod(o, "A"), "getHashUserId").toString();
            if (!TextUtils.isEmpty(v1_2) && v1_2.length() >= 15) {
                Cacheurid = v1_2.substring(v1_2.length() - 15);
            }
        } catch (Exception e) {
        }
        LogUtil.e("Cacheurid:" + Cacheurid + "");
        return Cacheurid;
    }

    private String getgray() {
        String cachegray = "";
        try {
            Object b = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.network.aa", mClassLoader), "b");
            cachegray = XposedHelpers.callMethod(b, "d").toString();
        } catch (Exception e) {
        }
        LogUtil.e("Cachegray: " + cachegray + "");
        return cachegray;
    }

    private String getcityCd() {
        String cachecityCd = "";
        try {
            cachecityCd = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.location.a", mClassLoader), "i").toString();
        } catch (Exception e) {
        }
        LogUtil.e("CachecityCd: " + cachecityCd + "");
        return cachecityCd;
    }

    private String getXTid() {
        try {
            Class m_s = XposedHelpers.findClass("com.networkbench.agent.impl.m.s", mClassLoader);
            Object f = XposedHelpers.callStaticMethod(m_s, "f");
            Object h = XposedHelpers.callMethod(f, "H");
            Object i = XposedHelpers.callStaticMethod(m_s, "I");
            Object xtidClass = m_s.getDeclaredMethod("a", String.class, int.class).invoke(null, h, i);
            String xtid = xtidClass.toString();
            return xtid;
        } catch (Exception e) {
        }
        return "";
    }

    private String Enc(String src) {
        try {
            return (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.encrypt.IJniInterface", mClassLoader), "encryptMsg", src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getDfpSessionId() {
        String CacheDfpSessionId = "";
        try {
            Object o = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.service.b", mClassLoader), "d");
            CacheDfpSessionId = o.toString();
        } catch (Exception e) {
        }
        return CacheDfpSessionId;
    }

    private String Dec(String src) {
        try {
            return (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.unionpay.encrypt.IJniInterface", mClassLoader), "decryptMsg", src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 查询订单详情
     *
     * @param orderid
     * @return
     */
    private String DoOrderInfoGet(final String orderid) {
        if (orderid.length() > 5) {
            try {
                Callable<String> callable = new Callable<String>() {
                    public String call() {
                        try {
                            Map<String, Object> dataMap = new HashMap<>();
                            dataMap.put("orderType", 21);
                            dataMap.put("transTp", "simple");
                            dataMap.put("orderId", orderid);
                            String args = JSON.toJSONString(dataMap);//"{\"orderType\":\"21\",\"transTp\":\"simple\",\"orderId\":\"" + orderid + "\"}";
                            String url = "https://wallet.95516.com/app/inApp/order/detail";
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder().url(url).header("X-Tingyun-Id", getXTid())
                                    .header("X-Tingyun-Lib-Type-N-ST", "0;" + System.currentTimeMillis())
                                    .header("sid", getSid()).header("urid", geturid())
                                    .header("cityCd", getcityCd()).header("locale", "zh-CN")
                                    .header("User-Agent", "Android CHSP").header("dfpSessionId", getDfpSessionId())
                                    .header("gray", getgray()).header("Accept", "*/*")
                                    .header("key_session_id", "").header("Content-Type", "application/json; charset=utf-8")
                                    .header("Host", "wallet.95516.com")
                                    .post(RequestBody.create(null, Enc(args))).build();
                            Response response = client.newCall(request).execute();
                            if (response != null && response.body() != null) {
                                String RSP = response.body().string();
                                LogUtil.e("获取订单详情=>" + url + " RSP=>" + RSP);
                                String DecRsp = Dec(RSP);
                                LogUtil.e("获取订单详情（解密）=>" + url + " DecRSP=>" + DecRsp);
                                JSONObject params = new JSONObject(DecRsp).getJSONObject("params");
//                                String orderDetail = params.getString("orderDetail");
//                                mlog("获取订单详情=>" + url + " orderDetail=>" + orderDetail);
//                                JSONObject o = new JSONObject(orderDetail);
//                                String u = o.getString("payUserName");
//                                String mark = o.getString("postScript");
//                                String totalAmount = params.getString("totalAmount");
//                                mlog("获取订单详情（数据）=>" + url + " u:" + u + " mark:" + mark + " totalAmount:" + totalAmount);
//                                Message message = new Message();
//                                message.what = 1;
//                                message.obj = "getpayresult:" + params.toString();
//                                handler.sendMessage(message);
                                LogUtil.e("获取订单详情（数据）发送成功=>: " + params.toString());
                                return params.toString();
                            }
                        } catch (Exception e) {
                        }
                        return "";
                    }
                };
                Future<String> future = fixedThread.submit(callable);
                String result = future.get();
                LogUtil.e("获取订单详情（数据）: " + result);
                if (!TextUtils.isEmpty(result)) {
                    return result;
                }
            } catch (Exception e) {
                return "ERR:" + e.getLocalizedMessage();
            }
        }
        return "ERROR_ORDER:" + orderid;
    }
}
