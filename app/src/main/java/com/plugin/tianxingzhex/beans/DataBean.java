package com.plugin.tianxingzhex.beans;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by chenguowu on 2018/12/24.
 */

@Entity
public class DataBean implements Serializable {

    static final long serialVersionUID = 536871008;

    @Id
    private Long id;

    // 订单号
    private String orderNum;
    // 列表上显示数据
    private String showData;
    // money
    private String money;
    // mark
    private String mark;
    // 支付时间
    @OrderBy
    private String time;

    // 订单状态 0 初始状态 1 确认完成 -1 确认失败
    private int orderState;


    //预留字段
    // 登录的账号id
    private String accountId;
    // 登录的账号
    private String account;
    // ali支付账号id
    private String aliPayId;
    // 服务器返回的数据
    private String backMessage;
    private String message1;
    private String message2;
    private String message3;
    private String message4;
    @Generated(hash = 1780520712)
    public DataBean(Long id, String orderNum, String showData, String money,
            String mark, String time, int orderState, String accountId,
            String account, String aliPayId, String backMessage, String message1,
            String message2, String message3, String message4) {
        this.id = id;
        this.orderNum = orderNum;
        this.showData = showData;
        this.money = money;
        this.mark = mark;
        this.time = time;
        this.orderState = orderState;
        this.accountId = accountId;
        this.account = account;
        this.aliPayId = aliPayId;
        this.backMessage = backMessage;
        this.message1 = message1;
        this.message2 = message2;
        this.message3 = message3;
        this.message4 = message4;
    }
    @Generated(hash = 908697775)
    public DataBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOrderNum() {
        return this.orderNum;
    }
    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
    public String getShowData() {
        return this.showData;
    }
    public void setShowData(String showData) {
        this.showData = showData;
    }
    public String getMoney() {
        return this.money;
    }
    public void setMoney(String money) {
        this.money = money;
    }
    public String getMark() {
        return this.mark;
    }
    public void setMark(String mark) {
        this.mark = mark;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getOrderState() {
        return this.orderState;
    }
    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }
    public String getAccountId() {
        return this.accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getAccount() {
        return this.account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getAliPayId() {
        return this.aliPayId;
    }
    public void setAliPayId(String aliPayId) {
        this.aliPayId = aliPayId;
    }
    public String getBackMessage() {
        return this.backMessage;
    }
    public void setBackMessage(String backMessage) {
        this.backMessage = backMessage;
    }
    public String getMessage1() {
        return this.message1;
    }
    public void setMessage1(String message1) {
        this.message1 = message1;
    }
    public String getMessage2() {
        return this.message2;
    }
    public void setMessage2(String message2) {
        this.message2 = message2;
    }
    public String getMessage3() {
        return this.message3;
    }
    public void setMessage3(String message3) {
        this.message3 = message3;
    }
    public String getMessage4() {
        return this.message4;
    }
    public void setMessage4(String message4) {
        this.message4 = message4;
    }

}
