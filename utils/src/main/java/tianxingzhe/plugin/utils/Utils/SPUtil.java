package tianxingzhe.plugin.utils.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tianxingzhe.plugin.utils.application.BaseApplication;

/**
 * Created by lijiangyun on 15/6/3.
 */
public class SPUtil {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "share_data";
    public static final String ACCOUNT_NUM = "account_num";
    public static final String ISFIRST = "isFirst";
    public static final String ISSMSOPEN = "isSMSOpen";//是否开启短信监听
    public static final String HOST_URL = "hosturl";
    public static final String SIGN_KEY = "signkey";
    private static final String APPPUSH_LOG = "log_bean";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static void put(String key, Object object) {

        SharedPreferences sp = BaseApplication.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(String key, Object defaultObject) {
        SharedPreferences sp = BaseApplication.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }


    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }



    //配置的账号
    public static String getAccountNum() {
        return (String) get(ACCOUNT_NUM, "");
    }

    public static void setAccountNum(String banknum) {
        put(ACCOUNT_NUM, banknum);
    }


    //是否是第一次开启
    public static String isFirst() {
        return (String) get(ISFIRST, "0");
    }

    public static void setNotFirst() {
        put(ISFIRST, "1");
    }

    //设置hosturl
    public static String getHostUrl() {
        return (String) get(HOST_URL, "");
    }

    public static void setHostUrl(String hostUrl) {
        put(HOST_URL, hostUrl);
    }
    //设置key
    public static String getSignKey() {
        return (String) get(SIGN_KEY, "");
    }

    public static void setSignKey(String signKey) {
        put(SIGN_KEY, signKey);
    }
    public static void setSMSListener(boolean signKey) {
        put(ISSMSOPEN, signKey);
    }
    public static boolean isSMSOpen() {
        return (Boolean) get(ISSMSOPEN, false);
    }


    public static synchronized void saveLogMessage(String... logMsg) {
        String logmessage = (String) get(APPPUSH_LOG, "");
        if (TextUtils.isEmpty(logmessage)) {
            List<LogBean> emptyList = new ArrayList<>();
            StringBuffer stringBuilder = new StringBuffer();
            for (int i = 0; i < logMsg.length; i++) {
                String s = logMsg[i];
                stringBuilder.append(s);
                if (i != logMsg.length - 1)
                    stringBuilder.append("\n");
            }
            emptyList.add(new LogBean(stringBuilder.toString()));
            put(APPPUSH_LOG, JSON.toJSONString(emptyList));
        } else {

            List<LogBean> logData = JSON.parseArray(logmessage, LogBean.class);
            if (logData.size() > 60) {
                logData.remove(logData.size() - 1);
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < logMsg.length; i++) {
                String s = logMsg[i];
                stringBuilder.append(s);
                if (i != logMsg.length - 1)
                    stringBuilder.append("\n");
            }
            logData.add(0, new LogBean(stringBuilder.toString()));
            put(APPPUSH_LOG, JSON.toJSONString(logData));
        }
    }

    public static void cleanLogMessage() {
        put(APPPUSH_LOG, "");
    }


    public static List getLogMessage() {
        String logmessage = (String) get(APPPUSH_LOG, "");
        if (TextUtils.isEmpty(logmessage)) return new ArrayList();
        return JSON.parseArray(logmessage, LogBean.class);
    }

    /**
     * Created by chenguowu on 2018/12/21.
     */

    public static class LogBean {
       private String time ;
       private String message;

        public LogBean() {
        }

        public LogBean(String message) {
            this.time = new SimpleDateFormat("yyyy年MM月dd日 hh:mm").format(new Date());
            this.message = message;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTime() {
            return time;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
