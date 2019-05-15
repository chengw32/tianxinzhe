package com.plugin.tianxingzhex.hook.dingding;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ConversationCnyProxy implements InvocationHandler {
    private ConversationCnyImpl ConversationCnyImpl;

    public ConversationCnyProxy(ConversationCnyImpl conversationCnyImpl) {
        this.ConversationCnyImpl = conversationCnyImpl;
    }

    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        XposedBridge.log("invoke ConversationCnyImpl method: " + method.getName());
        if (method.getName().contains("toString")) {
            return "this is string";
        }
        return XposedHelpers.callMethod(this.ConversationCnyImpl, method.getName(), objArr);
    }
}