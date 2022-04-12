package com.sscl.basesample.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

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

    private static final int REQUEST_CODE_PERMISSION = 3;
    private boolean needCheckPermission;
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;

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

    @Override
    protected void onStart() {
        super.onStart();
        //startActivityForResult被弃用，改用ActivityResultLauncher
        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                DebugUtil.warnOut(TAG, "manage result code " + result.getResultCode());
                checkFileManagePermission();
            }
        });
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

    /**
     * {@inheritDoc}
     * <p>
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (needCheckPermission) {
            needCheckPermission = false;
            checkPermission();
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
                intentActivityResultLauncher.launch(intent);
            }
        } else {
            toNext();
        }
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.no_permission)
                .setMessage(R.string.no_permission_message)
                .setPositiveButton(R.string.settings, (dialog, which) -> {
                    PermissionUtil.toSettingActivity(WelcomeActivity.this);
                    needCheckPermission = true;
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    ToastUtil.toastLong(WelcomeActivity.this, R.string.no_permission_exits);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
