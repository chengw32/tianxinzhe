package com.plugin.tianxingzhex.hook.alihook.redpackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.plugin.tianxingzhex.beans.GiftInfo;
import com.plugin.tianxingzhex.hook.alihook.AliPayHooker;
import com.plugin.tianxingzhex.hook.alihook.DelFrendHelper;
import com.plugin.tianxingzhex.server.BroadCaseSendUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class EnvelopeHooker {
    private Context context;
    private Method openEnvelopeMethod;

    public EnvelopeHooker(Context context) throws ClassNotFoundException, NoSuchMethodException {
        this.context = context;
        ClassLoader classLoader = context.getClassLoader();
        XposedHelpers.findAndHookMethod(Activity.class, "startActivityForResult", Intent.class, int.class, startActivityForResultHooker);
        Class SnsCouponDetailActivityClass = classLoader.loadClass("com.alipay.android.phone.discovery.envelope.get.SnsCouponDetailActivity");
        openEnvelopeMethod = SnsCouponDetailActivityClass.getDeclaredMethod("a", SnsCouponDetailActivityClass, boolean.class, boolean.class);
        openEnvelopeMethod.setAccessible(true);
        XposedHelpers.findAndHookMethod(SnsCouponDetailActivityClass, "b", classLoader.loadClass("com.alipay.giftprod.biz.crowd.gw.result.GiftCrowdDetailResult"), boolean.class, boolean.class, bHooker);
        XposedHelpers.findAndHookMethod(SnsCouponDetailActivityClass, "onDestroy", onDestroyHooker);
    }


    public Context getContext() {
        return context;
    }

    private XC_MethodHook bHooker = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            String giftInfoString = JSON.toJSONString(param.args[0]);
            Log.e("xxxx","giftInfoString："+giftInfoString);
            GiftInfo giftInfo = JSON.parseObject(giftInfoString, GiftInfo.class);
//            boolean canOpen = false;
//            String info = CommonUtils.getIntentInfo(((Activity) param.thisObject).getIntent());

//            String socialCardCMsgId = ((Activity) param.thisObject).getIntent().getStringExtra("socialCardCMsgId");
//            for (OnEnveloperCallback onEnveloperCallback : onEnveloperCallbacks) {
//                if (onEnveloperCallback.canOpenEnveloper(giftInfo, socialCardCMsgId)) {
//                    canOpen = true;
//                    break;
//                }
//            }
//            Log.e("xxxx","canOpen："+canOpen);
//            if (!canOpen) return;
//            EnvelopeRunnable runnable = EnvelopeQueue.getInstance().just();
//            EnvelopeOrder order = runnable.getEnvelopeOrder();
            if ("1000".equals(giftInfo.getResultCode())){
                try {
                    Log.e("xxxx","拆红包");
                    openEnvelopeMethod.invoke(param.thisObject, param.thisObject, false, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("xxxx","拆红包 异常");
                }
            }


//            switch (giftInfo.getResultCode()) {
//                case "1000"://可以领取
////                    try {
////                        Log.e("xxxx","拆红包");
////                        order.setCrowdNo(giftInfo.getGiftCrowdInfo().getCrowdNo());
////                        openEnvelopeMethod.invoke(param.thisObject, param.thisObject, false, true);
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                        Log.e("xxxx","拆红包 异常");
////                    }
//                    return;
//                case "1320"://过期
//                    order.setExp(giftInfoString);
//                    order.setPaystatus(EnvelopeOrder.PayStatus.OVERDUE);
//                    order.setCrowdNo(giftInfo.getGiftCrowdInfo().getCrowdNo());
//                    order.setRemark("红包已经过期");
//                    order.setHasOpen(true);
//                    break;
//                case "1322"://已经被领取
//                    order.setExp(giftInfoString);
//                    order.setPaystatus(EnvelopeOrder.PayStatus.RECEIVED);
//                    order.setCrowdNo(giftInfo.getGiftCrowdInfo().getCrowdNo());
//                    order.setRemark("红包已被领取");
//                    order.setHasOpen(true);
//                    break;
//            }
//            order.setHasOpen(true);
//            EnvelopeOrder envelopeOrder = LiteOrmDBUtil.getGlobalDBUtil(context).queryOneByWhere(EnvelopeOrder.class, "alipayId", order.getAlipayId());
//            if (envelopeOrder == null) {
//                envelopeOrder = order;
//                LiteOrmDBUtil.getGlobalDBUtil(context).save(envelopeOrder);
//            } else {
//                envelopeOrder.setExp(order.getExp());
//                envelopeOrder.setCrowdNo(order.getCrowdNo());
//                envelopeOrder.setRemark(order.getRemark());
//                envelopeOrder.setHasOpen(order.isHasOpen());
//                envelopeOrder.setPaystatus(order.getPaystatus());
//
//                LiteOrmDBUtil.getGlobalDBUtil(context).update(envelopeOrder);
//            }
//            ApiByHttp.getInstance().uploadSqueak(envelopeOrder, new SqueakCallback(context, envelopeOrder.getAlipayId()));
//            for (OnEnveloperCallback onEnveloperCallback : onEnveloperCallbacks) {
//                onEnveloperCallback.onEnveloperEvent(envelopeOrder);
//            }
//            ((Activity) param.thisObject).finish();
        }
    };

    /**
     * 获取红包信息
     */
    private XC_MethodHook startActivityForResultHooker = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) {
            Log.e("xxxx","startActivityForResultHooker");
            if (!param.thisObject.getClass().getName().endsWith("SnsCouponDetailActivity")) return;
            Intent intent = (Intent) param.args[0];
            param.args[0] = null;
            Bundle mExtras = intent.getBundleExtra("mExtras");
            if (mExtras == null) return;
            analysisGift(mExtras.getSerializable("dataSource"));
        }
    };

    /**
     * 解析红包信息
     */
    private void analysisGift(Object giftResult) {
        Log.e("xxxx","analysisGift");
        if (giftResult == null) return;
        String giftInfoJson = JSON.toJSONString(giftResult);
        GiftInfo giftInfo = JSON.parseObject(giftInfoJson, GiftInfo.class);
        //红包拆成功
        Intent broadCastIntent = new Intent();
        broadCastIntent.putExtra(AliPayHooker.DATA_RED_PACKAGE, giftInfoJson);
        BroadCaseSendUtil.backAliData(context,broadCastIntent);


        DelFrendHelper.del(context.getClassLoader(),giftInfo.getGiftCrowdInfo().getCreator().getUserId());

        Log.e("hooklog","analysisGift: "+giftInfoJson);
//        GiftInfo.GiftCrowdInfoBean giftCrowdFlowInfo = giftInfo.getGiftCrowdInfo();
//        EnvelopeRunnable envelopeRunnable = EnvelopeQueue.getInstance().just();
//        if (envelopeRunnable == null || !giftCrowdFlowInfo.getCrowdNo().equals(envelopeRunnable.getEnvelopeOrder().getCrowdNo()))
//            return;
//        GiftInfo.GiftCrowdInfoBean giftCrowdInfo = giftInfo.getGiftCrowdInfo();
//        EnvelopeOrder envelopeOrder = LiteOrmDBUtil.getGlobalDBUtil(context).queryOneByWhere(EnvelopeOrder.class, "crowdNo", envelopeRunnable.getEnvelopeOrder().getCrowdNo());
//        if (envelopeOrder == null) envelopeOrder = envelopeRunnable.getEnvelopeOrder();
//        envelopeOrder.setExp(giftInfoJson);
//        envelopeOrder.setCreator_alipayAccount(giftCrowdInfo.getCreator().getAlipayAccount());
//        envelopeOrder.setCreator_imgUrl(giftCrowdInfo.getCreator().getImgUrl());
//        envelopeOrder.setCreator_realFriend(giftCrowdInfo.getCreator().isRealFriend());
//        envelopeOrder.setCreator_userId(giftCrowdInfo.getCreator().getUserId());
//        envelopeOrder.setCreator_userName(giftCrowdInfo.getCreator().getUserName());
//        envelopeOrder.setGmtCreateDesc(giftCrowdInfo.getGmtCreateDesc());
//        envelopeOrder.setReceiveDateDesc(giftCrowdFlowInfo.getReceiveDateDesc());
//        envelopeOrder.setAmount(giftCrowdInfo.getAmount());
//        envelopeOrder.setReceiveAmount(giftCrowdFlowInfo.getReceiveAmount());
//        envelopeOrder.setRemark(giftCrowdInfo.getRemark());
//        envelopeOrder.setReceiver_alipayAccount(giftCrowdFlowInfo.getReceiver().getAlipayAccount());
//        envelopeOrder.setReceiver_imgUrl(giftCrowdFlowInfo.getReceiver().getImgUrl());
//        envelopeOrder.setReceiver_realFriend(giftCrowdFlowInfo.getReceiver().isRealFriend());
//        envelopeOrder.setReceiver_userId(giftCrowdFlowInfo.getReceiver().getUserId());
//        envelopeOrder.setReceiver_userName(giftCrowdFlowInfo.getReceiver().getUserName());
//        envelopeOrder.setHasOpen(true);
//        envelopeOrder.setPaystatus(EnvelopeOrder.PayStatus.SUCCESS);
//        envelopeRunnable.getEnvelopeOrder().setPaystatus(EnvelopeOrder.PayStatus.SUCCESS);
//        LiteOrmDBUtil.getGlobalDBUtil(context).update(envelopeOrder);
//        EnvelopeQueue.getInstance().pop();
//        for (OnEnveloperCallback onEnveloperCallback : onEnveloperCallbacks) {
//            onEnveloperCallback.onEnveloperEvent(envelopeOrder);
//        }
    }

    private XC_MethodHook onDestroyHooker = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            for (OnEnveloperCallback onEnveloperCallback : onEnveloperCallbacks) {
                onEnveloperCallback.next();
            }
        }
    };

    private List<OnEnveloperCallback> onEnveloperCallbacks = new ArrayList<>(2);

    public void addEnveloperCallbacks(OnEnveloperCallback onEnveloperCallback) {
        this.onEnveloperCallbacks.add(onEnveloperCallback);
    }

    public void removeEnveloperCallbacks(OnEnveloperCallback onEnveloperCallback) {
        this.onEnveloperCallbacks.remove(onEnveloperCallback);
    }


    public interface OnEnveloperCallback {
        void onEnveloperEvent(EnvelopeOrder envelopeOrder);

        boolean canOpenEnveloper(GiftInfo giftInfo, String socialCardCMsgId);

        void next();
    }
}
