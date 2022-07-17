package com.sscl.baselibrary.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.sscl.baselibrary.receiver.HomeWatcherReceiver
import com.sscl.baselibrary.receiver.HomeWatcherReceiver.OnHomePressedListener


/**
 * Home键的监听类（广播接收者方式）
 *
 * @author jacke
 */
class HomeWatcher constructor(
    /**
     * 上下文
     */
    private val mContext: Context
) {
    /*--------------------------------成员变量--------------------------------*/
    /**
     * 广播过滤器
     */
    private val mFilter: IntentFilter

    /**
     * home键监听的广播接收者
     */
    private val mReceiver: HomeWatcherReceiver?
    /*--------------------------------公开方法--------------------------------*/
    /**
     * 设置监听
     *
     * @param listener listener
     */
    fun setOnHomePressedListener(listener: OnHomePressedListener?) {
        mReceiver!!.setOnHomePressedListener(listener)
    }

    /**
     * 开始监听，注册广播接收者
     */
    fun startWatch() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter)
        }
    }

    /**
     * 停止监听，注销广播接收者
     */
    fun stopWatch() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver)
        }
    }
    /*--------------------------------构造方法--------------------------------*/ /**
     * 构造方法
     *
     * @param context 上下文
     */
    init {
        mFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        mReceiver = HomeWatcherReceiver(this)
    }
}