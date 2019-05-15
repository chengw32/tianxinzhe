package com.plugin.tianxingzhex.hook.alihook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class PermissionUtil extends Activity {
    private String operation;
    private boolean isOverlays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        operation = getIntent().getStringExtra("operation");
        Toast.makeText(this, "请授予" + ApplicationUtil.getAppName(this) + "重要权限", Toast.LENGTH_LONG).show();
        if (!StringUtil.isEmpty(operation)) {
            int requestCode = getIntent().getIntExtra("requestCode", -1);
            startActivityForResult(new Intent(operation), requestCode);
            return;
        }
        isOverlays = getIntent().getBooleanExtra("isOverlays", false);
        if (isOverlays) {
            int requestCode = getIntent().getIntExtra("requestCode", -1);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION), requestCode);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int requestCode = getIntent().getIntExtra("requestCode", -1);
            String[] permissions = getIntent().getStringArrayExtra("permissions");
            if (permissions == null) {
                finish();
                return;
            }
            requestPermissions(permissions, requestCode);
        } else {
            finish();
        }
    }

    abstract public static class OnPermissionCallback {
        abstract protected void onGranted();

        protected void onDisGranted() {
        }
    }

    public static boolean checkOperationPermission(Context context, String operation) {
        if (Build.VERSION.SDK_INT > 19) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(operation, android.os.Process.myUid(), context.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        }
        return true;
    }

    public static boolean checkOverlaysPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23)
            return Settings.canDrawOverlays(context);
        return true;
    }

    public static synchronized void requestOverlaysPermissions(Context context, OnPermissionCallback callback) {
        if (Build.VERSION.SDK_INT < 23) return;
        int requestCode = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        if (callback != null)
            permissionCallbackMap.put(requestCode, new WeakReference<>(callback));
        Intent intent = new Intent(context, PermissionUtil.class);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("isOverlays", true);
        context.startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static synchronized void requestOperationPermissions(Context context, OnPermissionCallback callback, String operation) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        int requestCode = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        if (callback != null)
            permissionCallbackMap.put(requestCode, new WeakReference<>(callback));
        Intent intent = new Intent(context, PermissionUtil.class);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("operation", operation);
        context.startActivity(intent);
    }

    public static boolean checkPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static HashMap<Integer, WeakReference<OnPermissionCallback>> permissionCallbackMap = new HashMap<>(1);

    public static synchronized void requestPermissions(Context context, OnPermissionCallback callback, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        int requestCode = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        if (callback != null)
            permissionCallbackMap.put(requestCode, new WeakReference<>(callback));
        Intent intent = new Intent(context, PermissionUtil.class);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("permissions", permissions);
        context.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean hasAllGranted = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                hasAllGranted = false;
            }
        }
        WeakReference<OnPermissionCallback> permissionCallback = permissionCallbackMap.get(requestCode);
        if (permissionCallback != null) {
            OnPermissionCallback callback = permissionCallback.get();
            if (callback != null)
                if (hasAllGranted) {
                    callback.onGranted();
                } else {
                    callback.onDisGranted();
                }
            permissionCallback.clear();
            permissionCallbackMap.remove(requestCode);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WeakReference<OnPermissionCallback> permissionCallback = permissionCallbackMap.get(requestCode);
        if (permissionCallback != null) {
            OnPermissionCallback callback = permissionCallback.get();
            if (callback != null) {
                if (isOverlays) {
                    if (checkOverlaysPermission(this)) {
                        callback.onGranted();
                    } else {
                        callback.onDisGranted();
                    }
                } else {
                    if (checkOperationPermission(this, operation)) {
                        callback.onGranted();
                    } else {
                        callback.onDisGranted();
                    }
                }
                permissionCallback.clear();
                permissionCallbackMap.remove(requestCode);
            }
        }
        finish();
        overridePendingTransition(0, 0);
    }
}