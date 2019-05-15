package com.plugin.tianxingzhex.hook.dingding;

import android.content.Intent;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class OpenCnyImpl {
    private ClassLoader classLoader;
    private String clusterid;
    private int errNum;
    private String sender;

    public OpenCnyImpl(ClassLoader classLoader, String str, String str2, int i) {
        this.classLoader = classLoader;
        this.sender = str;
        this.clusterid = str2;
        this.errNum = i;
    }

    void onDataReceived(Object obj) {
        try {
            if (XposedHelpers.getObjectField(obj, "pickStatus").toString().equals("1")) {
                XposedBridge.log("catch re package ok");
                Object objectField = XposedHelpers.getObjectField(obj, "redEnvelopCluster");
                String obj2 = XposedHelpers.getObjectField(objectField, "businessId").toString();
                String obj3 = XposedHelpers.getObjectField(objectField, "amount").toString();
                String obj4 = XposedHelpers.getObjectField(objectField, "congratulations").toString();
                String obj5 = XposedHelpers.getObjectField(objectField, "clusterId").toString();
                Intent intent = new Intent();
                intent.setAction("com.paysdog.www.hook");
                intent.putExtra("type", "dingding_success");
                intent.putExtra("redId", obj5);
                intent.putExtra("money", obj3);
                intent.putExtra("remark", obj4);
                intent.putExtra("tradeNo", obj2);
               RimetHook.context.sendBroadcast(intent);
                XposedBridge.log("open red package ok");
                return;
            }
            OpenRedpacketThread2 openRedpacketThread2 = new OpenRedpacketThread2(this.classLoader, this.sender, this.clusterid, this.errNum + 1);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    void onException(String str, String str2) {
        XposedBridge.log("OpenCnyImpl onException");
        OpenRedpacketThread2 openRedpacketThread2 = new OpenRedpacketThread2(this.classLoader, this.sender, this.clusterid, this.errNum + 1);
    }

    void onProgress(Object obj, int i) {
        XposedBridge.log("OpenCnyImpl onProgress");
    }
}