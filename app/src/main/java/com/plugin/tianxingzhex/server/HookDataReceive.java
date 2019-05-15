package com.plugin.tianxingzhex.server;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushManagerReceiver;
import com.plugin.tianxingzhex.beans.UpYunShanFuQRBean;
import com.plugin.tianxingzhex.hook.alihook.AliPayHooker;
import com.plugin.tianxingzhex.hook.alihook.utils.AliDataAnalysis;
import com.plugin.tianxingzhex.hook.alihook.utils.HookModul;
import com.plugin.tianxingzhex.hook.wechat.WeChatHooker;
import com.plugin.tianxingzhex.hook.yunshangfu.YunShanFuHook;
import com.plugin.tianxingzhex.runnables.SMSRunnable;
import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

import tianxingzhe.plugin.utils.Utils.LogUtil;
import tianxingzhe.plugin.utils.Utils.SPUtil;

/**
 * Created by chenguowu on 2019/3/10.
 */
public class HookDataReceive extends PushManagerReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("hooklog：-------------------- onReceive  intent " + intent.getAction());
        if (null == intent) return;
        if (intent.getAction().equals(WeChatHooker.WECHAT_ACTION)) {

            intent.setClass(context, PluginServer.class);
            context.startService(intent);
            return;
        }
        if (intent.getAction().equals(AliPayHooker.ACTION_ALI)) {
            //红包信息
            String stringExtra = intent.getStringExtra(AliPayHooker.DATA_RED_PACKAGE);
            if (!TextUtils.isEmpty(stringExtra)) {
                LogUtil.e("红包已拆：" + stringExtra);
                Intent redPackageIntent = new Intent(context, PluginServer.class);
                redPackageIntent.setAction(PluginServer.SERVICE_UP_REDPACAGE_DATA);
                redPackageIntent.putExtra("data", stringExtra);
                context.startService(redPackageIntent);
                return;
            }
            //账号信息
            String accountInfo = intent.getStringExtra(AliPayHooker.MESSAGE_ALI_ACCOUNT_INFO);
            if (!TextUtils.isEmpty(accountInfo)) {
                LogUtil.e("账号信息：" + accountInfo);
                Intent accountInfoIntent = new Intent(context, PluginServer.class);
                accountInfoIntent.setAction(PluginServer.SERVICE_UP_ACCOUNT_DATA);
                accountInfoIntent.putExtra("data", accountInfo);
                context.startService(accountInfoIntent);
                return;
            }
            //支付成功的回调
            String paySuccessInfo = intent.getStringExtra(AliPayHooker.PAY_SUCCESS_BACK);
            if (!TextUtils.isEmpty(paySuccessInfo)) {
                //收到 收款款通知
                HookModul analysis = AliDataAnalysis.analysis(paySuccessInfo);
                if (null != analysis) {
                    LogUtil.e("支付成功： 金额" + analysis.getExtraInfo().getMoney() + " 备注：" + analysis.getContent().getAssistMsg2());
                    Intent linkIntent = new Intent(context, PluginServer.class);
                    linkIntent.setAction(PluginServer.SERVICE_UP_COLLECTION_PAY_SRCCESS);
                    linkIntent.putExtra("mark", analysis.getContent().getAssistMsg2());
                    linkIntent.putExtra("money", analysis.getExtraInfo().getMoney());
                    context.startService(linkIntent);
                }
                return;
            }
            //收款码生成
            String link = intent.getStringExtra(AliPayHooker.COLLECTION_LINK);
            if (!TextUtils.isEmpty(link)) {
                //收到 收款连接
                Log.e("hooklog", "HookDataReceive *****************收到链接");
                String mark = intent.getStringExtra("mark");
                Intent linkIntent = new Intent(context, PluginServer.class);
                linkIntent.setAction(PluginServer.SERVICE_UP_COLLECTION_LINK);
                linkIntent.putExtra("mark", mark);
                linkIntent.putExtra("link", link);
                context.startService(linkIntent);
                return;
            }

        } else if (YunShanFuHook.ACTION_YUN_SHAN_FU.equals(intent.getAction())) {

            String qrcode = intent.getStringExtra("qrcode");
            if (!TextUtils.isEmpty(qrcode)) {
                String mark = intent.getStringExtra("mark");
                String money = intent.getStringExtra("money");
                LogUtil.e("收到-----数据 mark:" + mark + " money: " + money + " qrcode: " + qrcode);

                UpYunShanFuQRBean yunShanFuQRBean = new UpYunShanFuQRBean();
                yunShanFuQRBean.amount = money;
                yunShanFuQRBean.orderno = mark;
                yunShanFuQRBean.qrcode = qrcode;
                Intent qrcodeIntent = new Intent(context, PluginServer.class);
                qrcodeIntent.setAction(PluginServer.SERVICE_UP_QRCODE_DATA);
                qrcodeIntent.putExtra("data", JSON.toJSONString(yunShanFuQRBean));
                context.startService(qrcodeIntent);
                return;
            }

            //云闪付支付成功回调金额
            String name = intent.getStringExtra("name");
            if (!TextUtils.isEmpty(name)) {
                Intent qrcodeIntent = new Intent(context, PluginServer.class);
                qrcodeIntent.setAction(PluginServer.SERVICE_UP_PAYSUCCESS);
                qrcodeIntent.putExtra("money", intent.getStringExtra("money"));
                qrcodeIntent.putExtra("name", name);
                context.startService(qrcodeIntent);
            }

            //查询订单详情
//            String name = intent.getStringExtra("name");
//            String money = intent.getStringExtra("money");
//            if ()

        }
//        else if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//            if (SPUtil.isSMSOpen())
//            ThreadPoolUtils.executor(new SMSRunnable(context,intent));
//        }

    }
}