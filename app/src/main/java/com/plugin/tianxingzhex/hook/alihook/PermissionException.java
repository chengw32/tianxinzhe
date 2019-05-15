package com.plugin.tianxingzhex.hook.alihook;

/**
 * 权限异常
 */
public class PermissionException extends Exception {
    /**
     * 动态权限
     * 可主动申请的用户权限
     */
    public static final int PERMISSION_TYPE_DYNAMIC = 0;
    /**
     * 设置权限
     * 需要用户主动跳转开启的权限
     */
    public static final int PERMISSION_TYPE_OPERATION = 1;
    private final int permissionType;

    /**
     * @param permissionType {@link #PERMISSION_TYPE_DYNAMIC}or{@link #PERMISSION_TYPE_OPERATION}.
     */
    public PermissionException(int permissionType, String message) {
        super(message);
        this.permissionType = permissionType;
    }

    public int getPermissionType() {
        return permissionType;
    }
}