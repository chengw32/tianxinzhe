package com.plugin.tianxingzhex.params;



import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.model.BaseParams;

/**
 * Created by chenguowu on 2019/1/15.
 */

public class UpCollectionParam extends BaseParams {
    private String API_NAME =  SPUtil.getHostUrl() +"/alipay/posttradeno";

    public String usercode ;
    public String orderno ;
    public String payurl ;
    public String sign ;



}
