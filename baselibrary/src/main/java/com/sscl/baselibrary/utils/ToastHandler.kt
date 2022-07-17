package com.sscl.baselibrary.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Toast

/**
 * 自定义Toast专用的Handler
 */
internal class ToastHandler
/**
 * Default constructor associates this handler with the [Looper] for the
 * current thread.
 *
 *
 * If this thread does not have a looper, this handler won't be able to receive messages
 * so an exception is thrown.
 */ constructor(
    /**
     * 上下文
     */
    private var context: Context
) : Handler(Looper.getMainLooper()) {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /**
     * Toast实例
     */
    private var textToast: Toast? = null
    private var viewToast: Toast? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有成员变量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开成员方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    fun setContext(context: Context) {
        this.context = context
    }
    /*--------------------------------重写父类方法--------------------------------*/
    /**
     * Subclasses must implement this to receive messages.
     *
     * @param msg 信息
     */
    public override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val what: Int = msg.what
        when (what) {
            MESSAGE, VIEW -> showToast(msg)
            CANCEL -> hideToast()
            SET_RE_USE -> setReuse(msg)
            else -> {}
        }
    }
    /*--------------------------------私有方法--------------------------------*/
    /**
     * 设置是否重用未消失的Toast
     *
     * @param msg Message消息
     */
    private fun setReuse(msg: Message) {
        val obj: Any? = msg.obj
        if (obj == null) {
            return
        }
        if (!(obj is Boolean)) {
            return
        }
        isReuse = obj
    }

    /**
     * 隐藏Toast
     */
    private fun hideToast() {
        if (textToast != null) {
            textToast!!.cancel()
            textToast = null
        }
    }

    /**
     * 显示Toast
     *
     * @param msg Message消息
     */
    @SuppressLint("ShowToast")
    private fun showToast(msg: Message) {
        val obj: Any? = msg.obj
        val arg1: Int = msg.arg1
        if (obj == null) {
            return
        }
        if (obj is String) {
            if (viewToast != null) {
                viewToast!!.cancel()
                viewToast = null
            }
            val messageText: String = obj
            if (textToast == null) {
                textToast = Toast.makeText(context, messageText, Toast.LENGTH_LONG)
            } else {
                if (isReuse) {
                    textToast!!.setText(messageText)
                } else {
                    if (arg1 != KEEP_TOAST) {
                        hideToast()
                    }
                    textToast = Toast.makeText(context, messageText, Toast.LENGTH_LONG)
                }
            }
            textToast!!.show()
        } else if (obj is View) {
            if (textToast != null) {
                textToast!!.cancel()
                textToast = null
            }
            if (viewToast == null) {
                viewToast = Toast(context)
            } else {
                if (!isReuse) {
                    if (arg1 != KEEP_TOAST) {
                        hideToast()
                    }
                    viewToast = Toast(context)
                }
            }
            viewToast!!.setView(obj)
            viewToast!!.show()
        } else {
            return
        }
    }

    companion object {

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 库内静态常量
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
        /**
         * 显示Toast
         */
       const val MESSAGE: Int = 1

        /**
         * 取消toast显示
         */
        const val CANCEL: Int = 2

        /**
         * 设置是否重用上次未消失的Toast直接进行显示
         */
        const val SET_RE_USE: Int = 3

        /**
         * 当前是否是用于保持Toast显示（超过3000秒时长的Toast）
         */
        const val KEEP_TOAST: Int = 4

        /**
         * 当前是否为第一次弹出Toast
         */
        const val FIRST_SEND: Int = 5

        /**
         * 使用视图
         */
        const  val VIEW: Int = 6

        /**
         * 是否重用上次还未消失的Toast
         */
        var isReuse: Boolean = false
            private set
    }
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}