package tianxingzhe.plugin.utils.application;

import android.app.Application;

/**
 * Created by chenguowu on 2019/2/14.
 */
public class BaseApplication extends Application {
    /**
     * The context.
     */
    public static Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
