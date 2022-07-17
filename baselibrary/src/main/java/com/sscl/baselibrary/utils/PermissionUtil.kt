package com.sscl.baselibrary.utils

import android.app.Activity
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.content.*
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.annotation.*
import java.util.ArrayList

/**
 * 权限请求工具类
 *
 * @author jackie
 */
object PermissionUtil {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    interface OnPermissionRequestResult {
        /**
         * 权限请求成功
         */
        fun permissionRequestSucceed()

        /**
         * 权限请求失败
         *
         * @param failedPermissions 请求失败的权限
         */
        fun permissionRequestFailed(failedPermissions: Array<String>)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private val TAG: String = PermissionUtil::class.java.simpleName

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private var requestCode: Int = 0
    private var permissions: Array<out String>? = null
    private var onPermissionRequestResult: OnPermissionRequestResult? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开静态方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Check if the calling context has a set of permissions.
     *
     * @param perms one ore more permissions, such as [Manifest.permission.CAMERA].
     * @return true if all permissions are already granted, false if at least one permission is not
     * yet granted.
     * @see Manifest.permission
     */
    fun hasPermissions(context: Context, @Size(min = 1) vararg perms: String): Boolean {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default")

            // DANGER ZONE!!! Changing this will break the library.
            return true
        }

        // Null context may be passed if we have detected Low API (less than M) so getting
        // to this point with a null context should not be possible.
        for (perm: String in perms) {
            if ((ContextCompat.checkSelfPermission(context, perm)
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                return false
            }
        }
        return true
    }

    /**
     * 设置权限相关回调
     *
     * @param onPermissionRequestResult 回调
     */
    fun setOnPermissionRequestResult(onPermissionRequestResult: OnPermissionRequestResult?) {
        PermissionUtil.onPermissionRequestResult = onPermissionRequestResult
    }

    /**
     * 获取跳转到权限设置界面的Intent
     *
     * @param context 上下文
     * @return Intent
     */
    fun getPermissionSettingIntent(context: Context): Intent {
        val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(Uri.fromParts("package", context.getPackageName(), null))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    /**
     * 直接跳转到设置界面
     *
     * @param activity Activity
     */
    fun toSettingActivity(activity: Activity) {
        activity.startActivity(getPermissionSettingIntent(activity))
    }

    /**
     * 请求权限
     *
     * @param activity    Activity
     * @param requestCode 请求码
     * @param permissions 权限
     */
    fun requestPermission(
        activity: Activity,
        requestCode: Int,
        @Size(min = 1) vararg permissions: String
    ) {
        PermissionUtil.requestCode = requestCode
        PermissionUtil.permissions = permissions
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    /**
     * 权限请求结果，需要重写[Activity.onRequestPermissionsResult]，在该方法中加入本方法
     *
     * @param activity     Activity
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 权限的允许情况
     */
    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != PermissionUtil.requestCode) {
            return
        }
        val nonNullPermissions = PermissionUtil.permissions ?: return
        val hasPermissions: Boolean = hasPermissions(activity, *nonNullPermissions)
        if (hasPermissions) {
            onPermissionRequestResult?.permissionRequestSucceed()
        } else {
            val strings: ArrayList<String> = ArrayList()
            for (i in grantResults.indices) {
                val grantResult: Int = grantResults[i]
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    strings.add(nonNullPermissions[i])
                }
            }
            onPermissionRequestResult?.permissionRequestFailed(strings.toTypedArray())
        }
    }

    /**
     * 是否权限永久被拒绝
     *
     * @param activity   Activity
     * @param permission 权限
     * @return 是否被永久拒绝
     */
    fun isPermissionAlwaysDenied(activity: Activity, permission: String): Boolean {
        return !hasPermissions(
            activity,
            permission
        ) && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * 是否有任何一个权限被用户永久拒绝
     *
     * @param activity   Activity
     * @param permission 权限
     * @return 是否有任何一个权限被用户永久拒绝
     */
    fun isAnyPermissionAlwaysDenied(
        activity: Activity,
        @Size(min = 1) vararg permission: String
    ): Boolean {
        var result = false
        for (s: String in permission) {
            val b: Boolean = isPermissionAlwaysDenied(activity, s)
            if (b) {
                result = true
                break
            }
        }
        return result
    }
}