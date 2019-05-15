package com.plugin.tianxingzhex.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.plugin.tianxingzhex.hook.alihook.AliPayHooker;
import com.plugin.tianxingzhex.hook.yunshangfu.YunShanFuHook;

/**
 * Created by chenguowu on 2019/3/10.
 */
public class BroadCaseSendUtil {
    public static void backYunShanFuData(Context context,Intent intent){
        setCompinet(intent);
        intent.setAction(YunShanFuHook.ACTION_YUN_SHAN_FU);
        context.sendBroadcast(intent);
    }
    public static void backAliData(Context context,Intent intent){
        setCompinet(intent);
        intent.setAction(AliPayHooker.ACTION_ALI);
        context.sendBroadcast(intent);
    }

    //8.0 适配静态注册
    public static void setCompinet(Intent intent){
        intent.setComponent(new ComponentName("com.plugin.tianxingzhe","HookDataReceive"));
    }

    public static void startAct(Context context,Intent intent){

    }

}
