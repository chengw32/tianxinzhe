package com.plugin.tianxingzhex.beans;

/**
 * Created by chenguowu on 2019/1/18.
 */

public class WechatInfoBean {

    //微信的登录账号
    String wechatLoginId ;
    //微信号
    String wechatName ;
    //定金额的收款码
    String qrCode ;
    //无金额的收款码
    String collectionCode ;

    public String getCollectionCode() {
        return collectionCode;
    }

    public void setCollectionCode(String collectionCode) {
        this.collectionCode = collectionCode;
    }

    public String getWechatLoginId() {
        return wechatLoginId;
    }

    public void setWechatLoginId(String wechatLoginId) {
        this.wechatLoginId = wechatLoginId;
    }

    public String getWechatName() {
        return wechatName;
    }

    public void setWechatName(String wechatName) {
        this.wechatName = wechatName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
