package com.plugin.tianxingzhex.hook.alihook;

import android.content.Context;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

public class AlipayUtils {
    private static final String packageName = "com.eg.android.AlipayGphone";
    private static final String appPackageName = "com.alipay.mobile";

    /**
     * 检测是否安装支付宝
     */
    public static boolean checkInstalled(Context context) {
        return ApplicationUtil.checkInstalled(context, packageName);
    }

    /**
     * 判断应用是否已经启动
     */
    public static boolean isRunning(Context context) throws PermissionException {
        return ApplicationUtil.isRunning(context, packageName, appPackageName);
    }

    /**
     * 启动支付宝
     */
    public static void startAlipay(Context context) {
        ApplicationUtil.startApp(context, packageName);
    }

    public static String getAliAccountInfo(Context mContext) {
        try {
            Class<?> AlipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", mContext.getClassLoader());
            Class<?> SocialSdkContactService = XposedHelpers.findClass("com.alipay.mobile.personalbase.service.SocialSdkContactService", mContext.getClassLoader());


            Object o = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(AlipayApplication, "getInstance", new Object[0]), "getMicroApplicationContext", new Object[0]), "findServiceByInterface", SocialSdkContactService.getName()), "getMyAccountInfoModelByLocal", new Object[0]);


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", XposedHelpers.getObjectField(o, "userId").toString());
            jsonObject.put("loginId", XposedHelpers.getObjectField(o, "loginId").toString());
            jsonObject.put("profession", XposedHelpers.getObjectField(o, "profession").toString());
            jsonObject.put("realNameStatus", XposedHelpers.getObjectField(o, "realNameStatus").toString());
            jsonObject.put("interest", XposedHelpers.getObjectField(o, "interest").toString());
            jsonObject.put("income", XposedHelpers.getObjectField(o, "income").toString());
            return jsonObject.toString();
        } catch (Exception e) {
            return "Exception";
        }
    }
    public static String getUserId(ClassLoader classLoader) {
        Class<?> AlipayApplication = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", classLoader);
        Class<?> socialSdkContactService = XposedHelpers.findClass("com.alipay.mobile.personalbase.service.SocialSdkContactService", classLoader);
        Object instace = XposedHelpers.callStaticMethod(AlipayApplication, "getInstance");
        Object applicationContext = XposedHelpers.callMethod(instace, "getMicroApplicationContext");
        Object service = XposedHelpers.callMethod(applicationContext, "findServiceByInterface", socialSdkContactService.getName());
        Object accountInfo = XposedHelpers.callMethod(service, "getMyAccountInfoModelByLocal");
        if (accountInfo != null) {
            return XposedHelpers.getObjectField(accountInfo, "userId").toString();
        } else return null;
    }

    public static void securityCheckHook(ClassLoader classLoader) {
        try {
            Class<?> securityCheckClazz = XposedHelpers.findClass("com.alipay.mobile.base.security.CI", classLoader);
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", String.class, String.class, String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object object = param.getResult();
                    XposedHelpers.setBooleanField(object, "a", false);
                    param.setResult(object);
                    super.afterHookedMethod(param);
                }
            });

            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", Class.class, String.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return (byte) 1;
                }
            });
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", ClassLoader.class, String.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return (byte) 1;
                }
            });
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return false;
                }
            });

        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }
}
