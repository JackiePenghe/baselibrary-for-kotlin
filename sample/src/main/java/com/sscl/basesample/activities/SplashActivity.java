package com.sscl.basesample.activities;

import android.content.Intent;

import com.sscl.baselibrary.activity.BaseSplashActivity;
import com.sscl.baselibrary.utils.ConversionUtil;
import com.sscl.baselibrary.utils.DebugUtil;

/**
 * 防止应用启动黑白屏
 *
 * @author alm
 */
public class SplashActivity extends BaseSplashActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate() {
        String ipv4String = "192.168.0.2";
        byte[] bytes = ConversionUtil.ipv4StringToByteArray(ipv4String);
        if (bytes != null) {
            DebugUtil.warnOut(TAG, ConversionUtil.byteArrayToHexStr(bytes));
        }

        //本界面仅用于防止程序黑白屏。想要更改本界面的黑白屏的背景，手动在res文件夹下新建一个xml文件夹，再新建一个files_path.xml。在其中配置即可
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
