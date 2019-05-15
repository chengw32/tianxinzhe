package com.plugin.tianxingzhex.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.plugin.tianxingzhex.R;

import java.util.ArrayList;
import java.util.List;

import tianxingzhe.plugin.utils.Utils.SPUtil;


public class LogDialog extends Dialog {

    private List<SPUtil.LogBean> dataList = new ArrayList();
    private ListView mListview;

    public LogDialog(Context activity) {
        this(activity, R.style.pay_dialog_style);
    }

    public LogDialog(Context activity, int style) {
        super(activity, style);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);
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
        mWindow.setGravity(Gravity.TOP);

        mListview = findViewById(R.id.lv_log);
        findViewById(R.id.bt_clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtil.cleanLogMessage();
                refresh();
            }
        });

        mListview.setAdapter(mAdapter);

    }

    BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return dataList.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View layout = getLayoutInflater().inflate(R.layout.log_item, null);
            TextView message = layout.findViewById(R.id.tv_log_message);
            SPUtil.LogBean logBean = dataList.get(position);
            if (logBean.getMessage().length() > 5 && "false".equals(logBean.getMessage().substring(0, 5))) {
                message.setTextColor(Color.parseColor("#EA4539"));
            }else {
                message.setTextColor(Color.parseColor("#000000"));
            }
            StringBuffer sb = new StringBuffer();
            sb.append("时间：").append(logBean.getTime()).append("\n");
            sb.append(logBean.getMessage());
            message.setText(sb.toString());
            return layout;
        }
    };

    public void refresh() {

        List<SPUtil.LogBean> logMessage = SPUtil.getLogMessage();
        dataList.clear();
        dataList.addAll(logMessage);
        mAdapter.notifyDataSetChanged();
    }
}
