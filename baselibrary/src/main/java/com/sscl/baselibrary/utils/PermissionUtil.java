package com.sscl.baselibrary.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sscl.baselibrary.activity.BaseSplashActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限请求工具类
 *
 * @author jackie
 */
public class PermissionUtil {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public interface OnPermissionRequestResult {

        /**
         * 权限请求成功
         */
        void permissionRequestSucceed();

        /**
         * 权限请求失败
         *
         * @param failedPermissions 请求失败的权限
         */
        void permissionRequestFailed(String[] failedPermissions);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static final String TAG = PermissionUtil.class.getSimpleName();

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static int requestCode;
    private static String[] permissions;

    private static OnPermissionRequestResult onPermissionRequestResult;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开静态方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /**
     * Check if the calling context has a set of permissions.
     *
     * @param perms one ore more permissions, such as {@link Manifest.permission#CAMERA}.
     * @return true if all permissions are already granted, false if at least one permission is not
     * yet granted.
     * @see Manifest.permission
     */
    public static boolean hasPermissions(@NonNull Context context, @Size(min = 1) @NonNull String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");

            // DANGER ZONE!!! Changing this will break the library.
            return true;
        }

        // Null context may be passed if we have detected Low API (less than M) so getting
        // to this point with a null context should not be possible.

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * 设置权限相关回调
     *
     * @param onPermissionRequestResult 回调
     */
    public static void setOnPermissionRequestResult(OnPermissionRequestResult onPermissionRequestResult) {
        PermissionUtil.onPermissionRequestResult = onPermissionRequestResult;
    }

    /**
     * 获取跳转到权限设置界面的Intent
     *
     * @param context 上下文
     * @return Intent
     */
    public static Intent getPermissionSettingIntent(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * 直接跳转到设置界面
     *
     * @param activity Activity
     */
    public static void toSettingActivity(@NonNull Activity activity) {
        activity.startActivity(getPermissionSettingIntent(activity));
    }

    /**
     * 请求权限
     *
     * @param activity    Activity
     * @param requestCode 请求码
     * @param permissions 权限
     */
    public static void requestPermission(@NonNull Activity activity, int requestCode, @Size(min = 1) String... permissions) {
        PermissionUtil.requestCode = requestCode;
        PermissionUtil.permissions = permissions;
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * 权限请求结果，需要重写{@link Activity#onRequestPermissionsResult(int, String[], int[])}，在该方法中加入本方法
     *
     * @param activity     Activity
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 权限的允许情况
     */
    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != PermissionUtil.requestCode) {
            return;
        }
        boolean hasPermissions = hasPermissions(activity, PermissionUtil.permissions);
        if (hasPermissions) {
            if (onPermissionRequestResult != null) {
                onPermissionRequestResult.permissionRequestSucceed();
            }
        } else {
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    strings.add(permissions[i]);
                }
            }
            if (onPermissionRequestResult != null) {
                String[] failedPermissions = new String[strings.size()];
                onPermissionRequestResult.permissionRequestFailed(strings.toArray(failedPermissions));
            }
        }
    }

    /**
     * 是否权限永久被拒绝
     *
     * @param activity   Activity
     * @param permission 权限
     * @return 是否被永久拒绝
     */
    public static boolean isPermissionAlwaysDenied(@NonNull Activity activity, String permission) {
        return !hasPermissions(activity, permission) && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * 是否有任何一个权限被用户永久拒绝
     *
     * @param activity   Activity
     * @param permission 权限
     * @return 是否有任何一个权限被用户永久拒绝
     */
    public static boolean isAnyPermissionAlwaysDenied(@NonNull Activity activity, @Size(min = 1) String... permission) {
        boolean result = false;
        for (String s : permission) {
            boolean b = isPermissionAlwaysDenied(activity, s);
            if (b) {
                result = true;
                break;
            }
        }
        return result;
    }
}
