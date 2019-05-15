package com.plugin.tianxingzhex.utils;


import com.plugin.tianxingzhex.application.MyApplication;
import com.plugin.tianxingzhex.beans.DataBean;

import java.util.List;

/**
 * Created by chenguowu on 2018/12/29.
 */

public class DBUtil {

    public static void saveData(DataBean dataBean) {
        delMore();
        MyApplication.mDaoSession.insertOrReplace(dataBean);
    }

    /**
     * Author chenguowu
     * Time 2018/12/29 17:35
     * Des 删除多余的
     * */
    public static void delMore() {
        List<DataBean> notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).build().list();
        if (notes != null && notes.size() > 50) {
            MyApplication.mDaoSession.delete(notes.get(0));
            delMore();
        }
    }

}
