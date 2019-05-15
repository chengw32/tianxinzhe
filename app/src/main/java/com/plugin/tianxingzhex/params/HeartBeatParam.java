package com.plugin.tianxingzhex.params;



import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.model.BaseParams;

/**
 * Created by chenguowu on 2019/1/15.
 */

public class HeartBeatParam extends BaseParams {
    private String API_NAME =  SPUtil.getHostUrl() +"/qrcode/active";

    public String data ;
    public String sign ;



}
