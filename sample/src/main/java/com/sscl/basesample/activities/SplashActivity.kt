package com.sscl.basesample.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import com.sscl.baselibrary.activity.BaseSplashActivity
import com.sscl.baselibrary.utils.BaseManager
import com.sscl.baselibrary.utils.PermissionUtil
import com.sscl.baselibrary.utils.ToastUtil
import com.sscl.basesample.MainActivity
import com.sscl.basesample.R

/**
 * 防止应用启动黑白屏
 *
 * @author alm
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseSplashActivity() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    companion object {

        private val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        private const val REQUEST_CODE_PERMISSION = 1

        private const val REQUEST_CODE_FILE_MANAGE = 2
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    private var needCheckPermission = false

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    private val onPermissionRequestResult: PermissionUtil.OnPermissionRequestResult =
        object : PermissionUtil.OnPermissionRequestResult {
            /**
             * 权限请求成功
             */
            override fun permissionRequestSucceed() {
                checkFileManagePermission()
            }

            /**
             * 权限请求失败
             *
             * @param failedPermissions 请求失败的权限
             */
            override fun permissionRequestFailed(failedPermissions: Array<String>) {
                val permissionAlwaysDenied: Boolean = PermissionUtil.isAnyPermissionAlwaysDenied(
                    this@SplashActivity,
                    *failedPermissions
                )
                if (permissionAlwaysDenied) {
                    showPermissionDialog()
                } else {
                    checkPermissions()
                }
            }

        }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onCreate() {
        checkPermissions()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_MANAGE) {
            checkFileManagePermission()
        }
    }

    override fun onResume() {
        super.onResume()
        if (needCheckPermission) {
            needCheckPermission = false
            checkPermissions()
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 检查权限
     */
    private fun checkPermissions() {
        if (PermissionUtil.hasPermissions(
                this,
                *PERMISSIONS
            )
        ) {
            checkFileManagePermission()
        } else {
            PermissionUtil.setOnPermissionRequestResult(onPermissionRequestResult)
            PermissionUtil.requestPermission(
                this,
                REQUEST_CODE_PERMISSION,
                *PERMISSIONS
            )
        }
    }

    private fun checkFileManagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                toNextActivity()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, REQUEST_CODE_FILE_MANAGE)
            }
        } else {
            toNextActivity()
        }
    }

    private fun toNextActivity() {
        //本界面仅用于防止程序黑白屏。想要更改本界面的黑白屏的背景，手动在res文件夹下新建一个xml文件夹，再新建一个files_path.xml。在其中配置即可
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.com_sscl_basesample_no_permission)
            .setMessage(R.string.com_sscl_basesample_no_permission_message)
            .setPositiveButton(
                R.string.com_sscl_basesample_settings
            ) { _, _ ->
                PermissionUtil.toSettingActivity(this@SplashActivity)
                BaseManager.handler.postDelayed({
                    needCheckPermission = true
                }, 500)
            }
            .setNegativeButton(
                R.string.com_sscl_basesample_cancel
            ) { _, _ ->
                ToastUtil.toastLong(
                    this@SplashActivity,
                    R.string.com_sscl_basesample_no_permission_exits
                )
                finish()
            }
            .setCancelable(false)
            .show()
    }
}