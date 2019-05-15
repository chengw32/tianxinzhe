package com.plugin.tianxingzhex.hook.alihook.redpackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


import com.alibaba.fastjson.JSON;
import com.plugin.tianxingzhex.beans.ChatMessage;
import com.plugin.tianxingzhex.beans.CollectionLinkBean;
import com.plugin.tianxingzhex.beans.GiftInfo;
import com.plugin.tianxingzhex.beans.GroupChatMessage;
import com.plugin.tianxingzhex.hook.alihook.AliPayHooker;
import com.plugin.tianxingzhex.hook.alihook.AlipayUtils;
import com.plugin.tianxingzhex.hook.alihook.redpackage.queue.EnvelopeQueue;
import com.plugin.tianxingzhex.hook.alihook.redpackage.task.ChatEnvelopeRunnable;
import com.plugin.tianxingzhex.hook.alihook.redpackage.task.EnvelopeRunnable;
import com.plugin.tianxingzhex.server.BroadCaseSendUtil;
import com.plugin.tianxingzhex.utils.ThreadPoolUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class EnvelopeHelper implements EnvelopeHooker.OnEnveloperCallback {
    private ClassLoader classLoader;
    private Context context;
    private boolean startEnvelope;
    private Field singlefField;
    private Field groupfField;

    public EnvelopeHelper(EnvelopeHooker envelopeHooker) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        this.context = envelopeHooker.getContext();
        this.classLoader = context.getClassLoader();

        envelopeHooker.addEnveloperCallbacks(this);

        Class ChatMsgDaoOpClass = XposedHelpers.findClass("com.alipay.mobile.socialcommonsdk.bizdata.chat.data.ChatMsgDaoOp", classLoader);
        singlefField = ChatMsgDaoOpClass.getDeclaredField("f");
        singlefField.setAccessible(true);
        XposedBridge.hookAllMethods(ChatMsgDaoOpClass, "saveMessages", receiveSingleMsgHooker);

        Class GroupChatMsgDaoOpClass = XposedHelpers.findClass("com.alipay.mobile.socialcommonsdk.bizdata.chat.data.GroupChatMsgDaoOp", classLoader);
        groupfField = GroupChatMsgDaoOpClass.getDeclaredField("f");
        groupfField.setAccessible(true);
        XposedBridge.hookAllMethods(GroupChatMsgDaoOpClass, "saveMessages", receiveGroupMsgHooker);

        initHistory();
    }

    private Activity topActivity;

    //////聊天红包//////聊天红包//////聊天红包//////聊天红包//////聊天红包//////聊天红包//////聊天红包//////

    private XC_MethodHook.Unhook unhook;

    private void initHistory() {
        unhook = XposedHelpers.findAndHookMethod("com.alipay.mobile.quinox.LauncherActivity", classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                unhook.unhook();
                unhook = null;
                //历史任务
//                List<EnvelopeOrder> envelopeOrders = LiteOrmDBUtil.getGlobalDBUtil(context).queryByWhere(EnvelopeOrder.class, "has_upload", false);
//                for (EnvelopeOrder envelopeOrder : envelopeOrders) {
//                    if (envelopeOrder.getEnvelopeType() != EnvelopeOrder.EnvelopeType.SQUEAK) {
//                        ApiByHttp.getInstance().uploadEnvelope(envelopeOrder, new EnvelopeCallback(context, envelopeOrder.getSocialCardCMsgId()));
//                        continue;
//                    }
//                }
//                envelopeOrders = LiteOrmDBUtil.getGlobalDBUtil(context).queryByWhere(EnvelopeOrder.class, "has_open", false);
//                if (envelopeOrders == null || envelopeOrders.size() == 0) return;
//                for (EnvelopeOrder envelopeOrder : envelopeOrders) {
//                    if (envelopeOrder.getEnvelopeType() != EnvelopeOrder.EnvelopeType.SQUEAK) {
//                        openEnvelope(envelopeOrder.getLink(), envelopeOrder.getSocialCardCMsgId(), envelopeOrder.isGroup(), envelopeOrder.getChatUserId());
//                    }
//                }
            }
        });
    }

    private XC_MethodHook receiveSingleMsgHooker = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            Log.e("xxxx", "收到红包");
            List<ChatMessage> msgs = AlipayChatUtils.parserSingleChatMessage((List<Object>) param.args[0]);
            for (ChatMessage msg : msgs)
                if ("107".equals(msg.getTemplateCode()) && msg.getAction() == 3) {
                    AlipayUtils.startAlipay(context);
                    Thread.sleep(1500);
                    openEnvelope(msg.getLink(), msg.getClientMsgId(), false, (String) singlefField.get(param.thisObject));
                } else if ("105".equals(msg.getTemplateCode())) {

                    //收款码生成后收到消息 发回app 提交服务器
                    String link = msg.getLink();
                    Log.e("hooklog","生成收款单 放回app 准备提交服务器 link: "+link);
                    Intent collectionIntent = new Intent();
                    CollectionLinkBean templateData = JSON.parseObject(msg.getTemplateData(), CollectionLinkBean.class);
                    Log.e("hooklog", "midTitle: " + templateData.getMidTitle());
                    collectionIntent.putExtra(AliPayHooker.COLLECTION_LINK, link);
                    //备注
                    String mark = templateData.getMidTitle();
                    collectionIntent.putExtra("mark",mark);
                    if (!ThreadPoolUtils.cacheLink.containsKey(mark))
                    ThreadPoolUtils.cacheLink.put(mark,link);
                    BroadCaseSendUtil.backAliData(context, collectionIntent);
                    //移除队列
//                ThreadPoolUtils.remove(templateData.getMidTitle());
                }
//                else if ("11".equals(msg.getTemplateCode())) {
//                    Log.e("hooklog", "startActivity: SocialPersonalActivity_");
//                    Intent intent = new Intent(context, XposedHelpers.findClass("com.alipay.android.phone.personalapp.socialpayee.ui.SocialPersonalActivity_", classLoader));
//                    String userid = msg.getClientMsgId().substring(0, msg.getClientMsgId().indexOf("@"));
//                    Log.e("hooklog", "userid: " + userid);
//                    intent.putExtra("userId", userid);
//                    intent.putExtra("money", "0.1");
//                    intent.putExtra("mark", "xx23");
//                    context.startActivity(intent);
//                }
        }
    };

    private XC_MethodHook receiveGroupMsgHooker = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            List<GroupChatMessage> msgs = AlipayChatUtils.parserGroupChatMessage((List<Object>) param.args[0]);
            for (GroupChatMessage msg : msgs)
                if ("107".equals(msg.getTemplateCode()) && msg.getAction() == 3) {
                    AlipayUtils.startAlipay(context);
                    openEnvelope(msg.getLink(), msg.getClientMsgId(), true, (String) groupfField.get(param.thisObject));
                }
        }
    };

    public void startEnvelope() {
        startEnvelope = true;
        for (Object[] objects : pausedList) {
            try {
                openEnvelope((String) objects[0], (String) objects[1], (boolean) objects[2], (String) objects[3]);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void pauseEnvelope() {
        startEnvelope = false;
    }

    Object SchemeServiceImpl;
    Method processMethod;

    private void initSchemeServiceImpl() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (SchemeServiceImpl == null) {
            Class SchemeServiceImplClass = XposedHelpers.findClass("com.alipay.mobile.framework.service.common.impl.SchemeServiceImpl", classLoader);
            processMethod = SchemeServiceImplClass.getDeclaredMethod("process", Uri.class);
            Class AlipayApplicationClass = XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", classLoader);
            Method getInstanceMethod = AlipayApplicationClass.getDeclaredMethod("getInstance");
            Object alipayApplication = getInstanceMethod.invoke(null);
            Method getMicroApplicationContextMethod = AlipayApplicationClass.getMethod("getMicroApplicationContext");
            Object microApplicationContext = getMicroApplicationContextMethod.invoke(alipayApplication);
            Method findServiceByInterfaceMethod = microApplicationContext.getClass().getDeclaredMethod("findServiceByInterface", String.class);
            SchemeServiceImpl = findServiceByInterfaceMethod.invoke(microApplicationContext, "com.alipay.mobile.framework.service.common.SchemeService");
        }
    }

    private List<Object[]> pausedList = new ArrayList<>();

    public synchronized void openEnvelope(String link, String clientMsgId, boolean isGroup, String chatUserId) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Log.e("xxxx", "link:" + link);
//        if (!startEnvelope) {
//            pausedList.add(new Object[]{link, clientMsgId, isGroup, chatUserId});
//            return;
//        }
        initSchemeServiceImpl();
//        EnvelopeOrder chatOrder = LiteOrmDBUtil.getGlobalDBUtil(context).queryOneByWhere(EnvelopeOrder.class, "msg_id", clientMsgId);
//        if (chatOrder == null) {
        EnvelopeOrder chatOrder = new EnvelopeOrder();
        chatOrder.setLink(link);
        chatOrder.setSocialCardCMsgId(clientMsgId);
        chatOrder.setChatUserId(chatUserId);
        chatOrder.setGroup(isGroup);
//            LiteOrmDBUtil.getGlobalDBUtil(context).save(chatOrder);
//        } else {
//            if (chatOrder.isHasOpen()) return;
//        }
        Log.e("xxxx", "openEnvelope");
        Log.e("xxxx", "link:" + link);
        new Thread(new ChatEnvelopeRunnable(chatOrder, classLoader, SchemeServiceImpl, processMethod)).start();
//        EnvelopeQueue.getInstance().add(new ChatEnvelopeRunnable(chatOrder, classLoader, SchemeServiceImpl, processMethod) );
    }

    @Override
    public void onEnveloperEvent(EnvelopeOrder envelopeOrder) {
//        if (envelopeOrder.getEnvelopeType() != EnvelopeOrder.EnvelopeType.SQUEAK) {
//            ApiByHttp.getInstance().uploadEnvelope(envelopeOrder, new EnvelopeCallback(context, envelopeOrder.getSocialCardCMsgId()));
//            if (envelopeOrder.isGroup())
//                handle.noticeMessage("收到群组红包：" + envelopeOrder);
//            else
//                handle.noticeMessage("收到个人红包：" + envelopeOrder);
//        }
    }

    @Override
    public void next() {
//        if (EnvelopeQueue.getInstance().size() == 0) {
//            List<EnvelopeOrder> envelopeOrders = LiteOrmDBUtil.getGlobalDBUtil(context).queryByWhere(EnvelopeOrder.class, "has_open", false);
//            if (envelopeOrders == null || envelopeOrders.size() == 0) return;
//            for (EnvelopeOrder envelopeOrder : envelopeOrders) {
//                if (envelopeOrder.getEnvelopeType() != EnvelopeOrder.EnvelopeType.SQUEAK) {
//                    try {
//                        openEnvelope(envelopeOrder.getLink(), envelopeOrder.getSocialCardCMsgId(), envelopeOrder.isGroup(), envelopeOrder.getChatUserId());
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } else
//            EnvelopeQueue.getInstance().next();
    }

    @Override
    public boolean canOpenEnveloper(GiftInfo giftInfo, String socialCardCMsgId) {
        EnvelopeRunnable runnable = EnvelopeQueue.getInstance().just();
        if (runnable == null) return false;
        if (runnable.getEnvelopeOrder().getEnvelopeType() != EnvelopeOrder.EnvelopeType.SQUEAK) {
            if (runnable.getEnvelopeOrder().getSocialCardCMsgId().equals(socialCardCMsgId))
                return true;
        }
        return false;
    }
}