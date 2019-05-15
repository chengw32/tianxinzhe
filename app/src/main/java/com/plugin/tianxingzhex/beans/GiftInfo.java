package com.plugin.tianxingzhex.beans;


public class GiftInfo {

    /**
     * asyncRec : false
     * crowdDuration : 1440
     * extInfo : {}
     * giftCrowdInfo : {"amount":"1.00","canResend":false,"count":0,"creator":{"alipayAccount":"157***6370","imgUrl":"http://tfs.alipayobjects.com/images/partner/TB1uE6zaddFDuNkUvMQXXbVBpXa_160X160","realFriend":true,"userId":"2088302197305093","userName":"陈国武"},"crowdDuration":24,"crowdNo":"201902110206302200000000090036665128","gcashUseAvg":true,"gmtCreateDesc":"今天 19:40","id":0,"prodCode":"CROWD_COMMON_CASH","prodName":"普通红包","remark":"我","totalNumber":0,"withStars":false}
     * guessResult : false
     * hasNextPage : false
     * needCertify : false
     * needRealName : false
     * needWriteMessage : false
     * received : false
     * resultCode : 1000
     * resultDesc : 处理成功
     * success : true
     */

    private boolean asyncRec;
    private int crowdDuration;
    private ExtInfoBean extInfo;
    private GiftCrowdInfoBean giftCrowdInfo;
    private boolean guessResult;
    private boolean hasNextPage;
    private boolean needCertify;
    private boolean needRealName;
    private boolean needWriteMessage;
    private boolean received;
    private String resultCode;
    private String resultDesc;
    private boolean success;

    public boolean isAsyncRec() {
        return asyncRec;
    }

    public void setAsyncRec(boolean asyncRec) {
        this.asyncRec = asyncRec;
    }

    public int getCrowdDuration() {
        return crowdDuration;
    }

    public void setCrowdDuration(int crowdDuration) {
        this.crowdDuration = crowdDuration;
    }

    public ExtInfoBean getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(ExtInfoBean extInfo) {
        this.extInfo = extInfo;
    }

    public GiftCrowdInfoBean getGiftCrowdInfo() {
        return giftCrowdInfo;
    }

    public void setGiftCrowdInfo(GiftCrowdInfoBean giftCrowdInfo) {
        this.giftCrowdInfo = giftCrowdInfo;
    }

    public boolean isGuessResult() {
        return guessResult;
    }

    public void setGuessResult(boolean guessResult) {
        this.guessResult = guessResult;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isNeedCertify() {
        return needCertify;
    }

    public void setNeedCertify(boolean needCertify) {
        this.needCertify = needCertify;
    }

    public boolean isNeedRealName() {
        return needRealName;
    }

    public void setNeedRealName(boolean needRealName) {
        this.needRealName = needRealName;
    }

    public boolean isNeedWriteMessage() {
        return needWriteMessage;
    }

    public void setNeedWriteMessage(boolean needWriteMessage) {
        this.needWriteMessage = needWriteMessage;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class ExtInfoBean {
    }

    public static class GiftCrowdInfoBean {
        /**
         * amount : 1.00
         * canResend : false
         * count : 0
         * creator : {"alipayAccount":"157***6370","imgUrl":"http://tfs.alipayobjects.com/images/partner/TB1uE6zaddFDuNkUvMQXXbVBpXa_160X160","realFriend":true,"userId":"2088302197305093","userName":"陈国武"}
         * crowdDuration : 24
         * crowdNo : 201902110206302200000000090036665128
         * gcashUseAvg : true
         * gmtCreateDesc : 今天 19:40
         * id : 0
         * prodCode : CROWD_COMMON_CASH
         * prodName : 普通红包
         * remark : 我
         * totalNumber : 0
         * withStars : false
         */

        private String amount;
        private boolean canResend;
        private int count;
        private CreatorBean creator;
        private int crowdDuration;
        private String crowdNo;
        private boolean gcashUseAvg;
        private String gmtCreateDesc;
        private int id;
        private String prodCode;
        private String prodName;
        private String remark;
        private int totalNumber;
        private boolean withStars;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public boolean isCanResend() {
            return canResend;
        }

        public void setCanResend(boolean canResend) {
            this.canResend = canResend;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public CreatorBean getCreator() {
            return creator;
        }

        public void setCreator(CreatorBean creator) {
            this.creator = creator;
        }

        public int getCrowdDuration() {
            return crowdDuration;
        }

        public void setCrowdDuration(int crowdDuration) {
            this.crowdDuration = crowdDuration;
        }

        public String getCrowdNo() {
            return crowdNo;
        }

        public void setCrowdNo(String crowdNo) {
            this.crowdNo = crowdNo;
        }

        public boolean isGcashUseAvg() {
            return gcashUseAvg;
        }

        public void setGcashUseAvg(boolean gcashUseAvg) {
            this.gcashUseAvg = gcashUseAvg;
        }

        public String getGmtCreateDesc() {
            return gmtCreateDesc;
        }

        public void setGmtCreateDesc(String gmtCreateDesc) {
            this.gmtCreateDesc = gmtCreateDesc;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getProdCode() {
            return prodCode;
        }

        public void setProdCode(String prodCode) {
            this.prodCode = prodCode;
        }

        public String getProdName() {
            return prodName;
        }

        public void setProdName(String prodName) {
            this.prodName = prodName;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getTotalNumber() {
            return totalNumber;
        }

        public void setTotalNumber(int totalNumber) {
            this.totalNumber = totalNumber;
        }

        public boolean isWithStars() {
            return withStars;
        }

        public void setWithStars(boolean withStars) {
            this.withStars = withStars;
        }

        public static class CreatorBean {
            /**
             * alipayAccount : 157***6370
             * imgUrl : http://tfs.alipayobjects.com/images/partner/TB1uE6zaddFDuNkUvMQXXbVBpXa_160X160
             * realFriend : true
             * userId : 2088302197305093
             * userName : 陈国武
             */

            private String alipayAccount;
            private String imgUrl;
            private boolean realFriend;
            private String userId;
            private String userName;

            public String getAlipayAccount() {
                return alipayAccount;
            }

            public void setAlipayAccount(String alipayAccount) {
                this.alipayAccount = alipayAccount;
            }

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public boolean isRealFriend() {
                return realFriend;
            }

            public void setRealFriend(boolean realFriend) {
                this.realFriend = realFriend;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }
        }
    }
}