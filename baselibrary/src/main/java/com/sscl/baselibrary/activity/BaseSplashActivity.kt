package com.sscl.baselibrary.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreen.KeepOnScreenCondition

/**
 * 防止程序启动白屏或黑屏
 *
 * @author alm
 */
@SuppressLint("CustomSplashScreen")
abstract class BaseSplashActivity constructor() : Activity() {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected val TAG: String = javaClass.getSimpleName()

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 重写父类方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    override fun onCreate(savedInstanceState: Bundle?) {
        //适配安卓12，设置为启动界面
        var splashScreen: SplashScreen? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen = installSplashScreen()
        }
        //隐藏导航栏（状态栏在theme中已经隐藏）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.hide(WindowInsets.Type.navigationBars())
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        super.onCreate(savedInstanceState)
        //防止本界面被多次启动
        if (runApp()) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen?.setKeepOnScreenCondition { true }
        }
        onCreate()
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 抽象方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 在本界面第一次启动时执行的操作
     */
    protected abstract fun onCreate()
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
    private fun runApp(): Boolean {
        if ((getIntent().getFlags() and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) > 0) {
            //为了防止重复启动多个闪屏页
            finish()
            return true
        }
        return false
    }
}