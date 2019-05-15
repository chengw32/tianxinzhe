package com.plugin.tianxingzhex.runnables;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by chenguowu on 2019/3/14.
 */
public class GetCollectionLinkRunnable implements Runnable {
    private String mMark, mMoney, mUserid;
    private Context mContext;
    private Handler mHandler;
    private Runnable mTimeOutRun;

    public GetCollectionLinkRunnable(String mMark, String mMoney, String mUserid, Context mContext) {
        this.mMark = mMark;
        this.mMoney = mMoney;
        this.mUserid = mUserid;
        this.mContext = mContext;
        this.mHandler = new Handler();
        mTimeOutRun = new TimeOutRunnable(mMark);
    }

    @Override
    public void run() {

        Log.e("hooklog", "去生成收款单===========线程开始");
        if (!ThreadPoolUtils.cacheLink.containsKey(mMark)) {
            //如果 map 里面没有该 mark 说明没有生成成功过 跳页面去生成
            Log.e("hooklog", "========跳页面去生成收款单===========");
            mHandler.postDelayed(mTimeOutRun,2000);
            Intent collectionIntent = new Intent(mContext, XposedHelpers.findClass("com.alipay.android.phone.personalapp.socialpayee.ui.SocialPersonalActivity_", mContext.getClassLoader()));
            collectionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            collectionIntent.putExtra("userId", mUserid);
            collectionIntent.putExtra("money", mMoney);
            collectionIntent.putExtra("mark", mMark);
            mContext.startActivity(collectionIntent);

            while (!ThreadPoolUtils.cacheLink.containsKey(mMark)) {
                //如果不包含 mark 或者在等待时间内  则一直while
            }
            Log.e("hooklog", "生成收款单=============== link: "+ ThreadPoolUtils.cacheLink.get(mMark));
        } else {
            Log.e("hooklog", "生成过收款单===直接跳过");
        }

        if (null != mTimeOutRun)
        mHandler.removeCallbacks(mTimeOutRun);
        mHandler = null ;
        Log.e("hooklog", "生成收款单===========线程结束");

    }
}
