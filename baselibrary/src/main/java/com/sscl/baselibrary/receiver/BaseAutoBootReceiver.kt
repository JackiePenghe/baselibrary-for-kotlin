package com.sscl.baselibrary.receiver

import android.content.*

abstract class BaseAutoBootReceiver : BroadcastReceiver() {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    protected val TAG: String = javaClass.simpleName

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onReceive(context: Context, intent: Intent) {
        val action: String? = intent.action
        if ((Intent.ACTION_BOOT_COMPLETED == action)) {
            doBootComplete(context, intent)
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 抽象方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    protected abstract fun doBootComplete(context: Context, receivedIntent: Intent)
}