package com.plugin.tianxingzhex.runnables;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.plugin.tianxingzhex.utils.CodeUtils;

/**
 * Created by chenguowu on 2019/3/9.
 */
public class YunShanFuQRCodeRunnable implements Runnable {

   private ImageView mImageView;
   private String mMoney ;
   private String mMark ;


    public YunShanFuQRCodeRunnable(ImageView imageView,String money,String mark) {
        this.mImageView = imageView;
        this.mMark = mark;
        this.mMoney = money ;
    }

    @Override
    public void run() {


        if (null != mImageView) {
            mImageView.setDrawingCacheEnabled(true);
            Bitmap drawingCache = mImageView.getDrawingCache();
            if (null != drawingCache) {
                String url = CodeUtils.getUrl(drawingCache);
                Log.e("hooklog", "url " + url);
            }
        }
    }
}
