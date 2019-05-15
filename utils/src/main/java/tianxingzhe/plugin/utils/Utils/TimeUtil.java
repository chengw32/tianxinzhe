package tianxingzhe.plugin.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenguowu on 2019/2/15.
 */
public class TimeUtil {
    public static String getCurrentTime(){
        return  new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
    }
}
