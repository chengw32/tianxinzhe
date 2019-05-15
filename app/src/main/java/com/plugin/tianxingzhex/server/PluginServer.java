package com.plugin.tianxingzhex.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushManager;
import com.lzy.okgo.model.Response;
import com.plugin.tianxingzhex.R;
import com.plugin.tianxingzhex.application.MyApplication;
import com.plugin.tianxingzhex.beans.DataBean;
import com.plugin.tianxingzhex.beans.DataBeanDao;
import com.plugin.tianxingzhex.beans.GiftInfo;
import com.plugin.tianxingzhex.beans.SMSDataBean;
import com.plugin.tianxingzhex.beans.UpAliInfoBean;
import com.plugin.tianxingzhex.beans.UpYunShanFuQRBean;
import com.plugin.tianxingzhex.beans.UserInfoBean;
import com.plugin.tianxingzhex.event.RefreshMainUiEvent;
import com.plugin.tianxingzhex.hook.alihook.AliPayHooker;
import com.plugin.tianxingzhex.hook.wechat.WeChatHooker;
import com.plugin.tianxingzhex.hook.yunshangfu.YunShanFuHook;
import com.plugin.tianxingzhex.params.HeartBeatParam;
import com.plugin.tianxingzhex.params.UpCollectionParam;
import com.plugin.tianxingzhex.params.UpDataParam;
import com.plugin.tianxingzhex.params.UpSmsDataParam;
import com.plugin.tianxingzhex.params.UpWechatParam;
import com.plugin.tianxingzhex.params.UpYunShanFuPaySuccessParam;
import com.plugin.tianxingzhex.params.UpYunShanFuQrcodeParam;
import com.plugin.tianxingzhex.params.YunShanFuParam;
import com.plugin.tianxingzhex.runnables.UpWechatRunnable;
import com.plugin.tianxingzhex.utils.DBUtil;
import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

import org.greenrobot.eventbus.EventBus;

import tianxingzhe.plugin.utils.Utils.LogUtil;
import tianxingzhe.plugin.utils.Utils.MD5Utils;
import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.Utils.TimeUtil;
import tianxingzhe.plugin.utils.Utils.ToastUtil;
import tianxingzhe.plugin.utils.okgo.OkGoCallBack;
import tianxingzhe.plugin.utils.okgo.OkGoUtils;


/**
 * Created by chenguowu on 2019/2/13.
 */
public class PluginServer extends Service {
    //红包信息
    public static final String SERVICE_UP_REDPACAGE_DATA = "service_up_readpackage_data";
    public static final String SERVICE_UP_ACCOUNT_DATA = "service_up_account_data";
    public static final String SERVICE_UP_YUNSHANFU = "service_up_yunshanfu_app";
    public static final String SERVICE_UP_QRCODE_DATA = "service_up_qrcode_data";
    public static final String SERVICE_UP_PAYSUCCESS = "service_up_paysuccess_data";
    public static final String SERVICE_UP_SMSDATA = "service_up_sms_data";
    //回传收款码链接
    public static final String SERVICE_UP_COLLECTION_LINK = "service_up_collection_link";
    //收款单支付完成提交服务器
    public static final String SERVICE_UP_COLLECTION_PAY_SRCCESS = "service_up_collection_link_pay_success";
    //    private HookDataReceive myReceive;
    String aliAccountId;
    //填写的账户
    String accountnum;

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification("正在运行...");
        startTime();
    }

    //心跳计时器
    //激活支付宝账号在线状态计时器
    private int intervalTime = 1 * 60 * 1000;

    private void startTime() {
        mHeartHandle.postDelayed(mHeartRunnable, intervalTime);
    }

    private Handler mHeartHandle = new Handler();

    private void refreshTimer() {
        mHeartHandle.removeCallbacks(mHeartRunnable);
        startTime();
    }

    private Runnable mHeartRunnable = new Runnable() {
        @Override
        public void run() {
            heartBeat();
            startTime();
        }
    };

    private void heartBeat() {
        if (TextUtils.isEmpty(accountnum)) return;

        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), PushIntentService.class);

        HeartBeatParam param = new HeartBeatParam();

        StringBuffer strData = new StringBuffer();
        strData.append(accountnum).append("|");
        strData.append(TimeUtil.getCurrentTime());

        param.data = strData.toString();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("0").append("|");
        stringBuffer.append(accountnum).append("|");
        stringBuffer.append(TimeUtil.getCurrentTime()).append("|");
        stringBuffer.append(SPUtil.getSignKey());
        param.sign = MD5Utils.MD5(stringBuffer.toString());

//        OkGoUtils.post(null, param, new OkGoCallBack<UpAliInfoBean>(null, UpAliInfoBean.class) {
//            @Override
//            protected void success(UpAliInfoBean body) {
//                ToastUtil.show("在线中...");
//            }
//
//        });
    }

    private void showNotification(String showMessage) {
        if (Build.VERSION.SDK_INT >= 18) {
            //------------------8.0 以上的适配-------------------------------
            String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
            String CHANNEL_ONE_NAME = "CHANNEL_ONE_ID";
            NotificationChannel notificationChannel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(notificationChannel);
            }

            //-------------------------------------------------------------------

            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("收款助手");
            builder.setContentText(showMessage);
            builder.setAutoCancel(false);
            builder.setOngoing(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ONE_ID);
            }
            startForeground(100, builder.build());
        } else {
            startForeground(100, new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.hasExtra("accountnum"))
        accountnum = intent.getStringExtra("accountnum");

        if (WeChatHooker.WECHAT_ACTION.equals(intent.getAction())) {
            SPUtil.saveLogMessage("收到微信收款");
            upWechat(intent);
        }

        if (AliPayHooker.SERVICE_ACTION_UP_ALI_APP.equals(intent.getAction())) {


            //支付宝信息为空 则调起支付宝 在返回信息
            upAliApp();
            getAliAccountInfo();
            if (!TextUtils.isEmpty(aliAccountId)) {
                //支付宝账号不为空 则直接上传数据 调起支付宝
                actAccountOnline();
            }
        } else if ("reUpData".equals(intent.getAction())) {
            //重新提交数据
            DataBean data = (DataBean) intent.getSerializableExtra("data");
            upAliData(data);
        } else if (SERVICE_UP_REDPACAGE_DATA.equals(intent.getAction())) {
            //从HookDataReceive接收到的红包数据信息
            upRedPackageInfo(intent.getStringExtra("data"));
        } else if (SERVICE_UP_COLLECTION_PAY_SRCCESS.equals(intent.getAction())) {
            //收款单支付完成回调服务器
            String money = intent.getStringExtra("money");
            String mark = intent.getStringExtra("mark");
            upCollectionOrder(mark, money);
        } else if (SERVICE_UP_ACCOUNT_DATA.equals(intent.getAction())) {
            //返回账号信息
            String accountInfo = intent.getStringExtra("data");
            UserInfoBean userInfoBean = JSON.parseObject(accountInfo, UserInfoBean.class);
            aliAccountId = userInfoBean.getUserId();
            actAccountOnline();
        } else if (SERVICE_UP_COLLECTION_LINK.equals(intent.getAction())) {
            //提交收款码链接
            String link = intent.getStringExtra("link");
            String mark = intent.getStringExtra("mark");
            Log.e("hooklog", "PluginServer 提交 link: " + link);
            //让用户在支付宝付 无需回调

            upCollectionLink(link, mark);

        } else if (SERVICE_UP_YUNSHANFU.equals(intent.getAction())) {
            //调起云闪付 激活账号
            upYunShanFuApp();
            actAccountOnline();
        } else if (SERVICE_UP_QRCODE_DATA.equals(intent.getAction())) {
            //云闪付二维码回传

            String data = intent.getStringExtra("data");
            LogUtil.e("提交数据： " + data);
            upYunShanFuQrcode(data);
        } else if (SERVICE_UP_PAYSUCCESS.equals(intent.getAction())) {
            //云闪付支付成功金额提交

            String name = intent.getStringExtra("name");
            String money = intent.getStringExtra("money");
            LogUtil.e("云闪付支付成功提交金额数据： " + money);
            upYunShanFuPaySuccess(money, name);
        } else if (SERVICE_UP_SMSDATA.equals(intent.getAction())) {
            //短信内容提交
            String content = intent.getStringExtra("content");
            upSMSSuccess(content);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void upCollectionLink(String link, String mark) {

        if (TextUtils.isEmpty(accountnum)) {
            ToastUtil.show("账号不能为空");
            return;
        }

        UpCollectionParam param = new UpCollectionParam();
        param.orderno = mark;
        param.payurl = link;
        param.usercode = accountnum;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(param.usercode).append("|");
        stringBuffer.append(param.orderno).append("|");
        stringBuffer.append(param.payurl).append("|");
        stringBuffer.append(SPUtil.getSignKey());
        LogUtil.e("sing 字符串：" + stringBuffer.toString());
        param.sign = MD5Utils.MD5(stringBuffer.toString());

        OkGoUtils.post(null, param, new OkGoCallBack<UpAliInfoBean>(null, UpAliInfoBean.class) {
            @Override
            protected void success(UpAliInfoBean body) {
                ToastUtil.show("提交成功");
            }

        });
    }

    private void upWechat(Intent intent) {
        String money = intent.getStringExtra("money");
        String msgId = intent.getStringExtra("msgId");
        String time = intent.getStringExtra("time");
        String bossName = intent.getStringExtra("bossName");
        ThreadPoolUtils.executor(new UpWechatRunnable(money,msgId,time,bossName));
    }

    private void upYunShanFuQrcode(String qrcodeInfo) {

        UpYunShanFuQRBean bean = JSON.parseObject(qrcodeInfo, UpYunShanFuQRBean.class);

        if (TextUtils.isEmpty(accountnum)) {
            ToastUtil.show("账号不能为空");
        }

        UpYunShanFuQrcodeParam param = new UpYunShanFuQrcodeParam();
        param.usercode = accountnum;
        param.amount = bean.amount;
        param.orderno = bean.orderno;
        param.qrcode = bean.qrcode;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(bean.amount).append("|");
        stringBuffer.append(accountnum).append("|");
        stringBuffer.append(bean.orderno).append("|");
        stringBuffer.append(bean.qrcode).append("|");
        stringBuffer.append(SPUtil.getSignKey());
        LogUtil.e("sing 字符串：" + stringBuffer.toString());
        param.sign = MD5Utils.MD5(stringBuffer.toString());

        OkGoUtils.post(null, param, new OkGoCallBack<UpAliInfoBean>(null, UpAliInfoBean.class) {
            @Override
            protected void success(UpAliInfoBean body) {
                ToastUtil.show("提交成功");
            }

        });
    }

    private void upSMSSuccess(String content) {

        final SMSDataBean smsDataBean = JSON.parseObject(content, SMSDataBean.class);

        if (null != smsDataBean) {
            DataBean dataBean = new DataBean();
            StringBuilder sb = new StringBuilder();
            sb.append("解析成功：").append("\n");
            sb.append("时间：").append(smsDataBean.time).append("\n");
            sb.append("银行：").append(smsDataBean.bankName).append("\n");
            sb.append("金额：").append(smsDataBean.money).append("\n");
            sb.append("银行电话：").append(smsDataBean.bankPhone);
            dataBean.setShowData(sb.toString());
            dataBean.setMark(smsDataBean.money);
            dataBean.setMoney(smsDataBean.money);
            dataBean.setOrderNum(smsDataBean.time);
            //同步存储到数据库
            saveDB(dataBean);
        }


        if (TextUtils.isEmpty(accountnum)) {
            ToastUtil.show("账号不能为空");
        }

        UpSmsDataParam param = new UpSmsDataParam();
        param.usercode = accountnum;
        param.content = content;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(accountnum).append("|");
        stringBuffer.append(param.content).append("|");
        stringBuffer.append(SPUtil.getSignKey());
        LogUtil.e("sing 字符串：" + stringBuffer.toString());
        param.sign = MD5Utils.MD5(stringBuffer.toString());

        SPUtil.saveLogMessage("提交短信数据：" + content);

        OkGoUtils.post(null, param, new OkGoCallBack<String>(null, String.class) {
            @Override
            protected void success(String body) {
                ToastUtil.show("提交成功");
                SPUtil.saveLogMessage("请求结果：" + body);
                DataBean notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).where(DataBeanDao.Properties.OrderNum.eq(smsDataBean.time)).unique();
                if (notes != null) {
                    notes.setOrderState(1);
                    MyApplication.mDaoSession.update(notes);
                }
            }

            @Override
            public void error(Response<String> response) {
                DataBean notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).where(DataBeanDao.Properties.OrderNum.eq(smsDataBean.time)).unique();
                if (notes != null) {
                    notes.setOrderState(-1);
                    notes.setBackMessage(response.body());
                    MyApplication.mDaoSession.update(notes);
                }
                SPUtil.saveLogMessage("请求结果：" + response.body());
            }
        });
    }

    private void upYunShanFuPaySuccess(String money, String name) {


        if (TextUtils.isEmpty(accountnum)) {
            ToastUtil.show("账号不能为空");
        }

        UpYunShanFuPaySuccessParam param = new UpYunShanFuPaySuccessParam();
        param.usercode = accountnum;
        param.amount = money;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(accountnum).append("|");
        stringBuffer.append(param.amount).append("|");
        stringBuffer.append(SPUtil.getSignKey());
        LogUtil.e("sing 字符串：" + stringBuffer.toString());
        param.sign = MD5Utils.MD5(stringBuffer.toString());

        OkGoUtils.post(null, param, new OkGoCallBack<UpAliInfoBean>(null, UpAliInfoBean.class) {
            @Override
            protected void success(UpAliInfoBean body) {
                ToastUtil.show("提交成功");
            }

        });
    }

    /**
     * Author chenguowu
     * Time 2019/1/18 16:57
     * Des 调起支付宝
     */
    private void upAliApp() {
        Intent intentx = getPackageManager().getLaunchIntentForPackage(AliPayHooker.AIPAY_PACKAGE_NAME);
        if (intentx != null) {
            intentx.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intentx);
        }
    }

    /**
     * Author chenguowu
     * Time 2019/1/18 16:57
     * Des 调起支付宝
     */
    private void upYunShanFuApp() {
        Intent intentx = getPackageManager().getLaunchIntentForPackage(YunShanFuHook.YUNSHANFU_PACKAGE_NAME);
        if (intentx != null) {
            intentx.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intentx);
        }
    }

    private void getAliAccountInfo() {
        //获取支付信息 并激活在线
        Intent infoIntent = new Intent();
        infoIntent.setAction(AliPayHooker.ACTION_RECEIVE_GET_ALI_ACCOUNT_INFO);
        sendBroadcast(infoIntent);
//        startTime();
    }


    /**
     * Author chenguowu
     * Time 2019/2/14 14:51
     * Des 提交账号信息
     */
    private void actAccountOnline() {

        String clientid = PushManager.getInstance().getClientid(this);
        if (TextUtils.isEmpty(clientid)) {
            ToastUtil.show("推送服务未初始化完成 稍等...");
            return;
        }

        if (TextUtils.isEmpty(accountnum)) {
            ToastUtil.show("账号不能为空");
        }

        YunShanFuParam param = new YunShanFuParam();
        param.usercode = accountnum;
        param.clientid = clientid;
        param.userid = aliAccountId;

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("0").append("|");
        stringBuffer.append(param.usercode).append("|");
        stringBuffer.append(param.clientid).append("|");
        stringBuffer.append(param.userid).append("|");
        stringBuffer.append(SPUtil.getSignKey());
        LogUtil.e("sing 字符串：" + stringBuffer.toString());
        param.Sign = MD5Utils.MD5(stringBuffer.toString());

        OkGoUtils.post(null, param, new OkGoCallBack<UpAliInfoBean>(null, UpAliInfoBean.class) {
            @Override
            protected void success(UpAliInfoBean body) {
                ToastUtil.show("配置成功");
            }

        });
    }

//    /**
//     * Author chenguowu
//     * Time 2019/2/14 14:51
//     * Des 提交账号信息
//     */
//    private void upAliAccountInfo() {
//        //        {"userId":"2088332926664018","loginId":"185******56","profession":"","realNameStatus":"Y"
//// ,"interest":"","income":""}
//        if (TextUtils.isEmpty(accountnum)) {
//            ToastUtil.show("账号不能为空");
//        }
//
//        AliAccountInfoParam param = new AliAccountInfoParam();
//        param.usercode = accountnum;
//        param.userid = aliAccountId;
//
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("0").append("|");
//        stringBuffer.append(accountnum).append("|");
//        stringBuffer.append(aliAccountId).append("|");
//        stringBuffer.append(SPUtil.getSignKey());
//        LogUtil.e("sing 字符串：" + stringBuffer.toString());
//        param.Sign = MD5Utils.MD5(stringBuffer.toString());
//
//        OkGoUtils.post(null, param, new OkGoCallBack<UpAliInfoBean>(null, UpAliInfoBean.class) {
//            @Override
//            protected void success(UpAliInfoBean body) {
//                ToastUtil.show("配置成功");
//            }
//
//        });
//    }

    private void upCollectionOrder(String mark, String money) {
        DataBean dataBean = new DataBean();
        StringBuilder sb = new StringBuilder();
        sb.append("转账备注：").append(mark).append(" ");
        sb.append("金额：").append(money).append(" ");
        dataBean.setShowData(sb.toString());
        dataBean.setMark(mark);
        dataBean.setMoney(money);
        dataBean.setOrderNum(mark);
        //同步存储到数据库
        saveDB(dataBean);

        upAliData(dataBean);
    }

    /**
     * Author chenguowu
     * Time 2019/2/14 14:32
     * Des 红包拆成功 提交信息
     */
    private void upRedPackageInfo(String stringExtra) {
        GiftInfo giftInfo = JSON.parseObject(stringExtra, GiftInfo.class);
        GiftInfo.GiftCrowdInfoBean giftCrowdInfo = giftInfo.getGiftCrowdInfo();

        String mark = giftCrowdInfo.getRemark();
//                try {
//                    List<String> strings = readLines(new StringReader(giftCrowdInfo.getRemark()));
//                    if (null != strings && !strings.isEmpty()) {
//                        Iterator<String> iterator = strings.iterator();
//                        String markValue = iterator.next();
//                        mark = arr(markValue);
//
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

        //存储到数据库
        DataBean notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).where(DataBeanDao.Properties.OrderNum.eq(giftCrowdInfo.getCrowdNo())).unique();
        if (notes != null) return;//说明该红包已经拆过 重复点击而已
        DataBean dataBean = new DataBean();
        StringBuilder sb = new StringBuilder();
        sb.append("转账备注：").append(mark).append(" ");
        sb.append("金额：").append(giftCrowdInfo.getAmount()).append(" ");
        sb.append("订单号：").append(giftCrowdInfo.getCrowdNo());
        dataBean.setShowData(sb.toString());
        dataBean.setMark(mark);
        dataBean.setMoney(giftCrowdInfo.getAmount());
        dataBean.setOrderNum(giftCrowdInfo.getCrowdNo());
        //同步存储到数据库
        saveDB(dataBean);

        upAliData(dataBean);
    }

    private void upAliData(final DataBean dataBean) {

        if (TextUtils.isEmpty(aliAccountId) || TextUtils.isEmpty(accountnum)) return;

        String amount = dataBean.getMoney();
        String mark = dataBean.getMark();

        UpDataParam upDataParam = new UpDataParam();
        upDataParam.amount = amount;
        upDataParam.orderno = mark;
        upDataParam.usercode = accountnum;
        upDataParam.userid = aliAccountId;
        upDataParam.paytime = TimeUtil.getCurrentTime();

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(amount).append("|");
        stringBuffer.append(mark).append("|");
        stringBuffer.append(upDataParam.paytime).append("|");
        stringBuffer.append(SPUtil.getSignKey());
        upDataParam.sign = MD5Utils.MD5(stringBuffer.toString());

        OkGoUtils.post(null, upDataParam, new OkGoCallBack<UpAliInfoBean>(null, UpAliInfoBean.class) {
            @Override
            protected void success(UpAliInfoBean body) {
                //发广播 删除好友
                LogUtil.e("userid: " + body.data);
                Intent intent = new Intent();
                intent.setAction(AliPayHooker.ACTION_DEL_FREND);
                intent.putExtra("userid", body.data);
                sendBroadcast(intent);
                DataBean notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).where(DataBeanDao.Properties.OrderNum.eq(dataBean.getOrderNum())).unique();
                if (notes != null) {
                    notes.setOrderState(1);
                    MyApplication.mDaoSession.update(notes);
                }
                refreshList();
            }

            @Override
            public void error(Response<UpAliInfoBean> response) {
                DataBean notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).where(DataBeanDao.Properties.OrderNum.eq(dataBean.getOrderNum())).unique();
                if (notes != null) {
                    notes.setOrderState(-1);
                    notes.setBackMessage(response.body().getMessage());
                    MyApplication.mDaoSession.update(notes);
                }
                refreshList();
            }
        });
    }

    private void saveDB(DataBean dataBean) {
        DBUtil.saveData(dataBean);
        refreshList();
    }

    /**
     * Author chenguowu
     * Time 2018/12/28 21:15
     * Des 刷新ui
     */
    private void refreshList() {
        EventBus.getDefault().post(new RefreshMainUiEvent());
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
