package com.plugin.tianxingzhex.runnables;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.lzy.okgo.model.Response;
import com.plugin.tianxingzhex.application.MyApplication;
import com.plugin.tianxingzhex.beans.DataBean;
import com.plugin.tianxingzhex.beans.DataBeanDao;
import com.plugin.tianxingzhex.beans.UpAliInfoBean;
import com.plugin.tianxingzhex.event.RefreshMainUiEvent;
import com.plugin.tianxingzhex.params.UpWechatParam;
import com.plugin.tianxingzhex.utils.DBUtil;
import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

import org.greenrobot.eventbus.EventBus;

import de.robv.android.xposed.XposedHelpers;
import tianxingzhe.plugin.utils.Utils.LogUtil;
import tianxingzhe.plugin.utils.Utils.MD5Utils;
import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.Utils.ToastUtil;
import tianxingzhe.plugin.utils.okgo.OkGoCallBack;
import tianxingzhe.plugin.utils.okgo.OkGoUtils;

/**
 * Created by chenguowu on 2019/3/14.
 */
public class UpWechatRunnable implements Runnable {
    private String money, msgId, time,bossName;

    public UpWechatRunnable(String money, String msgId, String time, String bossName) {
        this.money = money;
        this.msgId = msgId;
        this.time = time;
        this.bossName = bossName;
    }

    @Override
    public void run() {

        LogUtil.e("-------------微信订单提交线程-----开始----");

        DataBean notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).where(DataBeanDao.Properties.OrderNum.eq(msgId)).unique();
        if (notes != null) {
            LogUtil.e("重复订单 消息id："+msgId  + "  金额："+money);
            return;
        }

        final DataBean dataBean = new DataBean();
        StringBuilder sb = new StringBuilder();
        sb.append("收到微信收款：").append("\n");
        sb.append("金额：").append(money).append("\n");
        sb.append("店主：").append(bossName).append("\n");
        sb.append("时间：").append(time).append("\n");
        sb.append("消息id：").append(msgId).append("\n");
        dataBean.setShowData(sb.toString());
        dataBean.setMark(msgId);
        dataBean.setMoney(money);
        dataBean.setOrderNum(msgId);
        //存储到数据库
        DBUtil.saveData(dataBean);
        EventBus.getDefault().post(new RefreshMainUiEvent());


        UpWechatParam param = new UpWechatParam();
        param.money = money;
        param.msgId = msgId;
        param.time = time;
        param.name = bossName;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(bossName).append("|");
        stringBuffer.append(money).append("|");
        stringBuffer.append(time).append("|");
        stringBuffer.append(SPUtil.getSignKey());
        LogUtil.e("sing 字符串：" + stringBuffer.toString());
        param.sign = MD5Utils.MD5(stringBuffer.toString());

        OkGoUtils.post(null, param, new OkGoCallBack<String>(null, String.class) {
            @Override
            protected void success(String body) {
                DataBean notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).where(DataBeanDao.Properties.OrderNum.eq(dataBean.getOrderNum())).unique();
                if (notes != null) {
                    notes.setOrderState(1);
                    MyApplication.mDaoSession.update(notes);
                    EventBus.getDefault().post(new RefreshMainUiEvent());
                }
            }

            @Override
            public void error(Response<String> response) {
                DataBean notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).where(DataBeanDao.Properties.OrderNum.eq(dataBean.getOrderNum())).unique();
                if (notes != null) {
                    notes.setOrderState(-1);
                    notes.setBackMessage(response.body());
                    MyApplication.mDaoSession.update(notes);
                    EventBus.getDefault().post(new RefreshMainUiEvent());
                }
            }
        });
        LogUtil.e("-------------微信订单提交线程-----结束----");
    }
}
