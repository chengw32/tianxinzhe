package com.plugin.tianxingzhex.server;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.plugin.tianxingzhex.application.MyApplication;
import com.plugin.tianxingzhex.beans.ReceiveOrderBean;
import com.plugin.tianxingzhex.hook.alihook.AliPayHooker;
import com.plugin.tianxingzhex.hook.yunshangfu.YunShanFuHook;

import tianxingzhe.plugin.utils.Utils.LogUtil;

/**
 * Created by chenguowu on 2019/3/9.
 */
public class PushIntentService extends GTIntentService {
    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        // 透传消息的处理，详看SDK demo
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();
        Log.e("hooklog", "clintId: " + cid);

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Intent intent = new Intent();
            ReceiveOrderBean receiveOrderBean = JSON.parseObject(data, ReceiveOrderBean.class);
            String type = receiveOrderBean.type;
            if ("uppay".equals(type)) {
                //云闪付获取收款码
                intent.setAction(YunShanFuHook.ACTION_GET_CODE);
                intent.putExtra("money", receiveOrderBean.amount);
                intent.putExtra("mark", receiveOrderBean.orderno);
            } else if ("alipay_zzb".equals(type)) {
                //支付宝生成收款单
                LogUtil.e("去获取收款码链接: 参数： money: " + receiveOrderBean.amount + " mark: " + receiveOrderBean.orderno + " userId: " + receiveOrderBean.alipay_pid);
                Intent localIntent = new Intent();
                localIntent.putExtra("money", receiveOrderBean.amount);
                localIntent.putExtra("mark", receiveOrderBean.orderno);
                localIntent.putExtra("userId", receiveOrderBean.alipay_pid);
                localIntent.setAction(AliPayHooker.ACTION_GET_CLIIECTION_NO);
                MyApplication.context.sendBroadcast(localIntent);
            }
            sendBroadcast(intent);
            LogUtil.e("receiver message = " + data);

        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, " PushIntentService onReceiveClientId -> " + "clientid = " + clientid);
        LogUtil.e( " PushIntentService onReceiveClientId -> " + "clientid = " + clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
    }

}
