package com.plugin.tianxingzhex.hook;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.plugin.tianxingzhex.hook.alihook.AliPayHooker;
import com.plugin.tianxingzhex.hook.alihook.AlipayUtils;
import com.plugin.tianxingzhex.hook.dingding.RimetHook;
import com.plugin.tianxingzhex.hook.wanxin.WanXinHook;
import com.plugin.tianxingzhex.hook.wechat.WeChatHooker;
import com.plugin.tianxingzhex.hook.yinsheng.YinShengTongHook;
import com.plugin.tianxingzhex.hook.yunshangfu.YunShanFuHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import tianxingzhe.plugin.utils.Utils.LogUtil;


/**
 * Created by chenguowu on 2018/12/12.
 */

public class Main implements IXposedHookLoadPackage {

    private Object mCollectMoneyRpc;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

//        Log.e("hooklog", "packageName: " + loadPackageParam.packageName);
//        //设置是否勾选插件
//        if (loadPackageParam.packageName.equals("com.plugin.tianxingzhex")) {
//            XposedHelpers.setStaticObjectField(XposedHelpers.findClass("com.plugin.tianxingzhex.application.MyApplication", loadPackageParam.classLoader), "isUser", Boolean.valueOf(true));
//        }
//
////		if (!loadPackageParam.packageName.equals(AliPayHooker.AIPAY_PACKAGE_NAME))
////			return;
//
//        if (loadPackageParam.packageName.equals(AliPayHooker.AIPAY_PACKAGE_NAME)) {
//
//            final ClassLoader classLoader = loadPackageParam.classLoader;
//
//            try {
//                //获取 context
//                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        final Context context = (Context) param.args[0];
//                        final String str2 = loadPackageParam.processName;
//
//                        if ("com.eg.android.AlipayGphone".equals(str2) && !AliPayHooker.ALIPAY_PACKAGE_ISHOOK) {
//                            AliPayHooker.ALIPAY_PACKAGE_ISHOOK = true;
//                            AlipayUtils.securityCheckHook(context.getClassLoader());
//                            new AliPayHooker().hook(classLoader, context);
//                        }
//                    }
//                });
//            } catch (Throwable exp) {
//                Log.e("hooklog", "Main->handleLoadPackage->ali error:" + exp.getMessage());
//            }
//
//        }
//        //云闪付
//        else if (loadPackageParam.packageName.equals(YunShanFuHook.YUNSHANFU_PACKAGE_NAME)) {
//
//            LogUtil.e("processName: " + loadPackageParam.processName);
//
//            try {
//                if (!YunShanFuHook.YUNSHANFU_ISHOOK && YunShanFuHook.YUNSHANFU_PACKAGE_NAME.equals(loadPackageParam.processName)) {
//                    YunShanFuHook.YUNSHANFU_ISHOOK = true;
//                    //获取 context
//                    XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            Context context = (Context) param.args[0];
//                            LogUtil.e("context:" + context);
//                            new YunShanFuHook().hook(loadPackageParam.classLoader, context);
//                        }
//                    });
//                }
//            } catch (Throwable exp) {
//                LogUtil.e("Main->handleLoadPackage->YunShanFuHook error:" + exp.getMessage());
//            }
//
//        }
//        //淘宝
//        else if (loadPackageParam.packageName.equals(WanXinHook.wanxin_package_name)&&loadPackageParam.processName.equals(WanXinHook.wanxin_package_name)) {
//
//            LogUtil.e("processName: " + loadPackageParam.processName);
//
//                    try {
//                        if (!WanXinHook.WANXIN_IS_HOOK && WanXinHook.wanxin_package_name.equals(loadPackageParam.processName)) {
//                            WanXinHook.WANXIN_IS_HOOK = true;
//                            //获取 context
//                            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                                    super.afterHookedMethod(param);
//                                    Context context = (Context) param.args[0];
//                                   new WanXinHook(context,loadPackageParam.classLoader);
//                                }
//                            });
//                        }
//                    } catch (Throwable exp) {
//                        LogUtil.e("Main->handleLoadPackage->YunShanFuHook error:" + exp.getMessage());
//                    }
//
//        } else if ("com.alibaba.android.rimet".equals(loadPackageParam.packageName)) {
//            XposedHelpers.findAndHookMethod(Application.class, "attach", new Object[]{Context.class, new XC_MethodHook() {
//                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                    super.afterHookedMethod(methodHookParam);
//                    if (!RimetHook.DINGDING_ISHOOK) {
//                        RimetHook.DINGDING_ISHOOK = true;
//                        Context context = (Context) methodHookParam.args[0];
//                        LogUtil.e("dingding hook 成功");
//                        IntentFilter intentFilter = new IntentFilter();
//                        intentFilter.addAction(RimetHook.DINGDING_ACTION);
//                        intentFilter.addAction("com.tools.payhelper.dindingstart");
//                        context.registerReceiver(new RimetHook.MyReceiver(), intentFilter);
//                        RimetHook.hook(context, context.getClassLoader());
//                    }
//                }
//            }});
//
//        }
//        else if (loadPackageParam.packageName.equals(YinShengTongHook.AIPAY_PACKAGE_NAME)) {
//
//            LogUtil.e("processName: " + loadPackageParam.processName);
//
//            try {
//                if (!YinShengTongHook.ISHOOK && YinShengTongHook.AIPAY_PACKAGE_NAME.equals(loadPackageParam.processName)) {
//                    YinShengTongHook.ISHOOK = true;
//                    //获取 context
//                    XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            Context context = (Context) param.args[0];
//                            LogUtil.e("context:" + context);
//                            new YinShengTongHook(loadPackageParam.classLoader, context).hook();
//                        }
//                    });
//                }
//            } catch (Throwable exp) {
//                LogUtil.e("Main->handleLoadPackage->YinShengTongHook error:" + exp.getMessage());
//            }
//
//        }
//        else if (loadPackageParam.packageName.equals(WeChatHooker.WECHAT_PACKAGE_NAME)) {
//
//            LogUtil.e("processName: " + loadPackageParam.processName);
//
//            try {
//                if (!WeChatHooker.WECHAT_PACKAGE_ISHOOK && WeChatHooker.WECHAT_PACKAGE_NAME.equals(loadPackageParam.processName)) {
//                    WeChatHooker.WECHAT_PACKAGE_ISHOOK = true;
//                    //获取 context
//                    XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//                            Context context = (Context) param.args[0];
//                            LogUtil.e("context:" + context);
//                            new WeChatHooker().hook(loadPackageParam.classLoader, context);
//                        }
//                    });
//                }
//            } catch (Throwable exp) {
//                LogUtil.e("Main->handleLoadPackage->YinShengTongHook error:" + exp.getMessage());
//            }
//
//        }


    }


}
