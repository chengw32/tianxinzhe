package com.plugin.tianxingzhex.beans;

public class GroupChatMessage extends ChatMessage {
    private String hintUsers;
    private String senderId;

    public String getHintUsers() {
        return hintUsers;
    }

    public String getSenderId() {
        return senderId;
    }

    @Override
    public String toString() {
        return "GroupChatMessage{" +
                "hintUsers='" + getHintUsers() + '\'' +
                ", senderId='" + getSenderId() + '\'' +
                ", action=" + getAction() +
                ", appId='" + getAppId() + '\'' +
                ", atMe='" + getAtMe() + '\'' +
                ", bizIcon='" + getBizIcon() + '\'' +
                ", bizMemo='" + getBizMemo() + '\'' +
                ", bizRemind='" + getBizRemind() + '\'' +
                ", bizType='" + getBizType() + '\'' +
                ", clientMsgId='" + getClientMsgId() + '\'' +
                ", countAsUnread=" + isCountAsUnread() +
                ", createTime=" + getCreateTime() +
                ", egg='" + getEgg() + '\'' +
                ", errorCode=" + getErrorCode() +
                ", errorMemo='" + getErrorMemo() + '\'' +
                ", extendData='" + getExtendData() + '\'' +
                ", isEggRead=" + isEggRead() +
                ", isResourceUploaded=" + isResourceUploaded() +
                ", link='" + getLink() + '\'' +
                ", loadingState=" + getLoadingState() +
                ", localId=" + getLocalId() +
                ", mSenderUserId='" + getmSenderUserId() + '\'' +
                ", mediaState='" + getMediaState() + '\'' +
                ", msgId=" + getMsgId() +
                ", msgIndex='" + getMsgIndex() + '\'' +
                ", msgOptType='" + getMsgOptType() + '\'' +
                ", recent=" + isRecent() +
                ", scene=" + getScene() +
                ", sendingState=" + getSendingState() +
                ", side=" + getSide() +
                ", templateCode='" + getTemplateCode() + '\'' +
                ", templateData='" + getTemplateData() + '\'' +
                '}';
    }
}