package com.plugin.tianxingzhex.hook.wanxin;

import android.content.Context;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class GetHongBaoCallBackProxy implements InvocationHandler {
    private Context context;
    private String hongbaoid;
    private String orderid;

    public GetHongBaoCallBackProxy(Context context, String orderid, String hongbaoid) {
        this.context = context;
        this.orderid = orderid;
        this.hongbaoid = hongbaoid;
    }

    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        int i = 0;
        XposedBridge.log("invoke GetHongBaoCallBackProxy method: " + method.getName());
        if (objArr != null) {
            XposedBridge.log("GetHongBaoCallBackProxy method params = " + Arrays.toString(objArr));
        }
        if (method.getName().contains("onSuccess")) {
            Object o = objArr[0];
            if (o instanceof Object[]) {
                o = ((Object[]) o)[0];
            }
            Field[] fields = o.getClass().getDeclaredFields();
            int length = fields.length;
            while (i < length) {
                Field f = fields[i];
                f.setAccessible(true);
                XposedBridge.log("  " + f.getName() + " =" + f.get(o));
                i++;
            }
            double amount = XposedHelpers.getDoubleField(o, "amount");
            if (XposedHelpers.getIntField(o, "status") == 2) {
                XposedBridge.log("GetHongBaoCallBackProxy onSuccess " + o);


                //提交服务器
//                Utils.postWangxinHBCode(this.orderid, (0.01d * amount) + "");
                WanXinHook.dataMap.remove(this.hongbaoid);
            }
        }
        if (method.getName().contains("toString")) {
            return getClass() + "@12649";
        }
        return null;
    }
}