package com.plugin.tianxingzhex.runnables;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.plugin.tianxingzhex.utils.CodeUtils;
import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

import de.robv.android.xposed.XposedHelpers;
import tianxingzhe.plugin.utils.Utils.LogUtil;

/**
 * Created by chenguowu on 2019/3/9.
 */
public class YinShengTongQRCodeRunnable implements Runnable {

    private Context mContext;
    private String mMoney;


    public YinShengTongQRCodeRunnable(Context context, String money) {
        this.mContext = context;
        this.mMoney = money;
    }

    @Override
    public void run() {

        if (!ThreadPoolUtils.cacheLink.containsKey(mMoney)) {
            ThreadPoolUtils.cacheLink.put(mMoney, mMoney);
            String userCode = getUserCode();
            String userType = getUserType();


            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("https://m.ysepay.com/yst-qr/newQrCode?userCode=");
            stringBuffer.append(userCode);
            stringBuffer.append("&userType=");
            stringBuffer.append(userType);
            stringBuffer.append("&curPayMoney=");
            stringBuffer.append(mMoney);
            stringBuffer.append("&system=Android&version=");
            stringBuffer.append("4.1.4180801");


            LogUtil.e("html5Url : " + stringBuffer.toString());
            Intent creatqrcode = new Intent(mContext, XposedHelpers.findClass("com.ysepay.mobileportal.activity.html.JsBridgeHtml", mContext.getClassLoader()));
            creatqrcode.putExtra("html5Url", stringBuffer.toString());
            creatqrcode.putExtra("money", mMoney);
            creatqrcode.putExtra("hideRefleshItem", false);
            creatqrcode.putExtra("qrCodeEntryString", "02");
            creatqrcode.putExtra("htmlTitle", "收款");
            creatqrcode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(creatqrcode);


            while (null == YinShenTongStaticFiled.webView) {

            }


            LogUtil.e("等待5秒 等网页生成二维码");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }

            YinShenTongStaticFiled.webView.setDrawingCacheEnabled(true);
            Bitmap bmp = YinShenTongStaticFiled.webView.getDrawingCache();
            if (bmp == null || bmp.isRecycled()) {
                return;
            }

            String url = CodeUtils.getUrl(bmp);

            YinShenTongStaticFiled.webView.setDrawingCacheEnabled(false);
            bmp.recycle();

            LogUtil.e("获取到二维码:  " + url);
            if (null != YinShenTongStaticFiled.qrActivity && !YinShenTongStaticFiled.qrActivity.isFinishing()) {
                //销毁页面
                YinShenTongStaticFiled.qrActivity.finish();
            }
            //清空webview静态成员变量 因为这个webview存储的是当前二维码图片 下次订单来了 需要新的webview对象
            YinShenTongStaticFiled.webView = null;


        } else {
            LogUtil.e("已经包含的订单： 金额" + mMoney);
        }


    }


    private String getUserCode() {
        Class<?> Session = XposedHelpers.findClass("com.ysepay.mobileportal.http.pub.Session", mContext.getClassLoader());
        Object getSession = XposedHelpers.callStaticMethod(Session, "getSession");
        Object getUser = XposedHelpers.callMethod(getSession, "getUser");
        return XposedHelpers.callMethod(getUser, "get", "userCode", mContext).toString();
    }

    private String getUserType() {
        Class<?> Session = XposedHelpers.findClass("com.ysepay.mobileportal.http.pub.Session", mContext.getClassLoader());
        Object getSession = XposedHelpers.callStaticMethod(Session, "getSession");
        Object getUser = XposedHelpers.callMethod(getSession, "getUser");
        String mercType = XposedHelpers.callMethod(getUser, "get", "merchantType", mContext).toString();
        if (TextUtils.isEmpty(mercType) || (!"1".equals(mercType) && !"2".equals(mercType))) {
            return "01";
        }
        return "02";
    }


}
