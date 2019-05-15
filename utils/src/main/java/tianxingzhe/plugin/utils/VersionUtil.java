package tianxingzhe.plugin.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


/**
 * Created by lishaohang on 2016/7/16.
 * 获取应用版本信息工具类
 */
public class VersionUtil {
    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }


    public static String getAppNameAndVersion(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        int labelRes = packageInfo.applicationInfo.labelRes;
//        return context.getResources().getString(labelRes) + packageInfo.versionName;
        return context.getResources().getString(labelRes) ;
    }

}
