package com.plugin.tianxingzhex.params;


import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.model.BaseParams;

/**
 * Created by chenguowu on 2019/1/15.
 */

public class UpDataParam extends BaseParams {
    private String API_NAME = SPUtil.getHostUrl() + "/alipay/hbpost";

    public String sign;
    public String amount;
    public String orderno;
    public String paytime;
    public String usercode;
    public String userid;


}
