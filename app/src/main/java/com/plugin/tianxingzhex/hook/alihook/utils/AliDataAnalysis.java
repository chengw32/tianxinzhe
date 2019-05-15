package com.plugin.tianxingzhex.hook.alihook.utils;

/**
 * Created by chenguowu on 2018/12/17.
 */

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Author chenguowu
 * Time 2018/12/17 15:03
 * Des 解析 ali 返回的数据
 */
public class AliDataAnalysis {
    public static HookModul analysis(String messageInfo) {
        if (TextUtils.isEmpty(messageInfo)) return null;

            if (!TextUtils.isEmpty(messageInfo)) {
                String extraInfo = StringHelper.getTextCenter(messageInfo, "extraInfo='", "'");
                HookModul.ExtraInfo extraInfoBean = JSON.parseObject(extraInfo, HookModul.ExtraInfo.class);

                String status = extraInfoBean.getStatus();
                if ("二维码收款到账通知".equals(status) || "收到一笔转账".equals(status) || "收款到账成功".equals(status)) {

                    List<HookModul.ExtraInfo.ActionsBean> actions = extraInfoBean.getActions();
                    for (int i = 0; i < actions.size(); i++) {
                        HookModul.ExtraInfo.ActionsBean actionsBean = actions.get(i);
                        if ("查看详情".equals(actionsBean.getName())) {
                            String orderNum = StringHelper.getTextCenter(actionsBean.getUrl(), "tradeNO=", "&");
                            extraInfoBean.setOrderNo(orderNum);
                        }
                    }

                    String content = StringHelper.getTextCenter(messageInfo, "content='", "'");
                    HookModul.Content contentBean = JSON.parseObject(content, HookModul.Content.class);
                    HookModul modul = new HookModul();
                    modul.setMessageInfo(messageInfo);
                    modul.setContent(contentBean);
                    modul.setExtraInfo(extraInfoBean);
                    return modul;
                }


            }
        return null;
    }
}