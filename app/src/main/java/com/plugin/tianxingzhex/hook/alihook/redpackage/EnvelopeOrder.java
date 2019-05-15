package com.plugin.tianxingzhex.hook.alihook.redpackage;


public class EnvelopeOrder {
    private String exp;
    private String crowdNo;

    private String creator_alipayAccount;
    private String creator_imgUrl;
    private boolean creator_realFriend;
    private String creator_userId;
    private String creator_userName;

    private String gmtCreateDesc;
    private String receiveDateDesc;
    private String receiveDate;
    private String amount;
    private String receiveAmount;
    private String remark;

    private String receiver_alipayAccount;
    private String receiver_imgUrl;
    private boolean receiver_realFriend;
    private String receiver_userId;
    private String receiver_userName;
    private boolean hasUpload;
    private boolean hasOpen;


    private String link;
    private String socialCardCMsgId;
    private String chatUserId;
    private boolean isGroup;

    private String squeak;
    private int alipayId;
    private PayStatus paystatus;
    private EnvelopeType envelopeType;

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getCrowdNo() {
        return crowdNo;
    }

    public void setCrowdNo(String crowdNo) {
        this.crowdNo = crowdNo;
    }

    public String getCreator_alipayAccount() {
        return creator_alipayAccount;
    }

    public void setCreator_alipayAccount(String creator_alipayAccount) {
        this.creator_alipayAccount = creator_alipayAccount;
    }

    public String getCreator_imgUrl() {
        return creator_imgUrl;
    }

    public void setCreator_imgUrl(String creator_imgUrl) {
        this.creator_imgUrl = creator_imgUrl;
    }

    public boolean isCreator_realFriend() {
        return creator_realFriend;
    }

    public void setCreator_realFriend(boolean creator_realFriend) {
        this.creator_realFriend = creator_realFriend;
    }

    public String getCreator_userId() {
        return creator_userId;
    }

    public void setCreator_userId(String creator_userId) {
        this.creator_userId = creator_userId;
    }

    public String getCreator_userName() {
        return creator_userName;
    }

    public void setCreator_userName(String creator_userName) {
        this.creator_userName = creator_userName;
    }

    public String getGmtCreateDesc() {
        return gmtCreateDesc;
    }

    public void setGmtCreateDesc(String gmtCreateDesc) {
        this.gmtCreateDesc = gmtCreateDesc;
    }

    public String getReceiveDateDesc() {
        return receiveDateDesc;
    }

    public void setReceiveDateDesc(String receiveDateDesc) {
        this.receiveDateDesc = receiveDateDesc;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(String receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReceiver_alipayAccount() {
        return receiver_alipayAccount;
    }

    public void setReceiver_alipayAccount(String receiver_alipayAccount) {
        this.receiver_alipayAccount = receiver_alipayAccount;
    }

    public String getReceiver_imgUrl() {
        return receiver_imgUrl;
    }

    public void setReceiver_imgUrl(String receiver_imgUrl) {
        this.receiver_imgUrl = receiver_imgUrl;
    }

    public boolean isReceiver_realFriend() {
        return receiver_realFriend;
    }

    public void setReceiver_realFriend(boolean receiver_realFriend) {
        this.receiver_realFriend = receiver_realFriend;
    }

    public String getReceiver_userId() {
        return receiver_userId;
    }

    public void setReceiver_userId(String receiver_userId) {
        this.receiver_userId = receiver_userId;
    }

    public String getReceiver_userName() {
        return receiver_userName;
    }

    public void setReceiver_userName(String receiver_userName) {
        this.receiver_userName = receiver_userName;
    }

    public boolean isHasUpload() {
        return hasUpload;
    }

    public void setHasUpload(boolean hasUpload) {
        this.hasUpload = hasUpload;
    }

    public boolean isHasOpen() {
        return hasOpen;
    }

    public void setHasOpen(boolean hasOpen) {
        this.hasOpen = hasOpen;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSocialCardCMsgId() {
        return socialCardCMsgId;
    }

    public void setSocialCardCMsgId(String socialCardCMsgId) {
        this.socialCardCMsgId = socialCardCMsgId;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
        if (isGroup)
            setEnvelopeType(EnvelopeType.GROUP);
        else
            setEnvelopeType(EnvelopeType.PERSONAL);
    }

    public String getSqueak() {
        return squeak;
    }

    public void setSqueak(String squeak) {
        this.squeak = squeak;
        setEnvelopeType(EnvelopeType.SQUEAK);
    }

    public int getAlipayId() {
        return alipayId;
    }

    public void setAlipayId(int alipayId) {
        this.alipayId = alipayId;
    }

    public PayStatus getPaystatus() {
        return paystatus;
    }

    public void setPaystatus(PayStatus paystatus) {
        this.paystatus = paystatus;
    }

    public EnvelopeType getEnvelopeType() {
        return envelopeType;
    }

    public void setEnvelopeType(EnvelopeType envelopeType) {
        this.envelopeType = envelopeType;
    }

    public enum PayStatus {
        UNKNOW("未知"),
        ABNORMAL("异常"),
        FAIL("失败"),
        REPEAT("重复"),
        SUCCESS("成功"),
        OVERTIME("超时"),
        OVERDUE("过期"),
        RECEIVED("被领取");
        private String text;

        PayStatus(String text) {
            this.text = text;
        }

        public int value() {
            return this.ordinal();
        }

        public String text() {
            return text;
        }
    }

    public enum EnvelopeType {
        UNKNOW("未知"),
        PERSONAL("单聊"),
        GROUP("群聊"),
        SQUEAK("吱口令");
        private String text;

        EnvelopeType(String text) {
            this.text = text;
        }

        public int value() {
            return this.ordinal();
        }

        public String text() {
            return text;
        }
    }

    @Override
    public String toString() {
        return
                "\t红包号=" + crowdNo + "\n" +
                        "\t收到金额=" + receiveAmount + "\n" +
                        "\t红包备注=" + remark + "\n" +
                        "\t红包接收者=" + receiver_userId;
    }

    public String toLog() {
        return "EnvelopeOrder{" +
                "exp='" + exp + '\'' +
                ", crowdNo='" + crowdNo + '\'' +
                ", creator_alipayAccount='" + creator_alipayAccount + '\'' +
                ", creator_imgUrl='" + creator_imgUrl + '\'' +
                ", creator_realFriend=" + creator_realFriend +
                ", creator_userId='" + creator_userId + '\'' +
                ", creator_userName='" + creator_userName + '\'' +
                ", gmtCreateDesc='" + gmtCreateDesc + '\'' +
                ", receiveDateDesc='" + receiveDateDesc + '\'' +
                ", receiveDate='" + receiveDate + '\'' +
                ", amount='" + amount + '\'' +
                ", receiveAmount='" + receiveAmount + '\'' +
                ", remark='" + remark + '\'' +
                ", receiver_alipayAccount='" + receiver_alipayAccount + '\'' +
                ", receiver_imgUrl='" + receiver_imgUrl + '\'' +
                ", receiver_realFriend=" + receiver_realFriend +
                ", receiver_userId='" + receiver_userId + '\'' +
                ", receiver_userName='" + receiver_userName + '\'' +
                ", hasUpload=" + hasUpload +
                ", hasOpen=" + hasOpen +
                ", link='" + link + '\'' +
                ", socialCardCMsgId='" + socialCardCMsgId + '\'' +
                ", chatUserId='" + chatUserId + '\'' +
                ", isGroup=" + isGroup +
                ", squeak='" + squeak + '\'' +
                ", alipayId=" + alipayId +
                ", paystatus=" + paystatus +
                ", envelopeType=" + envelopeType +
                '}';
    }
}
