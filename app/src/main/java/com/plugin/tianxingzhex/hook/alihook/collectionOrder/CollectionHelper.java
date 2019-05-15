package com.plugin.tianxingzhex.hook.alihook.collectionOrder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by chenguowu on 2019/2/25.
 */
public class CollectionHelper {
    public CollectionHelper(final ClassLoader classLoader, final Context mcontext) {

        //设置收款界面 打开后设置数值
        XposedHelpers.findAndHookMethod("com.alipay.android.phone.personalapp.socialpayee.ui.SocialPersonalActivity", classLoader, "onResume", new XC_MethodHook() {

            protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {

                Log.e("hooklog", "设值");
                Activity activity = (Activity) methodHookParam.thisObject;
                Intent intent = activity.getIntent();
                String money = intent.getStringExtra("money");
                String mark = intent.getStringExtra("mark");
                //设置金额
                Object d = XposedHelpers.getObjectField(activity, "d");
                XposedHelpers.callMethod(d, "setText", String.valueOf(Double.valueOf(money)));
                //设置备注
                Object e = XposedHelpers.getObjectField(activity, "e");
                XposedHelpers.callMethod(e, "setText", mark);
                XposedHelpers.callMethod(activity, "b");
            }
        });
    }
}
