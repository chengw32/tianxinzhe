package com.plugin.tianxingzhex.params;



import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.model.BaseParams;

/**
 * Created by chenguowu on 2019/1/15.
 */

public class UpYunShanFuPaySuccessParam extends BaseParams {
    private String API_NAME =  SPUtil.getHostUrl() +"/uppay/post";

    public String usercode ;
    public String orderno ;
    public String qrcode ;
    public String amount ;
    public String sign ;



}
