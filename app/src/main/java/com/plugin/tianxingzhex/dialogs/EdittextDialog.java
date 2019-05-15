package com.plugin.tianxingzhex.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;


import com.plugin.tianxingzhex.R;

import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.Utils.ToastUtil;


public class EdittextDialog extends Dialog {


    public EdittextDialog(Context activity) {
        this(activity, R.style.pay_dialog_style);
    }

    public EdittextDialog(Context activity, int style) {
        super(activity, style);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edittext_layout);
        initView();
    }


    public void initView() {
        //点击空白消失

        Window mWindow = getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        // 透明度的范围为：0.0f-1.0f;0.0f表示完全透明,1.0f表示完全不透明(系统默认的就是这个)。
        //lp.alpha = 0.35f;

        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mWindow.setAttributes(lp);
        //设置对话框在屏幕的底部显示，当然还有上下左右，任意位置
        //mWindow.setGravity(Gravity.LEFT);
        mWindow.setGravity(Gravity.CENTER);

        final EditText et_host = findViewById(R.id.et_host_url);
        et_host.setText(SPUtil.getHostUrl());
        final EditText et_key = findViewById(R.id.et_key);
        et_key.setText(SPUtil.getSignKey());
        findViewById(R.id.bt_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = et_host.getText().toString().trim();
                if (TextUtils.isEmpty(host)) {
                    ToastUtil.show("服务器地址不能为空");
                    return;
                }
                String key = et_key.getText().toString().trim();
                if (TextUtils.isEmpty(key)) {
                    ToastUtil.show("密钥不能为空");
                    return;
                }
                SPUtil.setHostUrl(host);
                SPUtil.setSignKey(key);
                dismiss();
            }
        });


    }

}
