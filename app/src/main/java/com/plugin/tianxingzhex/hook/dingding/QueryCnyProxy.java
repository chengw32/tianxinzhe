package com.plugin.tianxingzhex.hook.dingding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class QueryCnyProxy implements InvocationHandler {
    private QueryCnyImpl QueryCnyImpl;

    public QueryCnyProxy(QueryCnyImpl queryCnyImpl) {
        this.QueryCnyImpl = queryCnyImpl;
    }

    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        XposedBridge.log("invoke QueryCnyProxy method: " + method.getName());
        if (method.getName().contains("toString")) {
            return "this is string";
        }
        return XposedHelpers.callMethod(this.QueryCnyImpl, method.getName(), objArr);
    }
}