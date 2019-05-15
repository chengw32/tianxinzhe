package com.plugin.tianxingzhex.hook.dingding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class OpenCnyProxy implements InvocationHandler {
    private OpenCnyImpl OpenCnyImpl;

    public OpenCnyProxy(OpenCnyImpl openCnyImpl) {
        this.OpenCnyImpl = openCnyImpl;
    }

    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        XposedBridge.log("invoke OpenCnyImpl method: " + method.getName());
        if (method.getName().contains("toString")) {
            return "this is string";
        }
        return XposedHelpers.callMethod(this.OpenCnyImpl, method.getName(), objArr);
    }
}