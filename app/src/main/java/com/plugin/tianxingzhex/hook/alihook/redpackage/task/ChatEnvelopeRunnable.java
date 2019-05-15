package com.plugin.tianxingzhex.hook.alihook.redpackage.task;

import android.net.Uri;
import android.util.Log;


import com.plugin.tianxingzhex.hook.alihook.AlipayUtils;
import com.plugin.tianxingzhex.hook.alihook.redpackage.EnvelopeOrder;

import java.lang.reflect.Method;

import tianxingzhe.plugin.utils.Utils.LogUtil;

public class ChatEnvelopeRunnable extends EnvelopeRunnable {
    private Object SchemeServiceImpl;
    private Method processMethod;
    private ClassLoader classLoader;

    public ChatEnvelopeRunnable(EnvelopeOrder envelopeOrder, ClassLoader classLoader, Object schemeServiceImpl, Method processMethod) {
        super(envelopeOrder);
        this.classLoader = classLoader;
        this.SchemeServiceImpl = schemeServiceImpl;
        this.processMethod = processMethod;
    }

    @Override
    public void run() {
        try {
//            if (!ApplicationUtil.isRunningForeground(handle.getContext())) {
//                ApplicationUtil.moveToTop((Activity) handle.getContext());
//                Thread.sleep(3000);
//            }
            if (SchemeServiceImpl != null && processMethod != null) {

                LogUtil.e("打开红包页面");
                StringBuilder sb = new StringBuilder(envelopeOrder.getLink());
                sb.append("&socialCardCMsgId=");
                sb.append(envelopeOrder.getSocialCardCMsgId());
                sb.append("&socialCardToUserId=");
                sb.append(envelopeOrder.getChatUserId());
                if (envelopeOrder.isGroup()) {
                    sb.append("&chatUserId=");
                    sb.append(envelopeOrder.getChatUserId());
                    sb.append("&chatUserType=");
                    sb.append(2);
                } else {
                    sb.append("&chatUserId=");
                    sb.append(AlipayUtils.getUserId(classLoader));
                    sb.append("&chatUserType=");
                    sb.append(1);
                }
                try {
                    Log.e("xxxx","run sb.tostring= "+sb.toString());
                    processMethod.invoke(SchemeServiceImpl, Uri.parse(sb.toString()));
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }
}