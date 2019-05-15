package com.plugin.tianxingzhex.hook.dingding;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tianxingzhe.plugin.utils.Utils.LogUtil;

public class CreateCnyProxy implements InvocationHandler {
    private String orderId;
    private Context mContext;


    public CreateCnyProxy(Context context, String str) {
        this.orderId = str;
        mContext=context;
    }

    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
       XposedBridge.log("invoke CreateCnyImpl method: " + method.getName());
        if (method.getName().contains("toString")) {
            return "this is string";
        }
        if (method.getName().equals("onDataReceived")){
                try {
                    obj =objArr[0];
                    Intent broadCastIntent = new Intent();
                    JSONObject object =new JSONObject();
                    object.put("orderId", this.orderId);
                    object.put("mark", XposedHelpers.getObjectField(obj, "congratulations").toString());
                    object.put("orderId", this.orderId);
                    object.put("dingdingOrderId", XposedHelpers.getObjectField(obj, "businessId").toString());
                    object.put("redId", XposedHelpers.getObjectField(obj, "clusterId").toString());
                    object.put("orderStr", XposedHelpers.getObjectField(obj, "alipayOrderString").toString());
                    broadCastIntent.putExtra("money", XposedHelpers.getObjectField(obj, "amount").toString());
                    broadCastIntent.putExtra("mark", XposedHelpers.getObjectField(obj, "congratulations").toString());
                    broadCastIntent.putExtra("type", "dingding");
                    broadCastIntent.putExtra("payurl",  object.toString());
                    broadCastIntent.setAction("com.tools.payhelper.qrcodereceived");
                    LogUtil.e("========================payurl: "+object.toString());
                    mContext.sendBroadcast(broadCastIntent);

                }catch (Exception e){
                }

        }
        return null;
    }
}