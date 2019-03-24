package com.sscl.baselibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.utils.HomeWatcher;

import java.lang.ref.WeakReference;

/**
 * 广播接收者,用于监听home键被按下
 *
 * @author pengh
 */
public class HomeWatcherReceiver extends BroadcastReceiver {

    /*--------------------------------静态常量--------------------------------*/

    final String SYSTEM_DIALOG_REASON_KEY = "reason";

    final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";

    final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

    /*--------------------------------成员变量--------------------------------*/
    /**
     * HomeWatcher弱引用
     */
    private WeakReference<HomeWatcher> homeWatcherWeakReference;

    /**
     * Home键的监听
     */
    private OnHomePressedListener mListener;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     *
     * @param homeWatcher HomeWatcher对象
     */
    public HomeWatcherReceiver(HomeWatcher homeWatcher) {
        homeWatcherWeakReference = new WeakReference<>(homeWatcher);
    }

    /*--------------------------------实现父类方法--------------------------------*/

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context#registerReceiver(BroadcastReceiver, * IntentFilter, String, Handler)}. When it runs on the main
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
     * bound using {@link Context#bindService(Intent, ServiceConnection, int) bindService()},
     * you can use {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @SuppressWarnings({"JavadocReference", "JavaDoc"})
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        HomeWatcher homeWatcher = homeWatcherWeakReference.get();
        if (homeWatcher == null) {
            return;
        }
        if (action == null) {
            return;
        }
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

            if (reason != null) {
                if (mListener != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        // 短按home键
                        mListener.onHomePressed();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        // 长按home键
                        mListener.onHomeLongPressed();
                    }
                }
            }
        }
    }

    public void setOnHomePressedListener(@Nullable OnHomePressedListener listener) {
        mListener = listener;
    }


    /**
     * Home键的接口
     */
    public interface OnHomePressedListener {
        /**
         * Home键被按下
         */
        void onHomePressed();

        /**
         * 长按Home键
         */
        void onHomeLongPressed();
    }
}