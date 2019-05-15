package com.plugin.tianxingzhex.hook.dingding;


import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ConversationCnyImpl {
    private ClassLoader classLoader;
    private Object redPacketsClusterObject;

    public ConversationCnyImpl(ClassLoader classLoader, Object obj) {
        this.classLoader = classLoader;
        this.redPacketsClusterObject = obj;
    }

    void onException(String str, String str2) {
        XposedBridge.log("ConversationCnyImpl onException " + str + " ##### " + str2);
    }

    void onProgress(Object obj, int i) {
        XposedBridge.log("ConversationCnyImpl onProgress");
    }

    void onSuccess(Object obj) {
        try {
            XposedBridge.log("ConversationCnyImpl onSuccess");
            Object o = XposedHelpers.newInstance(XposedHelpers.findClass("com.alibaba.android.dingtalk.redpackets.fragments.SendRandomRedPacketsFragment", this.classLoader), new Object[0]);
            XposedHelpers.setObjectField( o,"a", obj);
            XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.android.dingtalk.redpackets.fragments.SendFragment", this.classLoader), "a", new Object[]{o, this.redPacketsClusterObject});
            XposedBridge.log("ConversationCnyImpl onSuccess end");
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}