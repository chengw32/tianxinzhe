package com.plugin.tianxingzhex.hook.wanxin;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import de.robv.android.xposed.XposedBridge;
import tianxingzhe.plugin.utils.Utils.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;

public class CallBackProxy implements InvocationHandler {
    private long amount;
    private Context context;
    private String orderid;

    public CallBackProxy(Context context, String orderid, long amount) {
        this.context = context;
        this.orderid = orderid;
        this.amount = amount;
    }

    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        JSONObject send;
        XposedBridge.log("invoke CallBackProxy method: " + method.getName());
        if (objArr != null) {
            XposedBridge.log("CallBackProxy method params = " + Arrays.toString(objArr));
        }
        if (method.getName().contains("onError")) {
            send = new JSONObject();
            send.put("action", "pay_url");
            send.put("pay_url", objArr[1]);
            send.put("money", (((double) this.amount) * 0.01d) + "");
            send.put("id", this.orderid);
            LogUtil.e("onError orderid: "+orderid);
//            send.put("app_id", SocketActivity.readConfigFromFile("wangxinid", ""));
//            WebSocketUtil.getInstance().sendText(send.toString());
        }
        if (method.getName().contains("onSuccess")) {
//            String re = new Gson().toJson(objArr[0]);
            String re = JSON.toJSONString(objArr[0]);
            LogUtil.e("onSuccess "+re);
            XposedBridge.log(" onSuccess " + re);
            JSONObject object = (JSONObject) new JSONArray(re).get(0);
            JSONObject alipayParam = object.getJSONObject("alipayParam");
            String hongbaoId = object.optString("hongbaoId");
            String url = alipayParam.optString("url");
            XposedBridge.log("url =" + url + " hongbaoId =" + hongbaoId);
            XposedBridge.log("URLDecoder :" + URLDecoder.decode(url));
//            send = new JSONObject();
//            send.put("action", "pay_url");
//            send.put("pay_url", BASE64Utils.encodeBase64(url));
//            send.put("money", (((double) this.amount) * 0.01d) + "");
//            send.put("id", this.orderid);
//            send.put("app_id", SocketActivity.readConfigFromFile("wangxinid", ""));
//            WebSocketUtil.getInstance().sendText(send.toString());
        }
        if (method.getName().contains("toString")) {
            return getClass() + "@12549";
        }
        return null;
    }
}