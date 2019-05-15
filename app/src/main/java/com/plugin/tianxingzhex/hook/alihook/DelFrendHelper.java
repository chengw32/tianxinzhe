package com.plugin.tianxingzhex.hook.alihook;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by chenguowu on 2019/3/11.
 */
public class DelFrendHelper {

    public static void del(final ClassLoader classLoader, final String userId){

        new Thread(new Runnable() {
            public void run() {
                XposedBridge.log("删除好友开始=====");
                try {
                    Object a = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass("com.alipay.mobile.framework.AlipayApplication"), "getInstance", new Object[0]), "getMicroApplicationContext", new Object[0]), "getExtServiceByInterface", new Object[]{"com.alipay.mobile.personalbase.service.SocialSdkContactService"}), "queryAccountById", new Object[]{userId});
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("ContactAccount =");
                    stringBuilder.append(JSON.toJSONString(a));
                    XposedBridge.log(stringBuilder.toString());


                    XposedBridge.log("delectContact——111");
                    Object newInstance = XposedHelpers.newInstance(XposedHelpers.findClass("com.alipay.mobilerelation.biz.shared.req.HandleRelationReq", classLoader), new Object[0]);
                    XposedBridge.log("delectContact——222");
                    XposedHelpers.setObjectField(newInstance, "targetUserId", userId);
                    XposedHelpers.setObjectField(newInstance, "alipayAccount", XposedHelpers.getObjectField(a, "account"));
                    XposedHelpers.setObjectField(newInstance, "bizType", "2");
                    XposedBridge.log("delectContact——333");

                    Object a2 = a(b(classLoader));
                    Object[] objArr = new Object[]{newInstance};
                    XposedBridge.log("delectContact——444");
                    a2 = XposedHelpers.callMethod(XposedHelpers.callMethod(a2, "getRpcProxy", new Object[]{XposedHelpers.findClass("com.alipay.mobilerelation.biz.shared.rpc.AlipayRelationManageService", classLoader)}), "handleRelation", objArr);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("删除好友结果");
                    stringBuilder.append(JSON.toJSONString(a2));
                    XposedBridge.log(stringBuilder.toString());



                } catch (Exception e) {
                }
                XposedBridge.log("删除好友结束=====");
                cleanLocalDB(classLoader,userId);
            }
        }).start();








    }

    public static void cleanLocalDB(ClassLoader classLoader, String userid) {

        Class<?> UserIndependentCache = XposedHelpers.findClass("com.alipay.mobile.socialcommonsdk.bizdata.UserIndependentCache", classLoader);

        Class<?> RecentSessionDaoOp = XposedHelpers.findClass("com.alipay.mobile.socialcommonsdk.bizdata.contact.data.RecentSessionDaoOp",classLoader);
        Object RecentSessionDaoOpObj = XposedHelpers.callStaticMethod(UserIndependentCache, "getCacheObj", RecentSessionDaoOp);
        XposedHelpers.callMethod(RecentSessionDaoOpObj,"deleteRecentSession","1_" + userid);

        Class<?> ChatMsgDaoOp = XposedHelpers.findClass("com.alipay.mobile.socialcommonsdk.bizdata.chat.data.ChatMsgDaoOp", classLoader);
        Object ChatMsgDaoOpObj = XposedHelpers.callStaticMethod(UserIndependentCache, "getCacheObj", ChatMsgDaoOp,userid);
        XposedHelpers.callMethod(ChatMsgDaoOpObj,"deleteAllMsgs");

        Class<?> AliAccountDaoOp = XposedHelpers.findClass("com.alipay.mobile.socialcommonsdk.bizdata.contact.data.AliAccountDaoOp",classLoader);
        Object CAliAccountDaoOpObj = XposedHelpers.callStaticMethod(UserIndependentCache, "getCacheObj", AliAccountDaoOp);
        XposedHelpers.callMethod(CAliAccountDaoOpObj,"deleteAccountById",userid);

        Class<?> ContactExtInfoDaoOp = XposedHelpers.findClass("com.alipay.mobile.socialcontactsdk.contact.data.ContactExtInfoDaoOp", classLoader);
        Object ContactExtInfoDaoOpObj = XposedHelpers.callStaticMethod(UserIndependentCache, "getCacheObj", ContactExtInfoDaoOp);
        XposedHelpers.callMethod(ContactExtInfoDaoOpObj,"updateCertainContactExtStatus",userid, "extSettingInfoJsonStr", "");

        ArrayList arrayList = new ArrayList(1);
        arrayList.add(userid);
        Class<?> RecommendationFriendDaoOp = XposedHelpers.findClass("com.alipay.mobile.socialcommonsdk.bizdata.contact.data.RecommendationFriendDaoOp", classLoader);
        Object RecommendationFriendDaoOpObj = XposedHelpers.callStaticMethod(UserIndependentCache, "getCacheObj", RecommendationFriendDaoOp);
        XposedHelpers.callMethod(RecommendationFriendDaoOpObj,"deleteFriendsAndUpdateRecent",arrayList);

        Class<?> application = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", classLoader);
        Object instance = XposedHelpers.callStaticMethod(application, "getInstance");
        Object getMicroApplicationContext = XposedHelpers.callMethod(instance, "getMicroApplicationContext");
        Class<?> SocialSdkContactService = XposedHelpers.findClass("com.alipay.mobile.personalbase.service.SocialSdkContactService", classLoader);
        Object serviceByInterface = XposedHelpers.callMethod(getMicroApplicationContext, "findServiceByInterface",SocialSdkContactService.getName());
        XposedHelpers.callMethod(serviceByInterface,"clearFriendRequestState",userid);
    }

    public static Object a(Object obj) {
        try {
            return XposedHelpers.callMethod(obj, "findServiceByInterface", new Object[]{obj.getClass().getClassLoader().loadClass("com.alipay.mobile.framework.service.common.RpcService").getName()});
        } catch (Exception e) {
            return null;
        }
    }

    public static Object b(ClassLoader classLoader) {
        try {
            Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass("com.alipay.mobile.framework.LauncherApplicationAgent"), "getInstance", new Object[0]), "getMicroApplicationContext", new Object[0]);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ApplicationContext cn:");
            stringBuilder.append(callMethod.getClass().getName());
            XposedBridge.log(stringBuilder.toString());
            return callMethod;
        } catch (Exception e) {
            return null;
        }
    }


    public static Object a(ClassLoader classLoader, String str) {
        try {
            Object callMethod = XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callMethod(XposedHelpers.callStaticMethod(classLoader.loadClass("com.alipay.mobile.framework.AlipayApplication"), "getInstance", new Object[0]), "getMicroApplicationContext", new Object[0]), "getExtServiceByInterface", new Object[]{"com.alipay.mobile.personalbase.service.SocialSdkContactService"}), "queryAccountById", new Object[]{str});
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ContactAccount =");
            stringBuilder.append(JSON.toJSONString(callMethod));
            XposedBridge.log(stringBuilder.toString());
            return callMethod;
        } catch (Exception e) {
            return null;
        }
    }



}
