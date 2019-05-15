package com.plugin.tianxingzhex.utils;


import android.content.Context;
import android.os.Handler;
import android.util.Log;


import com.plugin.tianxingzhex.runnables.GetCollectionLinkRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chenguowu on 2019/2/26.
 */
public class ThreadPoolUtils {
    static final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    public static void executor(Runnable runnable){
        singleThreadExecutor.execute(runnable);
    }


    static Map<String, Map<Handler, Runnable>> tasks = new HashMap();
    static Map<String, String> ids = new HashMap();
    //收款码链接缓存
    public static Map<String, String> cacheLink = new ConcurrentHashMap<>();


    public static void excute(String tag, String userId, Context context) {
//        if (tasks.containsKey(tag))return;
        ids.put(tag, userId);

//        Log.e("hooklog", "放入等待队列：" + tag);
//        Handler handle = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                Intent collectionIntent = new Intent();
//                collectionIntent.setAction(AliPayHooker.ADATA_BACK_CLIIECTION_NO);
//                collectionIntent.putExtra("link", "delete.alipay.friend");
//                //备注
//                Log.e("hooklog", "放入等待队列：" + tag);
//                collectionIntent.putExtra("userId", ids.get(userId));
//                context.sendBroadcast(collectionIntent);
//                Log.e("hooklog", "收款码回传失败:  等待时间已到移除队列：" + tag);
//                tasks.remove(tag);
//            }
//        };
//        Log.e("hooklog", "加入等待队列:  " + tag);
//        handle.postDelayed(runnable, 4000);
//        Map<Handler, Runnable> map = new HashMap<>();
//        map.put(handle, runnable);
//        tasks.put(tag, map);
    }

    public static void remove(String tag) {
        Log.e("hooklog", "移除等待队列：" + tag);
        for (String entries : tasks.keySet()) {
            if (tag.equals(entries)) {
                Log.e("hooklog", "队列存在任务");
                Map<Handler, Runnable> handlerRunnableMap = tasks.get(tag);
                for (Map.Entry<Handler, Runnable> entry : handlerRunnableMap.entrySet()) {
                    Handler key = entry.getKey();
                    Runnable value = entry.getValue();
                    if (null != key && value != null)
                        key.removeCallbacks(value);

                    Log.e("hooklog", "移除任务 单号:  " + tag);
                    key = null;
                    value = null;
                    tasks.remove(tag);
                }
                return;
            }


        }
    }

    /**
     * Author chenguowu
     * Time 2019/3/12 17:06
     * Des 数据大于30条 清空
     */
    public static void cleanLinkMap() {
        if (cacheLink.size() > 30) cacheLink.clear();
    }


    public  static void getCollectionLink(GetCollectionLinkRunnable mRunnable){
        executor(mRunnable);
    }

}
