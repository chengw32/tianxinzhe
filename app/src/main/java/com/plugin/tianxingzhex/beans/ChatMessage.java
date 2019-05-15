package com.plugin.tianxingzhex.beans;


public class ChatMessage {
    private int action;
    private String appId;
    private String atMe;
    private String bizIcon;
    private String bizMemo;
    private String bizRemind;
    private String bizType;
    private String clientMsgId;
    private boolean countAsUnread;
    private long createTime;
    private String egg;
    private int errorCode;
    private String errorMemo;
    private String extendData;
    private boolean isEggRead;
    private boolean isResourceUploaded;
    private String link;
    private int loadingState;
    private long localId;
    private String mSenderUserId;
    private String mediaState;//= "{\"expressionState\":0,\"audioState\":0,\"gameState\":0}";
    private long msgId;
    private String msgIndex;
    private String msgOptType;
    private boolean recent;
    private int scene;
    private int sendingState;
    private int side;
    private String templateCode;
    private String templateData;

    public int getAction() {
        return action;
    }

    public String getAppId() {
        return appId;
    }

    public String getAtMe() {
        return atMe;
    }

    public String getBizIcon() {
        return bizIcon;
    }

    public String getBizMemo() {
        return bizMemo;
    }

    public String getBizRemind() {
        return bizRemind;
    }

    public String getBizType() {
        return bizType;
    }

    public String getClientMsgId() {
        return clientMsgId;
    }

    public boolean isCountAsUnread() {
        return countAsUnread;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getEgg() {
        return egg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMemo() {
        return errorMemo;
    }

    public String getExtendData() {
        return extendData;
    }

    public boolean isEggRead() {
        return isEggRead;
    }

    public boolean isResourceUploaded() {
        return isResourceUploaded;
    }

    public String getLink() {
        return link;
    }

    public int getLoadingState() {
        return loadingState;
    }

    public long getLocalId() {
        return localId;
    }

    public String getmSenderUserId() {
        return mSenderUserId;
    }

    public String getMediaState() {
        return mediaState;
    }

    public long getMsgId() {
        return msgId;
    }

    public String getMsgIndex() {
        return msgIndex;
    }

    public String getMsgOptType() {
        return msgOptType;
    }

    public boolean isRecent() {
        return recent;
    }

    public int getScene() {
        return scene;
    }

    public int getSendingState() {
        return sendingState;
    }

    public int getSide() {
        return side;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getTemplateData() {
        return templateData;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "action=" + action +
                ", appId='" + appId + '\'' +
                ", atMe='" + atMe + '\'' +
                ", bizIcon='" + bizIcon + '\'' +
                ", bizMemo='" + bizMemo + '\'' +
                ", bizRemind='" + bizRemind + '\'' +
                ", bizType='" + bizType + '\'' +
                ", clientMsgId='" + clientMsgId + '\'' +
                ", countAsUnread=" + countAsUnread +
                ", createTime=" + createTime +
                ", egg='" + egg + '\'' +
                ", errorCode=" + errorCode +
                ", errorMemo='" + errorMemo + '\'' +
                ", extendData='" + extendData + '\'' +
                ", isEggRead=" + isEggRead +
                ", isResourceUploaded=" + isResourceUploaded +
                ", link='" + link + '\'' +
                ", loadingState=" + loadingState +
                ", localId=" + localId +
                ", mSenderUserId='" + mSenderUserId + '\'' +
                ", mediaState='" + mediaState + '\'' +
                ", msgId=" + msgId +
                ", msgIndex='" + msgIndex + '\'' +
                ", msgOptType='" + msgOptType + '\'' +
                ", recent=" + recent +
                ", scene=" + scene +
                ", sendingState=" + sendingState +
                ", side=" + side +
                ", templateCode='" + templateCode + '\'' +
                ", templateData='" + templateData + '\'' +
                '}';
    }
}