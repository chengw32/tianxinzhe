package tianxingzhe.plugin.utils.okgo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.json.JSONException;
import org.json.JSONObject;

import tianxingzhe.plugin.utils.Utils.LogUtil;
import tianxingzhe.plugin.utils.Utils.SPUtil;
import tianxingzhe.plugin.utils.Utils.ToastUtil;
import tianxingzhe.plugin.utils.beans.BaseBean;

/**
 * Created by chenguowu on 2019/1/19.
 * 如果 activity 为空 则不创建提升进度条提醒
 */

public abstract class OkGoCallBack<T> extends JsonCallback<T> {

    private ProgressDialog dialog;
    private Class<T> clazz;

    public OkGoCallBack(Activity activity, Class<T> tClass) {
        super((Class<T>) String.class);
        clazz = tClass;
        initDialog(activity);
    }

    private void initDialog(Activity activity) {
        if (null == activity || activity.isFinishing()) return;
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onFinish() {
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {
        T t = response.body();
        LogUtil.e("请求结果  " + t.toString());
        SPUtil.saveLogMessage(t.toString());

        try {
            new JSONObject(t.toString());
            BaseBean baseBean = JSON.parseObject(t.toString(), BaseBean.class);
            if (null == baseBean) {
                LogUtil.e("error: ");
                error(response);
                return;

            } else {
                if ("success".equals(baseBean.getCode())) {
                    success(JSON.parseObject(t.toString(), clazz));
                } else {
                    ToastUtil.show(baseBean.getMessage());
                    LogUtil.e("error: " + baseBean.getMessage());
                    error(response);
                }
            }
        } catch (JSONException e) {
            error(response);
        }

    }

    @Override
    public void onError(Response<T> response) {
        super.onError(response);
        SPUtil.saveLogMessage("false", "" + response.body());
        error(response);
    }

    protected abstract void success(T body);

    public void error(com.lzy.okgo.model.Response<T> response) {
    }

}
