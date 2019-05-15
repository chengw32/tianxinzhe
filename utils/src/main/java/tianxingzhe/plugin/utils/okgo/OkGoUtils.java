package tianxingzhe.plugin.utils.okgo;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import tianxingzhe.plugin.utils.Utils.Constants;
import tianxingzhe.plugin.utils.Utils.LogUtil;
import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.model.BaseParams;

/**
 * Created by chenguowu on 2019/1/15.
 * <p>
 * <p>
 * 如果要添加 头部在 CallBack的 onStart 方法里添加
 * 如果要添加 头部在 CallBack的 onStart 方法里添加
 * 如果要添加 头部在 CallBack的 onStart 方法里添加
 * 如果要添加 头部在 CallBack的 onStart 方法里添加
 */

public class OkGoUtils {


    public static <T> void post(Activity act, BaseParams params, OkGoCallBack<T> jsonCallback) {
        OkGo.<T>post(getApiName(params))//
//                .upJson(JSON.toJSONString(parseDataParams(params)))
//                .headers("X-AUTH-TOKEN", SPUtil.getToken())
                .tag(act)// 页面销毁时取消请求是根据这个来判断的
                .params(parseDataParams(params))
//                .isMultipart(true)         //强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .execute(jsonCallback);
    }

    public static <T> void get(Activity act, BaseParams params, OkGoCallBack<T> jsonCallback) {
        OkGo.<T>get(getApiName(params))//
//                .headers("X-AUTH-TOKEN", SPUtil.getToken())
                .tag(act)// 页面销毁时取消请求是根据这个来判断的
//                .params(parseDataParams(params))
//                .isMultipart(true)         //强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .execute(jsonCallback);
    }


    private static Map<String, String> parseDataParams(BaseParams params) {
        if (params == null) {
            return new HashMap<String, String>();
        }
        Class<?> clz = params.getClass();
        return parseFields(params, clz);
    }

    private static Map<String, String> parseFields(Object input, Class<?> clz) {
        HashMap<String, String> dataParams = new HashMap<String, String>();
        // 获取当前类的所有属性，包括public、protected、private的，但是不包括父类的属性
        Field[] subFields = clz.getDeclaredFields();
        parseFieldsToMap(input, subFields, dataParams, false);
        // 获取所有public的属性，包括父类的；
        Field[] publicFields = clz.getFields();
        parseFieldsToMap(input, publicFields, dataParams, true);
        return dataParams;
    }


    private static void parseFieldsToMap(Object input, Field[] fields, HashMap<String, String> dataParams,
                                         boolean checkFieldInMap) {
        if (fields == null || fields.length == 0) {
            return;
        }
        String fieldName = null;
        Object value = null;
        for (int i = 0; i < fields.length; i++) {
            value = null;
            try {
                fieldName = fields[i].getName();
                if (excludeField(fieldName, dataParams, checkFieldInMap)) {
                    /*
                     * 1、如果是内部类，去掉当前相关类引用；
                     * 2、排除api,v,ecode,sid,serialVersionUID等参数；
                     * 3、排除ORIGINALJSON参数；
                     */
                    continue;
                } else {
                    fields[i].setAccessible(true);
                    value = fields[i].get(input);
                }
            } catch (Throwable e) {
            }
            if (value != null) {
                try {
                    if (value instanceof String) {
                        dataParams.put(fieldName, value.toString());
                    } else {
                        dataParams.put(fieldName, JSON.toJSONString(value));
                    }
                } catch (Throwable e) {
                }
            }

        }
    }


    private static boolean excludeField(String fieldName, HashMap<String, String> dataParams, boolean checkFieldInMap) {
        /*
         * 1、如果是内部类，去掉当前相关类引用； 2、排除api,v,ecode,sid,serialVersionUID等参数；
         * 3、排除ORIGINALJSON参数；
         */
        if ((fieldName.indexOf("$") != -1)) {
            /*
             * 1、如果是内部类，去掉当前相关类引用；
             */
            return true;
        } else if (Constants.API_NAME.equals(fieldName)) {
            /*
             * 2、排除api,v,ecode,sid,serialVersionUID等参数；
             */
            return true;
        } else if (checkFieldInMap) {
            /*
             * 判断数据Map重复field； 子类存在重复public属性时，不需要再获取该属性值，
             * 因为class.getFields()会获取所有public的属性，包括父类的；
             * 当子类存在与父类同名的属性时，会返回子类的属性值，
             * 因此，在class.getDeclaredFields()时，就不需要再获取同名属性值了。
             */
            if (dataParams.containsKey(fieldName)) {
                return true;
            }
        }
        return false;
    }

    private static String getApiName(BaseParams o) {
        if (o == null) {
            return null;
        }
        Class<?> c = (Class<?>) o.getClass();
        Field[] fs = c.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true);
            if (f.getName().equals(Constants.API_NAME)) {
                try {
                    String apiname = f.get(o).toString();
                    SPUtil.saveLogMessage("回调地址：" +apiname);
                    return apiname;
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
            }
        }
        return null;
    }

}
