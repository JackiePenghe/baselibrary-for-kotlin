package com.sscl.baselibrary.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
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
        //适配安卓12，设置为启动界面
        SplashScreen splashScreen = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen = SplashScreen.installSplashScreen(this);
        }
        //隐藏导航栏（状态栏在theme中已经隐藏）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getDecorView().getWindowInsetsController().hide(WindowInsets.Type.navigationBars());
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        super.onCreate(savedInstanceState);
        //防止本界面被多次启动
        if (runApp()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setKeepVisibleCondition(() -> true);
        }
        onCreate();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 抽象方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
