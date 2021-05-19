package com.sscl.basesample.activities;

import android.content.Intent;

import com.sscl.baselibrary.activity.BaseSplashActivity;

/**
 * 防止应用启动黑白屏
 *
 * @author alm
 */
public class SplashActivity extends BaseSplashActivity {

    @Override
    protected void onCreate() {
        //本界面仅用于防止程序黑白屏。想要更改本界面的黑白屏的背景，手动在res文件夹下新建一个xml文件夹，再新建一个files_path.xml。在其中配置即可
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
