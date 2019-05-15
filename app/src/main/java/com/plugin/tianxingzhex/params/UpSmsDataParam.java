package com.plugin.tianxingzhex.params;



import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.model.BaseParams;

/**
 * Created by chenguowu on 2019/1/15.
 */

public class UpSmsDataParam extends BaseParams {
    private String API_NAME =  SPUtil.getHostUrl() + "/notify/sms";

    public String usercode ;
    public String content ;
    public String sign ;

    @Override
    public String toString() {
        return "UpSmsDataParam{" +
                "API_NAME='" + API_NAME + '\'' +
                ", usercode='" + usercode + '\'' +
                ", content='" + content + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
