package com.plugin.tianxingzhex.params;



import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.model.BaseParams;

/**
 * Created by chenguowu on 2019/1/15.
 */

public class UpWechatParam extends BaseParams {
    private String API_NAME =  SPUtil.getHostUrl() +"/wx/post";

    public String money ;
    public String name ;
    public String time ;
    public String msgId ;
    public String sign ;



}
