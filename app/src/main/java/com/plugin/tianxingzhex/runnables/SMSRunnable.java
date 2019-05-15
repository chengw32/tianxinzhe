package com.plugin.tianxingzhex.runnables;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.plugin.tianxingzhex.server.PluginServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import tianxingzhe.plugin.utils.Utils.LogUtil;
import tianxingzhe.plugin.utils.Utils.SPUtil;


public class SMSRunnable implements Runnable {

    private Context context;
    private String phoneNum ;
    private String accountnum ;
    private String smsContent ;
    private long time ;

    public SMSRunnable(Context context, String phoneNum, String smsContent, long longDate, String accountnum) {
        this.context = context;
        this.phoneNum = phoneNum;
        this.accountnum = accountnum;
        this.smsContent = smsContent;
        this.time = longDate;
    }

    @Override
    public void run() {
        LogUtil.e("收到短信");
        CharSequence charSequence = "";
        try {
            InputStream open = context.getResources().getAssets().open("banks.json");
            byte[] bArr = new byte[open.available()];
            open.read(bArr);
            charSequence = new String(bArr, "utf-8");
        } catch (IOException e) {
           SPUtil.saveLogMessage("读取 banks.json 异常");
        }
        if (!TextUtils.isEmpty(charSequence)) {
            try {
                JSONArray jSONArray = new JSONArray(charSequence.toString());
                Map hashMap = new HashMap(jSONArray.length());
                for (int i = 0; i < jSONArray.length(); i++) {
                    hashMap.put(jSONArray.getJSONObject(i).getString("code"), jSONArray.getJSONObject(i));
                }

                    String messageBody = smsContent;
                    LogUtil.e("短信内容: " + messageBody);
                    SPUtil.saveLogMessage("收到短信内容为："+messageBody);
                    String replace = phoneNum.replace("+86", "").replace(" ", "");
                    //如果json信息里包含银行电话 进入if内
                    if ((charSequence.toString()).contains(replace)) {
                        if (hashMap.containsKey(replace)) {

                            JSONObject jSONObject = (JSONObject) hashMap.get(replace);
                            //匹配入账短信关键字
                            String kw = jSONObject.getString("kw");
                            String[] split1 = kw.split(",");
                            boolean isBankMessage = false;
                            for (int i = 0; i < split1.length; i++) {
                                String key = split1[i];
                                if (messageBody.contains(key)) {
                                    isBankMessage = true;
                                    break;
                                }
                            }
                            if (!isBankMessage) {
                                LogUtil.e("解析错误：如果是银行入账短信解析出错请联系开发人员调整");
                                SPUtil.saveLogMessage("解析错误：如果是银行入账短信解析出错请联系开发人员调整");
                                return;
                            }
                            if (jSONObject.has("moneyStart")) {
                                String starts = jSONObject.getString("moneyStart");
                                if (!TextUtils.isEmpty(starts)) {
                                    String[] split = starts.split(",");
                                    for (int i = 0; i < split.length; i++) {
                                        String itemStart = split[i];
                                        if (messageBody.contains(itemStart)) {
                                            replace = itemStart;
                                            break;
                                        }
                                    }
                                }
                            }
                            String string = jSONObject.getString("moneyEnd");
                            int indexOf = messageBody.indexOf(replace);
                            replace = messageBody.substring(replace.length() + indexOf, messageBody.indexOf(string));
                            messageBody = jSONObject.getString("bankName");
                            String string2 = jSONObject.getString("code");
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("解析成功:\n 银行名字：");
                            stringBuilder.append(messageBody);
                            stringBuilder.append("\n 金额:");
                            stringBuilder.append(replace);
                            stringBuilder.append("\n 银行电话:");
                            stringBuilder.append(string2);
//								Intent intent2 = new Intent();
//								intent2.putExtra("money", replace);
//								intent2.putExtra("bankname", messageBody);
//								intent2.putExtra("bankcode", string2);
//								intent2.setAction(MainActivity.BACK_HOME_ACTION_SM);
//								context.sendBroadcast(intent2);

                            SPUtil.saveLogMessage(stringBuilder.toString());
                            LogUtil.e(stringBuilder.toString());

                            // 下面是获取短信的发送时间
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("bankName", messageBody);
                            jsonObject.put("money", replace);
                            jsonObject.put("bankPhone", string2);
                            jsonObject.put("time", ""+time);

                            LogUtil.e("数据开始回传服务器");
                            Intent qrcodeIntent = new Intent(context, PluginServer.class);
                            qrcodeIntent.setAction(PluginServer.SERVICE_UP_SMSDATA);
                            qrcodeIntent.putExtra("content",jsonObject.toString());
                            qrcodeIntent.putExtra("accountnum",accountnum);
                            context.startService(qrcodeIntent);
                        }
                    } else {
                        System.out.println("收到银行短信02=");
                    }
            } catch (JSONException e2) {
                LogUtil.e("jaon解析异常：" + e2.getMessage());
            }
        }
    }


    private SmsMessage getIncomingMessage(Object obj, Bundle bundle) {
        if (Build.VERSION.SDK_INT < 23) {
            return SmsMessage.createFromPdu((byte[]) obj);
        } else {

        }
        return SmsMessage.createFromPdu((byte[]) obj);
//		return SmsMessage.createFromPdu((byte[]) obj, bundle.getString("format"));
    }

}
