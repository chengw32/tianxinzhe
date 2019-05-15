package com.plugin.tianxingzhex.hook.alihook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.plugin.tianxingzhex.hook.alihook.collectionOrder.CollectionHelper;
import com.plugin.tianxingzhex.hook.alihook.redpackage.EnvelopeHelper;
import com.plugin.tianxingzhex.hook.alihook.redpackage.EnvelopeHooker;
import com.plugin.tianxingzhex.runnables.GetCollectionLinkRunnable;
import com.plugin.tianxingzhex.server.BroadCaseSendUtil;
import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AliPayHooker {


    public static final String AIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
    public static boolean ALIPAY_PACKAGE_ISHOOK = false;

    //返回支付宝信息的action
    public static final String ACTION_ALI = "action_ali_data_back";

    //支付成功回调
    public static final String PAY_SUCCESS_BACK = "data_pay_success_back";

    //通知服务调起支付宝app
    public static final String SERVICE_ACTION_UP_ALI_APP = "service_action_up_ali_app";
    public static final String MESSAGE_ALI_ACCOUNT_INFO = "message_ali_account_info";
    //获取阿里账号信息
    public static final String ACTION_RECEIVE_GET_ALI_ACCOUNT_INFO = "action_receive_get_ali_account_info";
    //支付宝 红包数据
    public static final String DATA_RED_PACKAGE = "data_red_package_back";

    //支付宝 获取收款单号
    public static final String ACTION_GET_CLIIECTION_NO = "action_get_collection_no";
    //支付宝 返回收款连接
    public static final String COLLECTION_LINK = "date_back_collection_link";
    //支付宝 删除好友
    public static final String ACTION_DEL_FREND = "action_del_frend";

    public static boolean ISBACK = false;

    private static Context mContext;
    private boolean isQrcodeUiOpen;
    private Activity collectionActivity;

    public void hook(final ClassLoader classLoader, final Context mcontext) {
        mContext = mcontext;
        registReceive(mcontext);
        new CollectionHelper(classLoader, mcontext);

        XposedHelpers.findAndHookMethod("com.alipay.mobile.quinox.LauncherActivity", classLoader, "onResume", new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                if (!AliPayHooker.ISBACK) {

                    if (isLogin(classLoader)) {

                        AliPayHooker.ISBACK = true;
                        Intent localIntent = new Intent();
                        localIntent.putExtra(AliPayHooker.MESSAGE_ALI_ACCOUNT_INFO, AlipayUtils.getAliAccountInfo(mContext));
                        BroadCaseSendUtil.backAliData(mcontext, localIntent);

                        openAliQrcode(mcontext);
                    }
                }
            }
        });
        try {

            EnvelopeHooker envelopeHooker = new EnvelopeHooker(mcontext);
            EnvelopeHelper envelopeHelper = new EnvelopeHelper(envelopeHooker);

//
            XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.dao.TradeDao", classLoader), "insertMessageInfo", new XC_MethodHook() {

                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    //向别人发收款单 支付成功后回调 。收款码的支付成功 不走这个数据库
                    String messageInfo = (String) XposedHelpers.callMethod(methodHookParam.args[0], "toString", new Object[0]);
                    Log.e("hooklog", "TradeDao 收到数据：" + messageInfo);
                    Intent intent = new Intent();
                    intent.putExtra(PAY_SUCCESS_BACK, messageInfo);
                    BroadCaseSendUtil.backAliData(mcontext, intent);
                    super.beforeHookedMethod(methodHookParam);
                }
            });

            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRActivity", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    isQrcodeUiOpen = true;
                }
            });
            XposedHelpers.findAndHookMethod("com.alipay.mobile.payee.ui.PayeeQRActivity", classLoader, "onDestroy", new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    isQrcodeUiOpen = false;
                }
            });

            XposedHelpers.findAndHookMethod("com.alipay.android.phone.personalapp.socialpayee.ui.SocialPersonalActivity_", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    collectionActivity = (Activity) param.thisObject;
                    super.afterHookedMethod(param);
                }
            });
            //收款码收到的钱才会走这个数据库 但是没有备注信息需要爬网页
            // 转账或者发出去的收款单走TradeDao数据库 ServiceDao 虽然能收到消息但是是加密的数据
//            XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alipay.android.phone.messageboxstatic.biz.dao.ServiceDao", classLoader), "insertMessageInfo", new XC_MethodHook() {
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    try {
//                        String content = getTextCenter((String) XposedHelpers.callMethod(param.args[0], "toString", new Object[0]), "extraInfo='", "'").replace("\\", "");
//                        Log.e("hooklog", "ServiceDao 收到数据：" + content);
////                        if (!(content.contains("商家服务") || content.contains("收款到账"))) {
////                            if (!content.contains("收钱到账")) {
////                                StringBuilder stringBuilder = new StringBuilder();
////                                stringBuilder.append("截获错误");
////                                stringBuilder.append(content);
////                                Log.e("hooklog", "截获错误 ");
////                                super.beforeHookedMethod(param);
////                            }
////                        }
////                        getTradeInfo(mcontext, getCookieStr(classLoader));
//                    } catch (Exception e) {
//                    }
//                    super.beforeHookedMethod(param);
//                }
//            });

////            收款码界面收到一笔款
//			XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", classLoader), "clone", new XC_MethodHook() {
//				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//					try {
//						String content = (String) XposedHelpers.callMethod(param.args[0], "toString", new Object[0]);
//						Log.e("hooklog","SyncMessage 收到数据："+content);
////                        getTradeInfo(mcontext, getCookieStr(classLoader));
//					} catch (Exception e) {
//					}
//					super.beforeHookedMethod(param);
//				}
//			});
////

//			//收款码界面的 数据监听实时跟新
//			XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alipay.mobile.payee.model.SyncDataSet", classLoader), "update", new XC_MethodHook() {
//				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//					try {
//						String content = (String) XposedHelpers.callMethod(param.args[0], "toString", new Object[0]);
//						Intent broadCastIntent = new Intent();
//						broadCastIntent.setAction(AliPayHooker.ACTION_RECEIVE_MONEY_STATE);
//						broadCastIntent.putExtra("content", content);
//						mcontext.sendBroadcast(broadCastIntent);
//
//					} catch (Exception e) {
//					}
//					super.beforeHookedMethod(param);
//				}
//			});
//			XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alipay.mobile.payee.model.SyncDataSet", classLoader), "add", new XC_MethodHook() {
//				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//					try {
//						String content = (String) XposedHelpers.callMethod(param.args[0], "toString", new Object[0]);
//						Intent broadCastIntent = new Intent();
//						broadCastIntent.setAction(AliPayHooker.ACTION_RECEIVE_MONEY_STATE);
//						broadCastIntent.putExtra("isAdd", 1);
//						broadCastIntent.putExtra("content", content);
//						mcontext.sendBroadcast(broadCastIntent);
//
//					} catch (Exception e) {
//					}
//					super.beforeHookedMethod(param);
//				}
//			});


        } catch (Error e) {
        } catch (Exception e2) {
        }
    }


    public String getCookieStr(ClassLoader appClassLoader) {
        String cookieStr = "";
        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.common.transportext.biz.appevent.AmnetUserInfo", appClassLoader), "getSessionid", new Object[0]);
        Context context = (Context) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.common.transportext.biz.shared.ExtTransportEnv", appClassLoader), "getAppContext", new Object[0]);
        if (context == null) {
//			sendmsg(context, "异常context为空");
            return cookieStr;
        } else if (XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.common.helper.ReadSettingServerUrl", appClassLoader), "getInstance", new Object[0]) != null) {
            return (String) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.common.transport.http.GwCookieCacheHelper", appClassLoader), "getCookie", new Object[]{".alipay.com"});
        } else {
//			sendmsg(context, "异常readSettingServerUrl为空");
            return cookieStr;
        }
    }

    private void openAliQrcode(Context context) {
        if (!isLogin(context.getClassLoader())) return;
        if (isQrcodeUiOpen) return;
        Class<?> aClass = XposedHelpers.findClass("com.alipay.mobile.payee.ui.PayeeQRActivity", context.getClassLoader());
        Intent startAct = new Intent(context, aClass);
        startAct.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startAct);
    }


    public static Boolean isLogin(ClassLoader classLoader) {

        try {
            Class<?> AlipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", classLoader);
            Object o = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(AlipayApplication, "getInstance", new Object[0]), "getMicroApplicationContext", new Object[0]);
            Class<?> userInforHelp = XposedHelpers.findClass("com.alipay.mobile.common.helper.UserInfoHelper", classLoader);
            Object getInstance = XposedHelpers.callStaticMethod(userInforHelp, "getInstance");
            Object getUserInfo = XposedHelpers.callMethod(getInstance, "getUserInfo", o);
            String getSessionId = XposedHelpers.callMethod(getUserInfo, "getSessionId").toString();
            Log.e("hooklog", "SessionId: " + getSessionId);
            return !TextUtils.isEmpty(getSessionId);
        } catch (Exception e) {
            return false;
        }

    }

    public String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
    }


    public void getTradeInfo(final Context context, final String cookie) {
        Log.e("hooklog", "仿 httpGET请求 爬取 订单号");
        long current = System.currentTimeMillis() + 600000;
        long s = current - 864000000;
        String c = getCurrentDate();
        StringBuilder url = new StringBuilder();
        url.append("https://mbillexprod.alipay.com/enterprise/simpleTradeOrderQuery.json?beginTime=");
        url.append(s);
        url.append("&limitTime=");
        url.append(current);
        url.append("&pageSize=20&pageNum=1&channelType=ALL");
//        url = url.toString();
        HttpUtils httpUtils = new HttpUtils(15000);
        httpUtils.configResponseTextCharset("GBK");
        RequestParams params = new RequestParams();
        params.addHeader("Cookie", cookie);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://render.alipay.com/p/z/merchant-mgnt/simple-order.html?beginTime=");
        stringBuilder.append(c);
        stringBuilder.append("&endTime=");
        stringBuilder.append(c);
        stringBuilder.append("&fromBill=true&channelType=ALL");
        params.addHeader("Referer", stringBuilder.toString());
        httpUtils.send(HttpMethod.GET, url.toString(), params, new RequestCallBack<String>() {
            public void onSuccess(ResponseInfo<String> arg0) {
                Log.e("alixx", arg0.result);
                try {
                    JSONArray jsonArray = new JSONObject((String) arg0.result).getJSONObject("result").getJSONArray("list");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("jsonArray=");
                        stringBuilder.append(jsonArray.length());
                        XposedBridge.log(stringBuilder.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String tradeNo = jsonArray.getJSONObject(i).getString("tradeNo");
                            sendDataToService(tradeNo, cookie, context);
                        }
                    }
                } catch (Exception e) {
                }
            }

            public void onFailure(HttpException e, String s) {

            }
        });

    }

    //同步发送广播
    private synchronized void sendDataToService(String tradeNo, String cookie, Context context) {
        Intent broadCastIntent = new Intent();
//		broadCastIntent.setAction(AliPayHooker.TRADENORECEIVED_ACTION);
        broadCastIntent.putExtra("tradeno", tradeNo);
        broadCastIntent.putExtra("cookie", cookie);
        context.sendBroadcast(broadCastIntent);
    }

    public String getTextCenter(String text, String begin, String end) {
        try {
            int b = text.indexOf(begin) + begin.length();
            return text.substring(b, text.indexOf(end, b));
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }



    private HookReceive mReceiver;

    private void registReceive(Context context) {

        if (null != mReceiver) context.unregisterReceiver(mReceiver);
        mReceiver = new HookReceive();

        IntentFilter billReceiverIntentFilter = new IntentFilter();
        billReceiverIntentFilter.addAction(AliPayHooker.ACTION_RECEIVE_GET_ALI_ACCOUNT_INFO);
        billReceiverIntentFilter.addAction(ACTION_GET_CLIIECTION_NO);
        billReceiverIntentFilter.addAction(ACTION_DEL_FREND);
        //打开收款码界面
//		billReceiverIntentFilter.addAction(AliPayHooker.ACTION_OPEN_ALI_QRCODE_UI);
        context.registerReceiver(mReceiver, billReceiverIntentFilter);

    }


    class HookReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) return;
            if (AliPayHooker.ACTION_RECEIVE_GET_ALI_ACCOUNT_INFO.equals(intent.getAction())) {
                //获取 支付宝账号信息
                Intent localIntent = new Intent();
                localIntent.setAction(AliPayHooker.ACTION_ALI);
                localIntent.putExtra(AliPayHooker.MESSAGE_ALI_ACCOUNT_INFO, AlipayUtils.getAliAccountInfo(mContext));
                context.sendBroadcast(localIntent);

            } else if (AliPayHooker.ACTION_GET_CLIIECTION_NO.equals(intent.getAction())) {
                //生成收款单  然后再EnvelopeHelper 监听生成收款码后到后返回服务器

                String mark = intent.getStringExtra("mark");
                String userId = intent.getStringExtra("userId");
                String money = intent.getStringExtra("money");
                if (null != collectionActivity && !collectionActivity.isFinishing()) {
                    //生成收款码的页面对象不为空 而且没有销毁说明生成出错停留在这个页面了 先销毁掉
                    collectionActivity.finish();
                }
                //加入队列 单线程的 如果有多个推送过来保证一条一条处理
                ThreadPoolUtils.getCollectionLink(new GetCollectionLinkRunnable(mark, money, userId, context));


            } else if (ACTION_DEL_FREND.equals(intent.getAction())) {

                DelFrendHelper.del(context.getClassLoader(), intent.getStringExtra("userid"));
            }
//			else if (AliPayHooker.ACTION_OPEN_ALI_QRCODE_UI.equals(intent.getAction())) {
//				//跳转到收款码
//				Log.e("hooklog","打开支付宝收款码界面");
//				openAliQrcode(context);
//			}
        }
    }

}
