package com.sscl.basesample.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.sscl.baselibrary.activity.BaseWelcomeActivity
import com.sscl.baselibrary.utils.*
import com.sscl.baselibrary.utils.PermissionUtil.OnPermissionRequestResult
import com.sscl.basesample.MainActivity
import com.sscl.basesample.MyApplication
import com.sscl.basesample.R

/**
 * 欢迎页 在这个界面可以进行权限请求等`
 *
 * @author alm
 * @date 2017/10/13
 */
class WelcomeActivity : BaseWelcomeActivity() {
    private var needCheckPermission = false
    private var intentActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    override fun doAfterAnimation() {
        PermissionUtil.setOnPermissionRequestResult(object : OnPermissionRequestResult {
            override fun permissionRequestSucceed() {
                checkFileManagePermission()
            }

            override fun permissionRequestFailed(failedPermissions: Array<String>) {
                val permissionAlwaysDenied: Boolean = PermissionUtil.isPermissionAlwaysDenied(
                    this@WelcomeActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (permissionAlwaysDenied) {
                    showPermissionDialog()
                }
            }
        })
        checkPermission()
    }

    override fun onStart() {
        super.onStart()
        //startActivityForResult被弃用，改用ActivityResultLauncher
        intentActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                DebugUtil.warnOut(TAG, "manage result code " + result.resultCode)
                checkFileManagePermission()
            }
    }

    /**
     * 设置ImageView的图片资源
     *
     * @return 图片资源ID
     */
    override fun setImageViewSource(): Int {
        return R.drawable.com_sscl_basesample_bg_splash
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        if (needCheckPermission) {
            needCheckPermission = false
            checkPermission()
        }
    }

    private fun toNext() {
        LogCatHelper.getInstance().init()
        CrashHandler.getInstance().init(this@WelcomeActivity.applicationContext, true)
        MyApplication.initCrashListener()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkPermission() {
        if (PermissionUtil.hasPermissions(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            checkFileManagePermission()
        } else {
            PermissionUtil.requestPermission(
                this,
                REQUEST_CODE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private fun checkFileManagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                toNext()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                intentActivityResultLauncher?.launch(intent)
            }
        } else {
            toNext()
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.com_sscl_basesample_no_permission)
            .setMessage(R.string.com_sscl_basesample_no_permission_message)
            .setPositiveButton(
                R.string.com_sscl_basesample_settings
            ) { _, _ ->
                PermissionUtil.toSettingActivity(this@WelcomeActivity)
                needCheckPermission = true
            }
            .setNegativeButton(
                R.string.com_sscl_basesample_cancel
            ) { _, _ ->
                ToastUtil.toastLong(
                    this@WelcomeActivity,
                    R.string.com_sscl_basesample_no_permission_exits
                )
                finish()
            }
            .setCancelable(false)
            .show()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 3
    }
}