package com.sscl.baselibrary.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.splashscreen.SplashScreen;


/**
 * 防止程序启动白屏或黑屏
 *
 * @author alm
 */
@SuppressLint("CustomSplashScreen")
public abstract class BaseSplashActivity extends Activity {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    protected final String TAG = getClass().getSimpleName();

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写父类方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen splashScreen = null;
        if (needInstallSplash()) {
            splashScreen = SplashScreen.installSplashScreen(this);
        }
        super.onCreate(savedInstanceState);
        //防止本界面被多次启动
        if (runApp()) {
            return;
        }
        if (needInstallSplash()) {
            if (splashScreen != null) {
                splashScreen.setKeepVisibleCondition(() -> true);
            }
        }
        onCreate();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 抽象方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 是否需要手动安装为闪屏（AndroidManifest中不使用闪屏主题时，才可用）
     *
     * @return true表示需要手动安装为闪屏
     */
    protected abstract boolean needInstallSplash();

    /**
     * 在本界面第一次启动时执行的操作
     */
    protected abstract void onCreate();

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 判断当前程序是否已经在运行了
     *
     * @return true表示已经在运行了
     */
    private boolean runApp() {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) > 0) {
            //为了防止重复启动多个闪屏页
            finish();
            return true;
        }
        return false;
    }
}
