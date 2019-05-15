package com.plugin.tianxingzhex.hook.alihook;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class ApplicationUtil {
    /**
     * 检测是否安装
     */
    public static boolean checkInstalled(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断是否运行在虚拟环境中
     */
    public static boolean isRunInVirtual(Context context) {
        return !context.getPackageResourcePath().startsWith("/data/app/" + context.getPackageName());
    }

    /**
     * 判断应用是否已经启动
     */
    public static boolean isRunning(Context context, String... packageNames) throws PermissionException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !isRunInVirtual(context)) {
            if (!PermissionUtil.checkOperationPermission(context, AppOpsManager.OPSTR_GET_USAGE_STATS)) {
                throw new PermissionException(PermissionException.PERMISSION_TYPE_OPERATION, Settings.ACTION_USAGE_ACCESS_SETTINGS);
            }
            UsageStatsManager usm = ((UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE));
            long endTime = System.currentTimeMillis();
            long startTime = endTime - 10 * 1000;
            UsageEvents usageEvents = usm.queryEvents(startTime, endTime);
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                for (String packageName : packageNames) {
                    if (event.getPackageName().equals(packageName)) return true;
                }
            }
            return false;
        } else {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
            for (int i = 0; i < processInfos.size(); i++) {
                for (String packageName : packageNames) {
                    if (processInfos.get(i).processName.equals(packageName)) {
                        return true;
                    }
                }

            }
            return false;
        }
    }

    public static void moveToTop(Activity activity) {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        am.moveTaskToFront(activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
    }

    public static void moToBackground(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 启动App
     */
    public static void startApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 启动Service
     */
    public static void startService(Context context, String packageName, String serviceName) {
        if (!ApplicationUtil.isServiceRunning(context, packageName, serviceName)) {
            Intent serviceIntent = new Intent();
            serviceIntent.setComponent(new ComponentName(packageName, serviceName));
            context.startService(serviceIntent);
        }
    }

    /**
     * 判断本应用是否已经位于最前端
     *
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * [获取应用程序版本名称信息]
//     *
//     * @return 当前应用的版本名称
//     */
//    public static long getVersionCode(Context context, String packageName) {
//        try {
//            PackageManager packageManager = context.getPackageManager();
//            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
//            return packageInfo.getLongVersionCode();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    public static String getPackageName(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
/////


    /**
     * 获取版本号名称
     *
     * @param context 上下文
     */
    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     */
    public static String getVersionName(Context context, String packageName) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 判断服务是否运行
     */
    public static boolean isServiceRunning(Context context, String packageName, String
            serviceName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (packageName.equals(aInfo.process) && serviceName.equals(aInfo.service.getClassName()))
                return true;
        }
        return false;
    }

    public static void goHome(Context context) {
        if (context == null) return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        context.startActivity(intent);
    }
}
