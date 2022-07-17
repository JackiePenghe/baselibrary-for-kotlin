package com.sscl.baselibrary.receiver

import android.content.*

/**
 * 获取当前屏幕亮灭及解锁状态的广播监听
 *
 * @author jackie
 */
class ScreenStatusReceiver : BroadcastReceiver() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 接口定义
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * 屏幕状态更改的监听
     */
    interface OnScreenStatusChangedListener {
        /**
         * 屏幕点亮
         */
        fun onScreenOn()

        /**
         * 屏幕熄灭
         */
        fun onScreenOff()

        /**
         * 用户解锁完成
         */
        fun onUserUnlock()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    protected val TAG: String = javaClass.simpleName

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 屏幕状态更改的监听
     */
    private var onScreenStatusChangedListener: OnScreenStatusChangedListener? = null

    /*---------------------------------------实现父类方法---------------------------------------*/

    override fun onReceive(context: Context?, intent: Intent?) {
        val intent1 = intent ?: return

        val action: String = intent1.action ?: return
        when (action) {
            Intent.ACTION_SCREEN_ON -> {
                onScreenStatusChangedListener?.onScreenOn()
            }
            Intent.ACTION_SCREEN_OFF -> {
                onScreenStatusChangedListener?.onScreenOff()
            }
            Intent.ACTION_USER_PRESENT -> {
                onScreenStatusChangedListener?.onUserUnlock()
            }
            else -> {}
        }
    }

    /**
     * 设置屏幕状态更改的监听
     *
     * @param onScreenStatusChangedListener 屏幕状态更改的监听
     */
    fun setOnScreenStatusChangedListener(onScreenStatusChangedListener: OnScreenStatusChangedListener?) {
        this.onScreenStatusChangedListener = onScreenStatusChangedListener
    }
}