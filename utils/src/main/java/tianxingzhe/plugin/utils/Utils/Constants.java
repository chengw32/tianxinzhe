package tianxingzhe.plugin.utils.Utils;

/**
 * Created by chenguowu on 2019/1/15.
 */

public class Constants {

    public static final String API_NAME = "API_NAME";

    public static final int MAX_TEMPERATURE = 30;
    public static final int MIN_TEMPERATURE = 16;


    public static String getConnectState(int item){
        switch (item){
            case 0:
                return "未连接";
            case 1:
                return "连接中";
            case 2:
                return "连接成功";
                default:
                    return "连接失败" ;
        }
    }

}
