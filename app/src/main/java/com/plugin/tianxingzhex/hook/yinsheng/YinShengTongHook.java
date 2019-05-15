package com.plugin.tianxingzhex.hook.yinsheng;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.webkit.WebView;

import com.plugin.tianxingzhex.runnables.YinShenTongStaticFiled;
import com.plugin.tianxingzhex.runnables.YinShengTongQRCodeRunnable;
import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import tianxingzhe.plugin.utils.Utils.LogUtil;

/**
 * Created by chenguowu on 2019/2/22.
 */
public class YinShengTongHook {
    public static final String AIPAY_PACKAGE_NAME = "com.ysepay.mobileportal.activity";
    public static boolean ISHOOK = false;
    public static String ACTION_GET_ORDER = "action_get_order";
    private ClassLoader mClassLoader;
    private Context mContext;
    private Object instance;
    View progressBar;
    private WebView webView;
    private String money;

    public YinShengTongHook(ClassLoader classLoader, Context context) {
        this.mClassLoader = classLoader;
        this.mContext = context;
    }


    public void hook() {
        LogUtil.e("YinShengTongHook");
        registReceive();

        XposedHelpers.findAndHookMethod("com.ysepay.mobileportal.activity.html.JsBridgeHtml.WebViewChromeClient", mClassLoader, "onProgressChanged", WebView.class, int.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int progress = (int) param.args[1];
                if (100 == progress) {
                    LogUtil.e("网页加载结束");
                    YinShenTongStaticFiled.webView = (WebView) param.args[0];
                }
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.ysepay.mobileportal.activity.html.JsBridgeHtml", mClassLoader, "initView", new Object[]{new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                YinShenTongStaticFiled.qrActivity = (Activity) param.thisObject;
                super.afterHookedMethod(param);
            }
        }});
//        XposedHelpers.findAndHookMethod("com.ysepay.mobileportal.activity.mercaudit.CheckAccountNewFragment", mClassLoader, "initView", new XC_MethodHook() {
//            protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//
//                thisObject = methodHookParam.thisObject;
//                LogUtil.e("CheckAccountNewFragment initview");
//
//            }
//        });


        XposedHelpers.findAndHookMethod("com.ysepay.mobileportal.activity.mercaudit.CheckAccountNewFragment", mClassLoader, "callBack", Object.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                LogUtil.e("CheckAccountNewFragment callBack");
                Object obj = param.args[0];
                LogUtil.e("obj:" + obj);
                List getBody = (List) XposedHelpers.callMethod(obj, "getContentList");
                LogUtil.e("list.size :" + getBody.size());
                for (Object dataMap : getBody) {
                    LogUtil.e("dataMap :" + dataMap);
                    HashMap hashMap = (HashMap) dataMap;
                    LogUtil.e("dataMap2 :" + dataMap);
//                    Iterator<Map.Entry<String, Object>> iterator = dataMap.entrySet().iterator();
//                    while (iterator.hasNext()){
//                        Map.Entry<String, Object> next = iterator.next();
//                        String key = next.getKey();
//                        LogUtil.e(key);
//                        Object value = next.getValue();
//                        LogUtil.e(""+value);
//                    }
                }

                super.beforeHookedMethod(param);
            }
        });

    }

    private YinShenReceive mReceiver;

    private void registReceive() {
        if (null == mContext) return;
        LogUtil.e("registReceive: ");
        if (null != mReceiver) mContext.unregisterReceiver(mReceiver);
        mReceiver = new YinShenReceive();

        IntentFilter billReceiverIntentFilter = new IntentFilter();
        billReceiverIntentFilter.addAction(ACTION_GET_ORDER);
        //打开收款码界面
//		billReceiverIntentFilter.addAction(AliPayHooker.ACTION_OPEN_ALI_QRCODE_UI);
        mContext.registerReceiver(mReceiver, billReceiverIntentFilter);

    }

    class YinShenReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) return;
            if (ACTION_GET_ORDER.equals(intent.getAction())) {

                for (int i = 0; i < 5; i++) {

                    ThreadPoolUtils.executor(new YinShengTongQRCodeRunnable(context, String.valueOf(i + 33)));
                }


            }
        }
    }


//
//    private void getImageUrl(){
//        try {
//            HttpUtils httpUtils = new HttpUtils(15000);
//            httpUtils.configResponseTextCharset("UTF-8");
//            RequestParams params = new RequestParams();
////            params.addHeader("Cookie", logid);
//            httpUtils.send(HttpMethod.GET, "https://www.baidu.com/", params, new RequestCallBack<String>() {
//                @Override
//                public void onSuccess(ResponseInfo<String> arg0) {
//                    String str = arg0.result;
//                    LogUtil.e("result: "+str);
//
//                }
//
//                @Override
//                public void onFailure(HttpException e, String s) {
//                    LogUtil.e("onFailure: ");
//                }
//            });
//        } catch (Exception e) {
//                    LogUtil.e("Exception: "+e.getMessage());
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append("异常");
//            stringBuilder2.append(e.getMessage());
//            // TODO: 2018/12/27  异常
//        }
//    }

}
