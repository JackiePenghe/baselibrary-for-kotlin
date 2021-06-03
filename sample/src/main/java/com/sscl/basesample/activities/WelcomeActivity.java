package com.sscl.basesample.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.sscl.baselibrary.activity.BaseWelcomeActivity;
import com.sscl.baselibrary.utils.CrashHandler;
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

    @Override
    protected void doAfterAnimation() {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SETTING) {
            checkPermission();
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
            toNext();
        } else {
            showPermissionDialog();
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
