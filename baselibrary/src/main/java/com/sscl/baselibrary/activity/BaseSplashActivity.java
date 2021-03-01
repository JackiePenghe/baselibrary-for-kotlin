package com.sscl.baselibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;


/**
 * 防止程序启动白屏或黑屏
 *
 * @author alm
 */
public abstract class BaseSplashActivity extends Activity {

    /*--------------------------------重写父类方法--------------------------------*/

    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     * <p>
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     * <p>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止本界面被多次启动
        if (runApp()) {
            return;
        }
        onCreate();
    }

    /*--------------------------------抽象方法--------------------------------*/

    /**
     * 在本界面第一次启动时执行的操作
     */
    protected abstract void onCreate();

    /*--------------------------------私有方法--------------------------------*/

    /**
     * 判断当前程序是否已经在运行了
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
