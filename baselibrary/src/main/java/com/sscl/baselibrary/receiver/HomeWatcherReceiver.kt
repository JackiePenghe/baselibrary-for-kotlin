package com.sscl.baselibrary.receiver

import android.content.*
import com.sscl.baselibrary.utils.HomeWatcher
import java.lang.ref.WeakReference

/**
 * 广播接收者,用于监听home键被按下
 *
 * @author pengh
 */
class HomeWatcherReceiver constructor(homeWatcher: HomeWatcher) : BroadcastReceiver() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Home键的接口
     */
    interface OnHomePressedListener {
        /**
         * Home键被按下
         */
        fun onHomePressed()

        /**
         * 长按Home键
         */
        fun onHomeLongPressed()
    }

    companion object {

        /* * * * * * * * * * * * * * * * * * * 静态常量 * * * * * * * * * * * * * * * * * * */

        const val SYSTEM_DIALOG_REASON_KEY: String = "reason"
        const val SYSTEM_DIALOG_REASON_RECENT_APPS: String = "recentapps"
        const val SYSTEM_DIALOG_REASON_HOME_KEY: String = "homekey"
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    protected val TAG: String = javaClass.simpleName

    /**
     * HomeWatcher弱引用
     */
    private val homeWatcherWeakReference: WeakReference<HomeWatcher>

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    /**
     * Home键的监听
     */
    private var mListener: OnHomePressedListener? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onReceive(context: Context?, intent: Intent?) {
        val intent1 = intent ?: return
        val action: String? = intent1.action
        homeWatcherWeakReference.get() ?: return
        if (action == null) {
            return
        }
        if ((action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            val reason: String? = intent1.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
            if (reason != null) {
                if ((reason == SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    // 短按home键
                    mListener?.onHomePressed()
                } else if ((reason == SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    // 长按home键
                    mListener?.onHomeLongPressed()
                }
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    fun setOnHomePressedListener(listener: OnHomePressedListener?) {
        mListener = listener
    }
    /*--------------------------------构造方法--------------------------------*/ /**
     * 构造方法
     *
     * @param homeWatcher HomeWatcher对象
     */
    init {
        homeWatcherWeakReference = WeakReference(homeWatcher)
    }
}