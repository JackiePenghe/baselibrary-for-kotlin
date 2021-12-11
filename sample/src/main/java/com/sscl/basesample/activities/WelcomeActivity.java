package com.sscl.basesample.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.activity.BaseWelcomeActivity;
import com.sscl.baselibrary.utils.CrashHandler;
import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.baselibrary.utils.LogCatHelper;
import com.sscl.baselibrary.utils.PermissionUtil;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.basesample.MainActivity;
import com.sscl.basesample.MyApplication;
import com.sscl.basesample.R;


/**
 * 欢迎页 在这个界面可以进行权限请求等`
 *
 * @author alm
 * @date 2017/10/13
 */
public class WelcomeActivity extends BaseWelcomeActivity {

    private static final int REQUEST_CODE_SETTING = 1;
    private static final int REQUEST_CODE_PERMISSION = 3;
    private static final int REQUEST_CODE = 2;

    @Override
    protected void doAfterAnimation() {
        PermissionUtil.setOnPermissionRequestResult(new PermissionUtil.OnPermissionRequestResult() {
            @Override
            public void permissionRequestSucceed() {
                checkFileManagePermission();
            }

            @Override
            public void permissionRequestFailed(String[] failedPermissions) {
                boolean permissionAlwaysDenied = PermissionUtil.isPermissionAlwaysDenied(WelcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionAlwaysDenied) {
                    showPermissionDialog();
                }
            }
        });
        checkPermission();
    }

    /**
     * 设置ImageView的图片资源
     *
     * @return 图片资源ID
     */
    @Override
    protected int setImageViewSource() {
        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SETTING) {
            DebugUtil.warnOut(TAG, "permission result code " + resultCode);
            checkPermission();
        } else if (requestCode == REQUEST_CODE) {
            DebugUtil.warnOut(TAG, "manage result code " + resultCode);
            checkFileManagePermission();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void toNext() {

        LogCatHelper.getInstance().init();
        CrashHandler.getInstance().init(WelcomeActivity.this.getApplicationContext(), true);
        MyApplication.initCrashListener();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkPermission() {
        if (PermissionUtil.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            checkFileManagePermission();
        } else {
            PermissionUtil.requestPermission(this, REQUEST_CODE_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void checkFileManagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                toNext();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else {
            toNext();
        }
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.no_permission)
                .setMessage(R.string.no_permission_message)
                .setPositiveButton(R.string.settings, (dialog, which) -> PermissionUtil.toSettingActivity(WelcomeActivity.this, REQUEST_CODE_SETTING))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    ToastUtil.toastLong(WelcomeActivity.this, R.string.no_permission_exits);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
