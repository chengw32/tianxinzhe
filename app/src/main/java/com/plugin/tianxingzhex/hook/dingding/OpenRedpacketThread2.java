package com.plugin.tianxingzhex.hook.dingding;

import java.lang.reflect.Proxy;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class OpenRedpacketThread2 extends Thread {
    private ClassLoader classLoader;
    private String clusterid;
    private int errNum;
    private String sender;

    public OpenRedpacketThread2(ClassLoader classLoader, String str, String str2, int i) {
        this.classLoader = classLoader;
        this.sender = str;
        this.clusterid = str2;
        this.errNum = i;
    }

    public void run() {
        try {
            if (this.errNum > 0) {
                Thread.sleep(3000);
            }
            if (this.errNum <= 2) {
               XposedBridge.log("catch re package start");
                Class findClass = XposedHelpers.findClass("cny", this.classLoader);
                Object newProxyInstance = Proxy.newProxyInstance(this.classLoader, new Class[]{findClass}, new OpenCnyProxy(new OpenCnyImpl(this.classLoader, this.sender, this.clusterid, this.errNum)));
                Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass("cdi", this.classLoader), "a", new Object[0]);
               XposedBridge.log("rpc class:" + callStaticMethod.getClass());
                XposedHelpers.callMethod(callStaticMethod, "a", new Object[]{Long.valueOf(Long.parseLong(this.sender)), this.clusterid, Boolean.valueOf(false), newProxyInstance});
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}