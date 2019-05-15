package com.plugin.tianxingzhex.hook.wanxin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import org.json.JSONObject;

import java.lang.reflect.Proxy;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tianxingzhe.plugin.utils.Utils.LogUtil;

/**
 * Created by chenguowu on 2019/4/1.
 */
public class WanXinHook {
    public static String wanxin_package_name = "com.alibaba.mobileim";
        public static String account;
        public static boolean WANXIN_IS_HOOK ;
        public static String appkey = "12621186";
        public static ClassLoader classLoader;
        public static Context context;
        public static Map<String, String> dataMap = new HashMap();
        public MyReceiver myReceiver;

        public static class MyReceiver extends BroadcastReceiver {
            public void onReceive(Context context, final Intent intent) {
                final SimpleDateFormat format = new SimpleDateFormat("yy年MM月dd日 HH点mm分");
                if (intent.getAction().equals("creatHongBao")) {
                    LogUtil.e("creatHongBao");
//                    cntaobaotb308594778
                    createHonginfo(context,123,"cntaobaotb308594778","xxx123");
//                    createQunHonginfo(context,123,"286","xxx123");
                }
//                if (intent.getAction().equals("com.chuxin.socket.ACTION_CONNECT")) {
//                    ArrayList<String> types = intent.getStringArrayListExtra("type");
//                    XposedBridge.log((String) types.get(0));
//                    if (!types.contains("wangxin")) {
//                        return;
//                    }
////                    if (WebSocketUtil.getInstance().ws == null || !WebSocketUtil.getInstance().ws.isOpen()) {
////                        new Thread(new Runnable() {
////                            public void run() {
////                                String ip = intent.getStringExtra("ip");
////                                WebSocketUtil.getInstance().connect(ip);
////                                WebSocketUtil.getInstance().type = "wangxin";
////                                CommonUtil.log(format.format(new Date()) + " 连接 地址 " + ip);
////                            }
////                        }).start();
////                        Toast.makeText(context, " 旺信 连接到 " + intent.getStringExtra("ip"), 1).show();
////                    }
//                }
//                else if (intent.getAction().equals("com.chuxin.socket.ACTION_DISCONNECT")) {
//                    if (WebSocketUtil.getInstance().ws != null) {
//                        WebSocketUtil.getInstance().close = true;
//                        WebSocketUtil.getInstance().ws.sendClose();
//                        XposedBridge.log(format.format(new Date()) + " 断开连接  ");
//                        Toast.makeText(context, "请求断开连接 ", 1).show();
//                    }
//                }
//                else if (intent.getAction().equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {
//                    XposedBridge.log("网络状态" + intent.getAction());
//                    if (((NetworkInfo) intent.getParcelableExtra("networkInfo")).getState() != State.CONNECTED) {
//                        isConnected = false;
//                    }
//                    XposedBridge.log("网络状态 isConnected =" + isConnected);
//                    if (isConnected) {
//                        WebSocketUtil.getInstance().connect();
//                    }
//                } else if (intent.getAction().equals("android.intent.action.TIME_TICK")) {
//                    WebSocketUtil.getInstance().connect();
//                }
            }
        }

        public WanXinHook(final Context context, final ClassLoader classLoader) {
            this.context = context;
            this.classLoader = classLoader;
            XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alibaba.mobileim.lib.presenter.hongbao.HongbaoManager", classLoader), "createHongbao", new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log(" 生成参数 " + Arrays.toString(param.args));
                }
            });
            XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alibaba.mobileim.YWAPI", classLoader), "createIMCore", new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (TextUtils.isEmpty(WanXinHook.account)) {
                        WanXinHook.account = (String) param.args[0];
//                        Utils.saveData(WanXinHook.account, "wxidnickname");
                    }
                }
            });
            XposedHelpers.findAndHookMethod("com.alibaba.mobileim.lib.presenter.message.MessageList", classLoader, "pushMessage", new Object[]{String.class, List.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    LogUtil.e(" 收到消息 " + param.args[0]);
                    List list = (List) param.args[1];
                    for (Object o : list) {
                        if (XposedHelpers.getIntField(o, "msgType") == 211) {
                            long msgId = XposedHelpers.getLongField(o, "msgId");
                            String url = new JSONObject((String) XposedHelpers.getObjectField(o, "content")).optJSONObject("template").optJSONObject("data").optJSONObject("body").optJSONArray("ac").getString(0);
                            XposedBridge.log(" 红包连接 " + url);
                            String urldecode = URLDecoder.decode(url);
                            XposedBridge.log(" 红包连接 decode" + urldecode);
                            Uri base = Uri.parse(urldecode);
                            String ActionExtraParam = base.getQueryParameter("ActionExtraParam");
                            String sender = base.getQueryParameter("sender");
                            String hongbaoId = Uri.parse(ActionExtraParam.replace("wangwang", "http")).getQueryParameter("hongbaoId");
                            if (sender == null) {
                                String s = urldecode.substring(urldecode.indexOf("sender=") + "sender=".length(), urldecode.length());
                                XposedBridge.log("s = " + s);
                                sender = s.split("&")[0];
                            }
                            XposedBridge.log(" 红包信息 =" + hongbaoId + " sender=" + sender);
                            WanXinHook.tryGetHongbao(context, sender, hongbaoId);
                        }
                    }
                }
            }});
            XposedBridge.hookAllMethods(XposedHelpers.findClass("com.alibaba.mobileim.YWAPI", classLoader), "getIMKitInstance", new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }
            });
            XposedHelpers.findAndHookMethod(Activity.class, "onCreate", new Object[]{Bundle.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String Obg = param.thisObject.toString();
                    XposedBridge.log(" obj " + Obg);
                    if (Obg.contains("MainTabActivity")) {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                String accountStr = (String) XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.mobileim.gingko.WangXinApi", classLoader), "getInstance", new Object[0]), "getAccount", new Object[0]), "getAccount", new Object[0]);
                                LogUtil.e(" 账号 " + accountStr);
//                                Utils.saveData(accountStr, "wxid");
                            }
                        }, 1000);
                        Activity activity = (Activity) param.thisObject;
                        WanXinHook.this.myReceiver = new MyReceiver();
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("com.chuxin.socket.ACTION_CONNECT");
                        intentFilter.addAction("com.chuxin.socket.ACTION_DISCONNECT");
                        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                        intentFilter.addAction("creatHongBao");
                        activity.registerReceiver(WanXinHook.this.myReceiver, intentFilter);
                    }
                }
            }});
        }

        public static void createHonginfo(Context context, long money, String receiver, String orderid) {
            final Object WxAccount = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.mobileim.YWAPI", classLoader), "getIMKitInstance", new Object[]{account, appkey}), "getIMCore", new Object[0]), "getWxAccount", new Object[0]);
            final Object HongbaoManager = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.mobileim.lib.presenter.hongbao.HongbaoManager", classLoader), "getInstance", new Object[0]);
            final String createHongbaoId = (String) XposedHelpers.callMethod(HongbaoManager, "createHongbaoId", new Object[]{WxAccount});
            String title = "恭喜发财，大吉大利！";
            final long uuid = ((Long) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.mobileim.channel.util.WXUtil", classLoader), "getUUID", new Object[0])).longValue();
            Class findClass = null;
            try {
                findClass = classLoader.loadClass("com.alibaba.mobileim.channel.event.IWxCallback");
            } catch (ClassNotFoundException e) {
                XposedBridge.log(e);
            }
            final Object param9 = Proxy.newProxyInstance(classLoader, new Class[]{findClass}, new CallBackProxy(context, orderid, money));
            XposedBridge.log(" param9 " + param9);
            if (param9 != null) {
                final long j = money;
                final String str = receiver;
                new Thread(new Runnable() {
                    public void run() {
                        XposedHelpers.callMethod(HongbaoManager, "createHongbao", new Object[]{WxAccount, createHongbaoId, Long.valueOf(j), Integer.valueOf(0), Integer.valueOf(1), "恭喜发财，大吉大利！", str, Long.valueOf(uuid), param9, Integer.valueOf(0)});
                    }
                }).start();
            }
        }

        public static void createQunHonginfo(Context context, long money, String qunid, String orderid) {
            final Object WxAccount = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.mobileim.YWAPI", classLoader), "getIMKitInstance", new Object[]{account, appkey}), "getIMCore", new Object[0]), "getWxAccount", new Object[0]);
            final Object HongbaoManager = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.mobileim.lib.presenter.hongbao.HongbaoManager", classLoader), "getInstance", new Object[0]);
            final String createHongbaoId = (String) XposedHelpers.callMethod(HongbaoManager, "createHongbaoId", new Object[]{WxAccount});
            String title = "恭喜发财，大吉大利！";
            final long uuid = ((Long) XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.mobileim.channel.util.WXUtil", classLoader), "getUUID", new Object[0])).longValue();
            XposedBridge.log("uuid " + uuid);
            Class findClass = null;
            try {
                findClass = classLoader.loadClass("com.alibaba.mobileim.channel.event.IWxCallback");
            } catch (ClassNotFoundException e) {
                XposedBridge.log(e);
            }
            final Object param9 = Proxy.newProxyInstance(classLoader, new Class[]{findClass}, new CallBackProxy(context, orderid, money));
            XposedBridge.log(" param9 " + param9);
            dataMap.put(createHongbaoId, orderid);
            if (param9 != null) {
                final long j = money;
                final String str = qunid;
                new Thread(new Runnable() {
                    public void run() {
                        XposedHelpers.callMethod(HongbaoManager, "createHongbao", new Object[]{WxAccount, createHongbaoId, Long.valueOf(j), Integer.valueOf(2), Integer.valueOf(1), "恭喜发财，大吉大利！", str, Long.valueOf(uuid), param9, Integer.valueOf(0)});
                    }
                }).start();
            }
        }

        //拆红包
        public static void tryGetHongbao(Context context, String sender, String hongbao_id) {
            String orderid = (String) dataMap.get(hongbao_id);
            final Object WxAccount = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.mobileim.YWAPI", classLoader), "getIMKitInstance", new Object[]{account, appkey}), "getIMCore", new Object[0]), "getWxAccount", new Object[0]);
            final Object HongbaoManager = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.mobileim.lib.presenter.hongbao.HongbaoManager", classLoader), "getInstance", new Object[0]);
            Class findClass = null;
            try {
                findClass = classLoader.loadClass("com.alibaba.mobileim.channel.event.IWxCallback");
            } catch (ClassNotFoundException e) {
                XposedBridge.log(e);
            }
            final Object param4 = Proxy.newProxyInstance(classLoader, new Class[]{findClass}, new GetHongBaoCallBackProxy(context, orderid, hongbao_id));
            final String str = sender;
            final String str2 = hongbao_id;
            new Thread(new Runnable() {
                public void run() {
                    XposedHelpers.callMethod(HongbaoManager, "tryGetHongbao", new Object[]{WxAccount, str, str2, param4});
                }
            }).start();
        }
}
