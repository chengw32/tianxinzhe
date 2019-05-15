package com.plugin.tianxingzhex.hook.dingding;

import android.content.Context;
import android.os.Handler;

import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class QueryCnyImpl {
    private ClassLoader classLoader;
    private Handler handler;
    private Context context;

    public QueryCnyImpl(ClassLoader classLoader, Handler handler, Context context) {
        this.classLoader = classLoader;
        this. context =context;
        this.handler =handler;
    }

    void onDataReceived(Object obj) {

        if (obj != null) {
            try {
                if (XposedHelpers.getObjectField(obj, "mSentList") != null) {
                  List list=     (List) XposedHelpers.getObjectField(obj, "mSentList");
                    for (Object next : list) {
                        XposedHelpers.getObjectField(next, "alipayStatus").toString();
                        XposedHelpers.getObjectField(next, "businessId").toString();
                        XposedHelpers.getObjectField(next, "congratulations").toString();
                        XposedHelpers.getObjectField(next, "amount").toString();
                        String obj2 = XposedHelpers.getObjectField(next, "status").toString();
                        String obj3 = XposedHelpers.getObjectField(next, "sender").toString();
                        String obj4 = XposedHelpers.getObjectField(next, "clusterId").toString();
                        String congratulations = XposedHelpers.getObjectField(next,"congratulations").toString();
                        if (obj2.equals("1") || obj2.equals("0")) {
                            new SendMsgThread(this.classLoader, next,handler,context).start();
                        } else if (obj2.equals("2")) {
                            new OpenRedpacketThread2(this.classLoader, obj3, obj4, 0).start();
                            RimetHook.taskList.remove(congratulations);
                        }
                    }
                  XposedBridge.log("QueryCnyImpl onDataReceived"+list.size());
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    void onException(String str, String str2) {
      XposedBridge.log("QueryCnyImpl onException");
    }

    void onProgress(Object obj, int i) {
      XposedBridge.log("QueryCnyImpl onProgress");
    }
}