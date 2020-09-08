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
        for (int i = 0; i < 16; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < i; j++) {
                String value = String.valueOf(j);
                stringBuilder.append(value.substring(value.length() - 1));
            }
            DebugUtil.warnOut(ConversionUtil.formatMicrometer(stringBuilder.toString()));
        }

        //本界面仅用于防止程序黑白屏。想要更改本界面的黑白屏的背景，手动在res文件夹下新建一个xml文件夹，再新建一个files_path.xml。在其中配置即可
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
