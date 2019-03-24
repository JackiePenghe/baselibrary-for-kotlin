package com.sscl.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;

import com.sscl.baselibrary.receiver.HomeWatcherReceiver;


/**
 * Home键的监听类（广播接收者方式）
 *
 * @author jacke
 */
public class HomeWatcher {
    
    /*--------------------------------成员变量--------------------------------*/

    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 广播过滤器
     */
    private IntentFilter mFilter;
    /**
     * home键监听的广播接收者
     */
    private HomeWatcherReceiver mReceiver;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public HomeWatcher(Context context) {
        mContext = context;
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mReceiver = new HomeWatcherReceiver(this);
    }

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 设置监听
     *
     * @param listener listener
     */
    public void setOnHomePressedListener(@Nullable HomeWatcherReceiver.OnHomePressedListener listener) {
        mReceiver.setOnHomePressedListener(listener);
    }

    /**
     * 开始监听，注册广播接收者
     */
    public void startWatch() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter);
        }
    }

    /**
     * 停止监听，注销广播接收者
     */
    public void stopWatch() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }
}