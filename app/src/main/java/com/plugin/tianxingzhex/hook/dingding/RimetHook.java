package com.plugin.tianxingzhex.hook.dingding;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.json.JSONObject;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tianxingzhe.plugin.utils.Utils.LogUtil;

public class RimetHook {
    public static final String DINGDING_ACTION = "dingding_action";
    public static ClassLoader classLoader;
    public static boolean DINGDING_ISHOOK = false;
    public static Context context;
    public static String currentUid;
    public static String mCid;
    public static Handler handler;
    public static Map<String, JSONObject> taskList = new HashMap<>();

    public static void hook(Context context, final ClassLoader classLoader) {
        RimetHook.context = context;
        RimetHook.classLoader = classLoader;

        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log(param.thisObject.getClass().toString());
                if (param.thisObject.getClass().toString().contains(".activity.HomeActivity")) {
                    if (handler == null) {
                        handler = new Handler() {

                            @Override
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    List<String> re = new ArrayList<>();
                                    for (String key : taskList.keySet()) {
                                        JSONObject object = taskList.get(key);
                                        if (System.currentTimeMillis() - object.optLong("time") > 1000 * 60 * 10) {
                                            re.add(key);
                                        }
                                    }
                                    for (String i : re) {
                                        //除去超过10分钟未支付了
                                        taskList.remove(i);
                                    }
                                    if (taskList.size() > 0) {
                                        query(classLoader);
                                        // handler.sendEmptyMessageDelayed(1, 10000);
                                    }
                                    handler.sendEmptyMessageDelayed(1, 10000);

                                    XposedBridge.log("查询 结果" + taskList.size());

                                }

                            }
                        };
                        handler.sendEmptyMessageDelayed(1, 10000);
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    getcid(classLoader);
                                    getcurrentid(classLoader);

                                } catch (Throwable th) {
                                    th.printStackTrace();
                                }
                            }
                        }).start();
                    }


                }

            }
        });
    }

    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.tools.payhelper.dindingstart")) {
                String type = intent.getStringExtra("type");
                if ("dingding".equals(type)) {
                    String money = intent.getStringExtra("money");
                    String remark = intent.getStringExtra("remark");
                    String orderid = intent.getStringExtra("orderid");
                    createOrder(classLoader, money, remark, orderid, context);
                } else if (type.equals("dingding_query")) {
                    query(context.getClassLoader());
                } else if (type.equals("getuserid")) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                getcid(classLoader);
//                                getcurrentid(classLoader);
                            } catch (Throwable th) {
                                th.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        }
    }


    public static void createOrder(ClassLoader classLoader, String money, String remark, String orderid, Context context) {
        LogUtil.e("createOrder");
        try {
            if (TextUtils.isEmpty(RimetHook.mCid)) {
                LogUtil.e("mCid == NULL");
                return;
            }
            JSONObject object = new JSONObject();
            object.put("money", money);
            object.put("remark", remark);
            object.put("orderid", orderid);
            object.put("time", System.currentTimeMillis());
            taskList.put(remark, object);
            String str5 = (String) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("cdh", classLoader), "a", new Object[0]), "b", new Object[0]);
            ClassLoader classLoader2 = classLoader;
            Object newProxyInstance = Proxy.newProxyInstance(classLoader2, new Class[]{XposedHelpers.findClass("cny", classLoader)}, new CreateCnyProxy(context, orderid));
            Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass("cdi", classLoader), "a", new Object[0]);
            XposedBridge.log("rpc class:" + callStaticMethod.getClass());
            XposedHelpers.callMethod(callStaticMethod, "a", new Object[]{Long.valueOf(Long.parseLong(currentUid)), str5, money, Integer.valueOf(Integer.parseInt("1")), null, Integer.valueOf(Integer.parseInt("0")), mCid, remark, Long.valueOf(Long.parseLong("0")), null, Long.valueOf(Long.parseLong("0")), null, Integer.valueOf(Integer.parseInt("0")), newProxyInstance});

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static void getcurrentid(ClassLoader classLoader) {
        LogUtil.e("getcurrentid");
        try {
            currentUid = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("ckb", classLoader), "a", new Object[0]), "b", new Object[0]), "getCurrentUid", new Object[0]).toString();
            // PayHelperUtils.sendLoginId("钉钉用户:" + currentUid,"dingding",context);
        } catch (Throwable th) {
        LogUtil.e("getcurrentid exception: "+th.getMessage());
            th.printStackTrace();
        }
    }

    public static void getcid(ClassLoader classLoader) {
        LogUtil.e("getcid");
        try {
            String obj = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("ckb", classLoader), "a", new Object[0]), "b", new Object[0]), "getCurrentUid", new Object[0]).toString();
            currentUid = obj;
            XposedBridge.log("listConversations start");
//            LogUtil.e("currentUid" + currentUid);
//            Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.wukong.im.IMEngine", classLoader), "getIMService", new Object[]{XposedHelpers.findClass("com.alibaba.wukong.im.ConversationService", classLoader)});
//            Object newProxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{XposedHelpers.findClass("com.alibaba.wukong.Callback", classLoader)}, new ListConversationsCnyProxy());
//            XposedHelpers.callMethod(callStaticMethod, "listLocalGroupConversations", new Object[]{newProxyInstance, Integer.valueOf(1000)});

        } catch (Throwable th) {
        LogUtil.e("getcid exception=="+th.getMessage());
            th.printStackTrace();
        }
    }

    public static void query(ClassLoader classLoader) {
        try {
            Object newProxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{XposedHelpers.findClass("cny", classLoader)}, new QueryCnyProxy(new QueryCnyImpl(classLoader, handler, context)));
            Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass("cdi", classLoader), "a", new Object[0]);
            XposedBridge.log("rpc class:" + callStaticMethod.getClass());
            XposedHelpers.callMethod(callStaticMethod, "a", new Object[]{Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(50), newProxyInstance});
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

}
