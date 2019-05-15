package com.plugin.tianxingzhex.params;



import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.model.BaseParams;

/**
 * Created by chenguowu on 2019/1/15.
 */

public class UpYunShanFuQrcodeParam extends BaseParams {
    private String API_NAME =  SPUtil.getHostUrl() +"/uppay/postqr";

    public String usercode ;
    public String orderno ;
    public String qrcode ;
    public String amount ;
    public String sign ;



}
