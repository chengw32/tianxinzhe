package com.plugin.tianxingzhex.hook.dingding;

import android.content.Context;
import android.os.Handler;

import java.lang.reflect.Proxy;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;


public class SendMsgThread extends Thread {
    private ClassLoader classLoader;
    private Object redPacketsClusterObject;
    private Handler handler;
    private Context context;

    public SendMsgThread(ClassLoader classLoader, Object obj, Handler handler, Context context) {
        this.classLoader = classLoader;
        this.redPacketsClusterObject = obj;
        this.  handler =handler;
        this.context=context;
    }

    public void run() {
        try {
           XposedBridge.log("重新发送消息开始");
            Object callStaticMethod = XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alibaba.wukong.im.IMEngine", this.classLoader), "getIMService", new Object[]{XposedHelpers.findClass("com.alibaba.wukong.im.ConversationService", this.classLoader)});
            Class findClass = XposedHelpers.findClass("com.alibaba.wukong.Callback", this.classLoader);
            Object newProxyInstance = Proxy.newProxyInstance(this.classLoader, new Class[]{findClass}, new ConversationCnyProxy(new ConversationCnyImpl(this.classLoader, this.redPacketsClusterObject)));
            XposedHelpers.callMethod(callStaticMethod, "getConversation", new Object[]{newProxyInstance, XposedHelpers.getObjectField(this.redPacketsClusterObject, "cid").toString()});
           XposedBridge.log("重新发送消息结束");
//            JSONObject jsonObject =new JSONObject(new Gson().toJson(redPacketsClusterObject));
//            final String remark =jsonObject.optString("congratulations");
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    JSONObject object =  RimetHook.taskList.get(remark);
//
//                    if (object!=null){
//                        Intent broadCastIntent = new Intent();
//                        broadCastIntent.putExtra("bill_no", object.optString("orderid"));
//                        broadCastIntent.putExtra("bill_money", object.optString("money"));
//                        broadCastIntent.putExtra("bill_mark", object.optString("remark"));
//                        broadCastIntent.putExtra("bill_type", "dingding");
////                        broadCastIntent.setAction(BILLRECEIVED_ACTION);
//                        context.sendBroadcast(broadCastIntent);
//                    }
//
//                }
//            });
        } catch (Throwable th) {
           XposedBridge.log("重新发送异常");
            th.printStackTrace();
        }
    }
}