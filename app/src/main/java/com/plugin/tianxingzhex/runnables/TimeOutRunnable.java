package com.plugin.tianxingzhex.runnables;

import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

/**
 * Created by chenguowu on 2019/3/14.
 */
public class TimeOutRunnable implements Runnable {

    private String mMark;

    public TimeOutRunnable(String mMark) {
        this.mMark = mMark;
    }

    @Override
    public void run() {
        if (!ThreadPoolUtils.cacheLink.containsKey(mMark))
        ThreadPoolUtils.cacheLink.put(mMark,"未生成收款单");
    }
}
