package com.plugin.tianxingzhex.hook.wechat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.zxing.common.StringUtils;
import com.plugin.tianxingzhex.beans.WechatInfoBean;
import com.plugin.tianxingzhex.hook.StaticConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;


/**
 * Created by chenguowu on 2019/1/16.
 */

public class WeChatHooker {
    public static final String WECHAT_ACTION = "wechat_data_action";
    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";

    //返回微信id
    public static final String ACTION_WECHAT_ID = "action_wechat_id";
    public static final String ACTION_WECHAT_DATA_BACK = "action_wechat_data_back";
    public static final String ACTION_WECHAT_URL_BACK = "action_wechat_url_back";
    //激活微信在线
    public static final String ACTION_OPEN_WECHAT_QRCODE_UI = "action_get_wechat_qrcode_ui";

    public static final String ACTION_GET_WECGAT_ID = "action_get_wechat_id";
    public static final String ACTION_SAVE_HEAD_URL = "action_save_head_url";

    //收到款后信息返回
    public static final String DATA_WECHAT_DATA_BACK = "data_wechat_data_back";
    //收款码
    public static final String DATA_WECHAT_DATA_BACK_URL = "data_wechat_data_back_url";

    //定额定备注收款款码
    public static final String DATA_WECHAT_DATA_BACK_CUSTOM_URL = "data_wechat_data_back_custom_url";
    //返回的 action
    public static final String ACTION_WECHAT_BACK_CUSTOM_URL = "action_wechat_back_custom_url";
    //来获取的 action
    public static final String ACTION_WECHAT_GET_CUSTOM_URL = "action_wechat_get_custom_url";
    public static final String ACTION_GET_WECHAT_ACCOUNT_INFO = "action_get_wechat_account_info";

    public static final String WE_NET_REQ_CLASS = "com.tencent.mm.plugin.collect.b.s";
    public static final String WE_G_CLAZZ = "com.tencent.mm.kernel.g";
    public static final String WE_L_CLAZZ = "com.tencent.mm.ab.l";
    public static final String APP_ID = "app_id";
    public static final String APP_TRADENO = "trade_no";

    public static boolean WECHAT_PACKAGE_ISHOOK = false;

    //微信启动的第一次的时候自动跳转
    public static boolean FIRST_TIMES = true;
    public static boolean isQrcodeUiOpen;

    private ClassLoader mClassLoader;
    private Long appId;
    private String tradeNo;

    private Object sqlInterface;

    private XC_MethodHook xc_methodHookReceiverMiui = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            String cls_name = (String) param.args[0];
            Log.e("hooklog", "className: " + cls_name);
            if (cls_name.equals("com.unionpay.push.UPPushService")) {
            }
        }
    };

    public void hook(ClassLoader classLoader, final Context context) {

//        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, xc_methodHookReceiverMiui);

        mClassLoader = classLoader;
        registReceive(context);

        try {
            XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", classLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {


//                    ContentValues contentValues = (ContentValues) methodHookParam.args[2];
//                    String str = (String) methodHookParam.args[0];
//                    if (!TextUtils.isEmpty(str)) {
//                        if (str.equals("message")) {
//                            Integer asInteger = contentValues.getAsInteger("type");
//                            if (asInteger != null && asInteger.intValue() == 318767153) {
//                                XposedBridge.log("dspay-xposed-wechat: 微信收款通知 start");
//                                JSONObject jSONObject = new JSONObject(getWechatOrder(context, new XmlToJson.Builder(contentValues.getAsString("content")).build().getJSONObject("msg").getJSONObject("appmsg").toString()));
////                                str = jSONObject.getString(AppConst.KEY_MSG_MONEY);
////                                String string = jSONObject.getString(AppConst.KEY_MSG_MARK);
////                                String string2 = jSONObject.getString(AppConst.KEY_MSG_PAYEE);
////                                String string3 = jSONObject.getString(AppConst.KEY_MSG_TIME);
////                                StringBuilder stringBuilder = new StringBuilder();
////                                stringBuilder.append("dspay-xposed-wechat: 收到微信收款通知，金额=");
////                                stringBuilder.append(str);
////                                stringBuilder.append("备注=");
////                                stringBuilder.append(string);
////                                stringBuilder.append("店长=");
////                                stringBuilder.append(string2);
////                                stringBuilder.append("时间=");
////                                stringBuilder.append(string3);
////                                XposedBridge.log(stringBuilder.toString());
////                                Intent intent = new Intent();
////                                intent.putExtra(AppConst.KEY_MSG_TYPE, AppConst.TYPE_VALUE_WECHAT);
////                                intent.putExtra(AppConst.KEY_MSG_MONEY, str);
////                                intent.putExtra(AppConst.KEY_MSG_MARK, string);
////                                intent.putExtra(AppConst.KEY_MSG_TIME, string3);
////                                intent.putExtra(AppConst.KEY_MSG_PAYEE, string2);
////                                intent.setAction(AppConst.RECEIVER_BILL_ACTION);
////                                context.sendBroadcast(intent);
////                                XposedBridge.log("dspay-xposed-wechat: 微信收款通知 end");
//                            }
//                        }
//                    }


//
                    Object str1 = methodHookParam.args[0];
                    Object str2 = methodHookParam.args[1];
                    ContentValues contentValues = (ContentValues) methodHookParam.args[2];

                    //type    2001 红包 2000 转账 5 收款码
                    Log.e("hooklog", "contentValues: " + contentValues);
                    Log.e("hooklog", "str1: " + str1);
                    Log.e("hooklog", "str2: " + str2);

                    //微信收款
//                收款金额￥0.33
//                汇总今日第1笔收款, 共计￥0.33
//                说明已存入店长___占位置_______________________(**武)的零钱 source=微信收款助手 title=[店员消息]收款到账0.33元 type=5
                    //收到钱通知
                    Integer type = contentValues.getAsInteger("type");
                    if (type != null && type == 318767153) {
                        //html 格式的数据
                        String content = contentValues.getAsString("content");
                        String msgId = contentValues.getAsString("msgId");
                        int i = content.indexOf("<pub_time>") + "<pub_time>".length();
                        int end = content.indexOf("</pub_time>");
                        String substring = content.substring(i, end);
                        Log.e("hooklog", "------------time: " + substring);
                        StaticConstants.msgIdAndTime.put(msgId, substring);

                    }
                    if (type != null && type == 5) {

                        //获取出金额
                        String description = contentValues.getAsString("description");
                        int startIndex = description.indexOf("￥") + 1;
                        int endIndex = description.indexOf(".") + 3;
                        String money = description.substring(startIndex, endIndex);
                        Log.e("hooklog", "money: " + money);


                        //取出时间
                        String msgId = contentValues.getAsString("msgId");
                        String payTime;
                        if (StaticConstants.msgIdAndTime.containsKey(msgId)) {
                            payTime = StaticConstants.msgIdAndTime.get(msgId);
                        } else {
                            payTime = System.currentTimeMillis() + "";
                        }
                        Log.e("hooklog", "time: " + payTime);


                        //店长
                        int boss_start = description.indexOf("店长");
                        int boss_end = description.indexOf("(");
                        if (boss_start >= 0 && boss_end >= 0) {
                            String bossName = description.substring(boss_start + 2, boss_end);
                            Log.e("hooklog", "bossName: " + bossName);
                            Intent localIntent = new Intent();
                            localIntent.setAction(WECHAT_ACTION);
                            localIntent.putExtra("money",money);
                            localIntent.putExtra("time",payTime);
                            localIntent.putExtra("bossName",bossName);
                            localIntent.putExtra("msgId",msgId);
                            context.sendBroadcast(localIntent);
                        }


                    }

                }
            });


            //微信收款码返回

            XposedHelpers.findAndHookMethod(WE_NET_REQ_CLASS, mClassLoader, "a", int.class, String.class, JSONObject.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
//                            {"retcode":"0","retmsg":"ok","pay_url":"wxp:\/\/f2f1ySgDToGThRLadO3pAA_RB2ZK5gDkOgo1"}
                            JSONObject jsonObject = (JSONObject) param.args[2];
                            String pay_url = jsonObject.getString("pay_url");
                            Log.e("wxxxx", "pay_url=" + pay_url);
                            //定额定备注的收款码返回广播
                            Intent urlIntent = new Intent();
                            urlIntent.setAction(ACTION_WECHAT_BACK_CUSTOM_URL);
                            urlIntent.putExtra(DATA_WECHAT_DATA_BACK_CUSTOM_URL, pay_url);
                            urlIntent.putExtra(APP_ID, appId);
                            urlIntent.putExtra(APP_TRADENO, tradeNo);
                            context.sendBroadcast(urlIntent);
                        }
                    });

//
//            //首次打开app时延迟一秒跳转到收款码页面
//            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.LauncherUI", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
//                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//
//                    openWechatQrcodeUi(context);
//
//                }
//            });
            //首次打开app时延迟一秒跳转到收款码页面
//            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.collect.ui.CollectMainUI", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
//                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//
//                    String hYg = XposedHelpers.getObjectField(methodHookParam.thisObject, "hYg").toString();
//                    Log.e("hooklog", "收款码：" + hYg);
//                    //返回收款码与账号信息
//                    String weChatLoginId = getWeChatLoginInfo(context);
//                    WechatInfoBean wechatInfoBean = JSON.parseObject(weChatLoginId, WechatInfoBean.class);
//                    wechatInfoBean.setCollectionCode(hYg);
//                    Intent localIntent = new Intent();
//                    localIntent.setAction(WeChatHooker.ACTION_WECHAT_URL_BACK);
//                    localIntent.putExtra(DATA_WECHAT_DATA_BACK_URL, JSON.toJSONString(wechatInfoBean));
//                    context.sendBroadcast(localIntent);
//
//                }
//            });
            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.collect.ui.CollectMainUI", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    isQrcodeUiOpen = true;
                }
            });
            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.collect.ui.CollectMainUI", classLoader, "onDestroy", new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    isQrcodeUiOpen = false;
                }
            });


        } catch (Exception e) {
            Log.e("hooklog", "Exception :" + e.getMessage());
        }
    }

//    public static String getWechatOrder(Context context, String str) {
//        JSONObject jSONObject = new JSONObject();
//        String str2 = "";
//        String str3 = "";
//        Object obj = "";
//        JSONObject parseObject = JSON.parseObject(str);
//        JSONObject jSONObject2 = parseObject.getJSONObject("mmreader").getJSONObject("template_detail").getJSONObject("line_content");
//        JSONObject jSONObject3 = parseObject.getJSONObject("mmreader").getJSONObject("template_detail").getJSONObject("opitems");
//        Object replace = jSONObject2.getJSONObject("topline").getJSONObject("value").getString("word").replace("￥", BuildConfig.FLAVOR);
//        JSONArray jSONArray = jSONObject2.getJSONObject("lines").getJSONArray("line");
//        Object obj2 = str3;
//        Object obj3 = str2;
//        for (int i = 0; i < jSONArray.size(); i++) {
//            JSONObject jSONObject4 = jSONArray.getJSONObject(i);
//            String string = jSONObject4.getJSONObject("key").getString("word");
//            String string2 = jSONObject4.getJSONObject("value").getString("word");
//            if (string.contains("备注")) {
//                obj3 = string2;
//            }
//            if (string.contains("说明") || string2.contains("已存入")) {
//                obj2 = StringUtils.getTextCenter(string2, "店长", "的");
//            }
//        }
//        JSONArray jSONArray2 = jSONObject3.getJSONArray("opitem");
//        for (int i2 = 0; i2 < jSONArray2.size(); i2++) {
//            String string3 = jSONArray2.getJSONObject(i2).getString("weapp_path");
//            if (string3.contains("timestamp")) {
//                obj = StringUtils.getTextCenter(string3, "timestamp=", "&");
//            }
//        }
//        if (StringUtils.isEmpty(obj)) {
//            obj = parseObject.getJSONObject("mmreader").getJSONObject("category").getJSONObject("item").getString("pub_time");
//        }
//        jSONObject.put("money", replace);
//        jSONObject.put(AppConst.KEY_MSG_MARK, obj3);
//        jSONObject.put(AppConst.KEY_MSG_PAYEE, obj2);
//        jSONObject.put(AppConst.KEY_MSG_TIME, obj);
//        return jSONObject.toJSONString();
//    }


    private WechatReceive mReceiver;

    private void registReceive(Context context) {
        if (null != mReceiver) context.unregisterReceiver(mReceiver);
        mReceiver = new WechatReceive();

        IntentFilter billReceiverIntentFilter = new IntentFilter();
        billReceiverIntentFilter.addAction(ACTION_OPEN_WECHAT_QRCODE_UI);
        //MyService来获取微信号
        billReceiverIntentFilter.addAction(ACTION_GET_WECGAT_ID);
        billReceiverIntentFilter.addAction(ACTION_WECHAT_GET_CUSTOM_URL);
        //获取微信账号信息
        billReceiverIntentFilter.addAction(ACTION_GET_WECHAT_ACCOUNT_INFO);
        context.registerReceiver(mReceiver, billReceiverIntentFilter);
    }

    public void getQRCode(float money, String mark) {
        Log.e("hooklog", "getQRCode");
        try {

            Class<?> aClass = XposedHelpers.findClass(WE_NET_REQ_CLASS, mClassLoader);
            Log.e("hooklog", "hook微信url");
            Log.e("hooklog", "aClass " + aClass);
            Constructor constructor = aClass.getConstructor(double.class, String.class, String.class);
            Object reqInstance = constructor.newInstance(money, "1", mark);
            Class<?> gClazz = mClassLoader.loadClass(WE_G_CLAZZ);
            Method ehMethod = gClazz.getDeclaredMethod("Eh");
            ehMethod.setAccessible(true);
            Object ehInstance = ehMethod.invoke(null);
            Field field = ehInstance.getClass().getDeclaredField("dpP");
            field.setAccessible(true);
            Object dpP = field.get(ehInstance);
            Class<?> lClazz = mClassLoader.loadClass(WE_L_CLAZZ);
            Method aMethod = dpP.getClass().getDeclaredMethod("a", lClazz, int.class);
            aMethod.setAccessible(true);
            aMethod.invoke(dpP, reqInstance, 0);
            Log.e("wxxxx", "invoke");
        } catch (Exception e) {
            Log.e("wxxxx", "Exception");
        }
    }

    class WechatReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) return;
            if (ACTION_OPEN_WECHAT_QRCODE_UI.equals(intent.getAction())) {
                openWechatQrcodeUi(context);
            } else if (ACTION_WECHAT_GET_CUSTOM_URL.equals(intent.getAction())) {
                Log.e("hooklog", "shoudao xiaoxi");
                if (null != mClassLoader) {
                    Log.e("hooklog", "class loader != null");
                    float money = intent.getFloatExtra("money", (float) 0.01);
                    String mark = intent.getStringExtra("mark");
                    appId = intent.getLongExtra(APP_ID, 0L);
                    tradeNo = intent.getStringExtra(APP_TRADENO);
//					Logger.e("tradeNo" + tradeNo);
                    getQRCode(money, mark);
                }
            } else if (ACTION_GET_WECHAT_ACCOUNT_INFO.equals(intent.getAction())) {
                //返回微信账号信息 MyService 收到后会激活微信在线
                String wechatInfo = getWeChatLoginInfo(context);
                Intent localIntent = new Intent();
                localIntent.setAction(WeChatHooker.ACTION_WECHAT_URL_BACK);
                localIntent.putExtra(DATA_WECHAT_DATA_BACK_URL, wechatInfo);
                context.sendBroadcast(localIntent);
            }
        }
    }

    private void openWechatQrcodeUi(Context context) {
        if (isQrcodeUiOpen) return;
        try {
            Class<?> aClass = XposedHelpers.findClass("com.tencent.mm.plugin.collect.ui.CollectMainUI", context.getClassLoader());
            Intent startAct = new Intent(context, aClass);
            startAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startAct);
        } catch (Exception e) {
            Log.e("hooklog", "openCollectMainUI: Exception " + e.getMessage());
        }
    }

    public static String getWeChatLoginInfo(Context context) {
        try {
//
            SharedPreferences sharedPreferences = context.getSharedPreferences("com.tencent.mm_preferences", 0);
            String login_user_name = sharedPreferences.getString("login_user_name", "");
            String login_weixin_username = sharedPreferences.getString("login_weixin_username", "");
            String last_login_uin = sharedPreferences.getString("last_login_uin", "");

            Log.e("xxxx", login_user_name + "-" + login_weixin_username + "-" + last_login_uin);

            WechatInfoBean wechatInfoBean = new WechatInfoBean();
            wechatInfoBean.setWechatLoginId(login_user_name);
            wechatInfoBean.setWechatName(login_weixin_username);

            return JSON.toJSONString(wechatInfoBean);
        } catch (Exception e) {
            return "";
        }
    }


}
