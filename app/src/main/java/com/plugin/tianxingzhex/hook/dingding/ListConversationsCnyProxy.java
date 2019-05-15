package com.plugin.tianxingzhex.hook.dingding;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tianxingzhe.plugin.utils.Utils.LogUtil;

public class ListConversationsCnyProxy implements InvocationHandler {


    public ListConversationsCnyProxy( ) {

    }

    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        XposedBridge.log("invoke ListConversationsCnyImpl method: " + method.getName());
        if (method.getName().contains("toString")) {
            return "this is string";
        }
        if (method.getName().equals("onSuccess")){
            try {
                XposedBridge.log(" 结果"+ Arrays.toString(objArr));
                LogUtil.e(" 结果"+ Arrays.toString(objArr));
                List list = (List) objArr[0];
                if (list != null && list.size() > 0) {
                    Iterator it = list.iterator();
                    if (it.hasNext()) {
                    RimetHook.mCid = XposedHelpers.getObjectField(it.next(), "mCid").toString();
//                    PayHelperUtils.sendLoginId(RimetHook.currentUid,"dingding", RimetHook.context);
                        LogUtil.e("cid"+RimetHook.mCid);
                    }
                }

            }catch (Exception e){
                XposedBridge.log(e);

            }
        }
        return null;
    }
}