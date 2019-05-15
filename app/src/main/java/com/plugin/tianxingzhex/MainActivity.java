package com.plugin.tianxingzhex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.plugin.tianxingzhex.application.MyApplication;
import com.plugin.tianxingzhex.base.BaseActivity;
import com.plugin.tianxingzhex.beans.DataBean;
import com.plugin.tianxingzhex.beans.DataBeanDao;
import com.plugin.tianxingzhex.dialogs.EdittextDialog;
import com.plugin.tianxingzhex.dialogs.LogDialog;
import com.plugin.tianxingzhex.event.RefreshMainUiEvent;
import com.plugin.tianxingzhex.hook.alihook.AliPayHooker;
import com.plugin.tianxingzhex.hook.sms.SMSContentObserver;
import com.plugin.tianxingzhex.hook.wechat.WeChatHooker;
import com.plugin.tianxingzhex.runnables.SMSRunnable;
import com.plugin.tianxingzhex.server.PluginServer;
import com.plugin.tianxingzhex.server.PushIntentService;
import com.plugin.tianxingzhex.server.PushService;
import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import tianxingzhe.plugin.utils.Utils.LogUtil;
import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.Utils.ToastUtil;
import tianxingzhe.plugin.utils.VersionUtil;
import tianxingzhe.plugin.utils.dialog.MyDialog;

public class MainActivity extends BaseActivity {

    private List<DataBean> datalist = new ArrayList<>();
    ListView listView;
    EditText accountNum;
    private SMSContentObserver smsContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager pkgManager = getPackageManager();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 2);
        }


        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission =
                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        final boolean smsPermession =
                pkgManager.checkPermission(Manifest.permission.READ_SMS, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            requestPermission();
        }
        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), PushIntentService.class);


//        String isAct = MyApplication.isUser ? "" : " （插件未勾选）";
//        setTitle(VersionUtil.getAppNameAndVersion(this) + "-短信版" + isAct);
        setTitle(VersionUtil.getAppNameAndVersion(this) + "-短信版" );
        EventBus.getDefault().register(this);

//        final CheckBox smsListener = (CheckBox) findViewById(R.id.cb_sms_listener);
//        if (smsPermession) {
//            smsListener.setChecked(SPUtil.isSMSOpen());
//        }
//        smsListener.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    actSmsReceiver();
//                }
//                SPUtil.setSMSListener(isChecked);
//            }
//        });

        actSmsReceiver();

        accountNum = (EditText) findViewById(R.id.et_account_num);
        accountNum.setText(SPUtil.getAccountNum());
        findViewById(R.id.ll_log).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showLog();
                return false;
            }
        });

        findViewById(R.id.bt_get_creat_dingding_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(YinShengTongHook.ACTION_GET_ORDER);
//                sendBroadcast(intent);
                Intent intent = new Intent("creatHongBao");
                sendBroadcast(intent);
            }
        });
        findViewById(R.id.tv_key_set).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPortDialog();
                return false;
            }
        });

        findViewById(R.id.bt_get_ali_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = accountNum.getText().toString().trim();
                String hostUrl = SPUtil.getHostUrl();
                if (TextUtils.isEmpty(hostUrl)) {
                    ToastUtil.show("请配置服务器地址跟密钥");
                    return;
                }

                if (!TextUtils.isEmpty(trim)) {
                    SPUtil.setAccountNum(trim);
                    upAliApp(trim);
                    return;
                }
                ToastUtil.show("输入账号");
            }
        });


        //打开微信
        findViewById(R.id.bt_act_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String trim = accountNum.getText().toString().trim();
                String hostUrl = SPUtil.getHostUrl();
                if (TextUtils.isEmpty(hostUrl)) {
                    ToastUtil.show("请配置服务器地址跟密钥");
                    return;
                }
                upWechatApp();
            }
        });
        findViewById(R.id.bt_get_yunshanfu_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = accountNum.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    SPUtil.setAccountNum(trim);
                    upYunShanFUApp(trim);
                    return;
                }
                ToastUtil.show("输入账号");
            }
        });
        initListView();
        refreshList();

    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SMSContentObserver.MSG_INBOX:
//                    if (!SPUtil.isSMSOpen()) return;
                    SPUtil.saveLogMessage("收到短信");
                    setSmsCode();
                    break;
            }
        }
    };

    private void setSmsCode() {

        String trim = accountNum.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtil.show("请填写账号");
            return;
        }

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    Uri.parse("content://sms/inbox"),
                    new String[]{"_id", "address", "body", "date"},
                    null, null, "date desc"); //
            if (cursor != null) {
                String body = "";
                String address = "";
                long longDate = 0;
                if (cursor.moveToFirst()) {
                    body = cursor.getString(cursor.getColumnIndex("body"));// 在这里获取短信信息
                    address = cursor.getString(cursor.getColumnIndex("address"));// 在这里获取短信信息
                    LogUtil.e(address);
                    LogUtil.e(body);

                    longDate = cursor.getLong(cursor.getColumnIndex("date"));
                    ThreadPoolUtils.executor(new SMSRunnable(MainActivity.this, address, body, longDate,accountNum.getText().toString().trim()));
//                    if (longDate >= clickTime) {
////                        if (!body.equals(msgContent) || !address.equals(phone) || longDateSMS != longDate) {
////							ContentValues values = new ContentValues();
////							values.put("read","1");	//修改短信为已读模式
////							//更新当前未读短信状态为已读
////							getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=?", new String[]{""+cursor.getInt(0)});
//
////                            analyzeSMS(address, body);
//
////                        }
//
//                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private long clickTime;

    private void actSmsReceiver() {
        if (null == smsContentObserver) {
            clickTime = System.currentTimeMillis();
            smsContentObserver = new SMSContentObserver(this, mHandler);
            getContentResolver().registerContentObserver(
                    Uri.parse("content://sms/"), true, smsContentObserver);// 注册监听短信数据库的变化
        }
    }

    private void showPortDialog() {
        new EdittextDialog(this).show();
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return datalist.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View layout = getLayoutInflater().inflate(R.layout.hook_result_item, null);
                TextView message = layout.findViewById(R.id.tv_message);
                TextView tv_state = layout.findViewById(R.id.tv_state);
                DataBean extraInfo = datalist.get(position);
                if (extraInfo.getOrderState() == 0) {
                    tv_state.setVisibility(View.GONE);
                } else if (extraInfo.getOrderState() == 1) {
                    tv_state.setVisibility(View.VISIBLE);
                    tv_state.setText("订单已确认完成");
                    tv_state.setTextColor(getResources().getColor(R.color._76B692));
                } else if (extraInfo.getOrderState() < 0) {
                    tv_state.setVisibility(View.VISIBLE);
                    tv_state.setText("无法确认该订单: " + extraInfo.getBackMessage());
                    tv_state.setTextColor(getResources().getColor(R.color.red));
                }
                message.setText(extraInfo.getShowData());
                return layout;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DataBean dataBean = datalist.get(position);
                int orderState = dataBean.getOrderState();
                if (orderState < 0) {
                    new MyDialog(MainActivity.this, "确认重新提交？", new MyDialog.OnConfirmClickListener() {
                        @Override
                        public void confirm() {
                            Intent intent = new Intent(MainActivity.this, PluginServer.class);
                            intent.setAction("reUpData");
                            intent.putExtra("data", dataBean);
                            startService(intent);
                        }
                    }).show();
                }

            }
        });

    }

    private LogDialog mLogDialog;

    private void showLog() {
        if (null == mLogDialog)
            mLogDialog = new LogDialog(MainActivity.this);
        mLogDialog.refresh();
        mLogDialog.show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                0);
    }


    private void refreshList() {

        List<DataBean> notes = MyApplication.mDaoSession.queryBuilder(DataBean.class).orderDesc(DataBeanDao.Properties.Id).build().list();
        datalist.clear();
        datalist.addAll(notes);
        BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();
        if (!listView.isStackFromBottom()) {
            listView.setStackFromBottom(true);
        }
        listView.setStackFromBottom(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //在每个if条件下填充相应的操作代码
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
//            case R.id.upWechat: //调起微信
//                upWechat();
//                Log.e("xxx","upWechat");
////				Intent localIntent1 = new Intent();
////				localIntent1.setAction(WeChatHooker.ACTION_WECHAT_URL_BACK);
////				localIntent1.putExtra(DATA_WECHAT_DATA_BACK_URL, "111");
////				sendBroadcast(localIntent1);
//                break;
//            case R.id.netTest: //穿透是否成功测试
//                new WebDialog(MainActivity.this).show();
//
//                break;
            case R.id.activityAli: //激活支付宝在线状态（获取支付宝信息在MyService收到信息后提交给后台）
//                upAliApp();
                break;
            case R.id.cleanData:

                MyApplication.mDaoSession.deleteAll(DataBean.class);
                refreshList();
                break;
//            case R.id.creatAliUrl: //生成收款码
//                Intent localIntent = new Intent();
//                localIntent.putExtra("mark", "nongdaxia_test");
//                localIntent.putExtra("money", "0.01");
//                localIntent.setAction(AliPayHooker.ACTION_URL_GTE_TEST);
//                sendBroadcast(localIntent);
//
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Author chenguowu
     * Time 2019/1/11 10:43
     * Des 通知服务获取支付id
     */
    public void upAliApp(String trim) {
        Intent intent = new Intent(MainActivity.this, PluginServer.class);
        intent.setAction(AliPayHooker.SERVICE_ACTION_UP_ALI_APP);
        intent.putExtra("accountnum", trim);
        startService(intent);
    }

    private void upWechatApp() {
        Intent intentx = getPackageManager().getLaunchIntentForPackage(WeChatHooker.WECHAT_PACKAGE_NAME);
        if (intentx != null) {
            intentx.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intentx);
        }
    }

    /**
     * Author chenguowu
     * Time 2019/1/11 10:43
     * Des 通知服务获取支付id
     */
    public void upYunShanFUApp(String trim) {

        Intent intent = new Intent(MainActivity.this, PluginServer.class);
        intent.setAction(PluginServer.SERVICE_UP_YUNSHANFU);
        intent.putExtra("accountnum", trim);
        startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshMainUiEvent event) {
        refreshList();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
