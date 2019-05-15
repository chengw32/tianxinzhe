package com.plugin.tianxingzhex.application;

import android.content.Intent;
import android.text.TextUtils;

import com.plugin.tianxingzhex.beans.DaoMaster;
import com.plugin.tianxingzhex.beans.DaoSession;
import com.plugin.tianxingzhex.server.PluginServer;
import com.tencent.bugly.Bugly;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.greenrobot.greendao.database.Database;

import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.application.BaseApplication;


/**
 * Created by chenguowu on 2019/2/13.
 */
public class MyApplication extends BaseApplication {

    public static boolean isUser;
    public static final String DB_NAME = "tianxingzhe-db.temp";
    public static DaoSession mDaoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        startService();
        initGreenDao();
        if (TextUtils.isEmpty(SPUtil.getHostUrl())) {
            SPUtil.setHostUrl("http://39.98.177.232:42857");
        }
        if (TextUtils.isEmpty(SPUtil.getSignKey())) {
            SPUtil.setSignKey("2E3D8E9315918C4A5432B88E22EC97C4");
        }
        SPUtil.setSMSListener(false);
        ZXingLibrary.initDisplayOpinion(this);

        Bugly.init(getApplicationContext(), "9813b7cefc", true);

    }

    public void startService() {
        context.startService(new Intent(context, PluginServer.class));
    }

    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME);
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
    }
}
