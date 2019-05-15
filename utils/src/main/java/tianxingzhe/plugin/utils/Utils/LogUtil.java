package tianxingzhe.plugin.utils.Utils;

import android.util.Log;

/**
 * Created by chenguowu on 2019/1/14.
 */

public class LogUtil {

    private static final String TAG = "hooklog" ;
    public static boolean logIsOpen = true;

    public static void e(String msg){
        if (logIsOpen)
        Log.e(TAG,msg);
    }
    public static void e(String...msg){
        if (logIsOpen)
        {
            StringBuffer stringBuffer = new StringBuffer();
            for (String s : msg) {
                stringBuffer.append(s).append("\n");
            }
        Log.e(TAG,stringBuffer.toString());
        }
    }
    public static void e(String tag, String msg){
        if (logIsOpen)
        Log.e(TAG,msg);
    }

}
