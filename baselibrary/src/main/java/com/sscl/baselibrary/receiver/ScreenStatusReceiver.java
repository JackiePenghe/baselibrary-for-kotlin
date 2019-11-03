package com.sscl.baselibrary.receiver;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;

import androidx.annotation.Nullable;

/**
 * 获取当前屏幕亮灭及解锁状态的广播监听
 *
 * @author jackie
 */
public class ScreenStatusReceiver extends BroadcastReceiver {

    /*---------------------------------------成员变量---------------------------------------*/

    /**
     * 屏幕状态更改的监听
     */
    private OnScreenStatusChangedListener onScreenStatusChangedListener;

    /*---------------------------------------实现父类方法---------------------------------------*/

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context#registerReceiver(BroadcastReceiver, IntentFilter, String, Handler)} . When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b> This means you should not perform any operations that
     * return a result to you asynchronously. If you need to perform any follow up
     * background work, schedule a {@link JobService} with
     * {@link JobScheduler}.
     * <p>
     * If you wish to interact with a service that is already running and previously
     * bound using {@link Context#bindService(Intent, ServiceConnection, int)}  bindService()},
     * you can use {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link BroadcastReceiver#onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(@Nullable Context context, @Nullable Intent intent) {

        if (context == null) {
            return;
        }
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            //亮屏
            case Intent.ACTION_SCREEN_ON:
                if (onScreenStatusChangedListener != null) {
                    onScreenStatusChangedListener.onScreenOn();
                }
                break;
            //灭屏
            case Intent.ACTION_SCREEN_OFF:
                if (onScreenStatusChangedListener != null) {
                    onScreenStatusChangedListener.onScreenOff();
                }
                break;
            //用户解锁成功
            case Intent.ACTION_USER_PRESENT:
                if (onScreenStatusChangedListener != null) {
                    onScreenStatusChangedListener.onUserUnlock();
                }
                break;
            default:
                break;
        }
    }

    /*---------------------------------------setter---------------------------------------*/

    /**
     * 设置屏幕状态更改的监听
     *
     * @param onScreenStatusChangedListener 屏幕状态更改的监听
     */
    public void setOnScreenStatusChangedListener(OnScreenStatusChangedListener onScreenStatusChangedListener) {
        this.onScreenStatusChangedListener = onScreenStatusChangedListener;
    }

    /*---------------------------------------接口定义---------------------------------------*/

    /**
     * 屏幕状态更改的监听
     */
    public interface OnScreenStatusChangedListener {

        /**
         * 屏幕点亮
         */
        void onScreenOn();

        /**
         * 屏幕熄灭
         */
        void onScreenOff();

        /**
         * 用户解锁完成
         */
        void onUserUnlock();
    }
}
