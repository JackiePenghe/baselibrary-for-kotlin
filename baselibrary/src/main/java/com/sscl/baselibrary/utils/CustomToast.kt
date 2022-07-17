package com.sscl.baselibrary.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Message
import android.view.View
import androidx.annotation.StringRes
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 自定义Toast，可实现自定义显示时间,兼容至安卓N及以上版本
 */
internal class CustomToast {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    companion object {

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 属性声明
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /* * * * * * * * * * * * * * * * * * * 静态常量属性 * * * * * * * * * * * * * * * * * * */

        /**
         * 长时间的吐司持续时间
         */
        const val LENGTH_LONG = 3500

        /**
         * 短时间的吐司持续时间
         */
        const val LENGTH_SHORT = 2000

        /* * * * * * * * * * * * * * * * * * * 静态可变属性 * * * * * * * * * * * * * * * * * * */

        /**
         * 是否重用上次未消失的Toast的标志（缓存标志），实际标志在handler中
         */
        private var reuse = false

        /* * * * * * * * * * * * * * * * * * * 静态可空属性 * * * * * * * * * * * * * * * * * * */

        /**
         * 自定义吐司本类单例
         */
        @SuppressLint("StaticFieldLeak")
        private var customToast: CustomToast? = null

        /**
         * 吐司专用的handler(使用Handler可以避免定时器在非主线程中导致的线程问题)
         */
        @SuppressLint("StaticFieldLeak")
        private var toastHandler: ToastHandler? = null

        /**
         * showToast的定时任务
         */
        private var SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE: ScheduledExecutorService? = null

        /**
         * hideToast的定时任务
         */
        private var HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE: ScheduledExecutorService? = null

        /*--------------------------------私有方法--------------------------------*/
        /**
         * 显示Toast
         *
         * @param context     上下文
         * @param messageText Toast文本内容
         * @param duration    Toast持续时间（单位：毫秒）
         */
        @SuppressLint("ShowToast")
        private fun showMyToast(context: Context, messageText: String, duration: Int) {
            if (toastHandler == null) {
                toastHandler = ToastHandler(context)
            } else {
                toastHandler?.setContext(context)
            }
            setHandlerReuse()
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.shutdownNow()
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.shutdownNow()
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = BaseManager.newScheduledExecutorService(2)
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = BaseManager.newScheduledExecutorService(2)
            val first = booleanArrayOf(true)
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.scheduleAtFixedRate(
                {
                    try {
                        if (first[0]) {
                            handlerShowToast(messageText, ToastHandler.FIRST_SEND)
                            first[0] = false
                        } else {
                            handlerShowToast(messageText, ToastHandler.KEEP_TOAST)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 0, 3000, TimeUnit.MILLISECONDS
            )
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.schedule({
                try {
                    SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.shutdownNow()
                    SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null
                    handlerCancelToast()
                    HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.shutdownNow()
                    HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, duration.toLong(), TimeUnit.MILLISECONDS)
        }

        /**
         * 显示Toast
         *
         * @param context  上下文
         * @param view     View
         * @param duration Toast持续时间（单位：毫秒）
         */
        @SuppressLint("ShowToast")
        private fun showMyToast(context: Context, view: View, duration: Int) {
            if (toastHandler == null) {
                toastHandler = ToastHandler(context)
            } else {
                toastHandler?.setContext(context)
            }
            setHandlerReuse()
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.shutdownNow()
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.shutdownNow()
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = BaseManager.newScheduledExecutorService(2)
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = BaseManager.newScheduledExecutorService(2)
            val first = booleanArrayOf(true)
            SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.scheduleAtFixedRate({
                try {
                    if (first[0]) {
                        handlerShowToast(view, ToastHandler.FIRST_SEND)
                        first[0] = false
                    } else {
                        handlerShowToast(view, ToastHandler.KEEP_TOAST)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 0, 3000, TimeUnit.MILLISECONDS)
            HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.schedule({
                try {
                    SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.shutdownNow()
                    SHOW_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null
                    handlerCancelToast()
                    HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE?.shutdownNow()
                    HIDE_TOAST_KEEP_SCHEDULED_EXECUTOR_SERVICE = null
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, duration.toLong(), TimeUnit.MILLISECONDS)
        }

        /**
         * 使用Handler显示Toast
         *
         * @param messageText Toast文本内容
         * @param arg         是否为定时器保持消息显示
         */
        private fun handlerShowToast(messageText: String, arg: Int) {
            val message = Message()
            message.obj = messageText
            message.what = ToastHandler.MESSAGE
            message.arg1 = arg
            toastHandler?.sendMessage(message)
        }

        /**
         * 使用Handler显示Toast
         *
         * @param arg 是否为定时器保持消息显示
         */
        private fun handlerShowToast(view: View, arg: Int) {
            val message = Message()
            message.obj = view
            message.what = ToastHandler.VIEW
            message.arg1 = arg
            toastHandler?.sendMessage(message)
        }

        /**
         * 设置Handler是否重用未消失的Toast
         */
        private fun setHandlerReuse() {
            val message = Message()
            message.what = ToastHandler.SET_RE_USE
            message.obj = reuse
            toastHandler?.sendMessage(message)
        }

        /**
         * 设置是否重用（缓存位，每次在显示Toast前会将其设置到Handler中）
         *
         * @param reuse true表示开启重用
         */
        fun setReuse(reuse: Boolean) {
            Companion.reuse = reuse
        }
        /*--------------------------------公开静态方法--------------------------------*/
        /**
         * 获取CustomToast本类
         *
         * @param context  上下文
         * @param message  吐司显示信息
         * @param duration 吐司显示时长
         * @return CustomToast本类
         */
        fun makeText(context: Context, message: String, duration: Int): CustomToast {
            if (customToast == null) {
                synchronized(CustomToast::class.java) {
                    if (customToast == null) {
                        customToast = CustomToast(context, message, duration)
                    } else {
                        customToast?.messageText = message
                        customToast?.duration = duration
                    }
                }
            } else {
                customToast?.messageText = message
                customToast?.duration = duration
            }
            return customToast!!
        }

        /**
         * 使用Handler取消Toast
         */
        fun handlerCancelToast() {
            try {
                val message = Message()
                message.what = ToastHandler.CANCEL
                toastHandler?.sendMessage(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * CustomToast本类
         *
         * @param context    上下文
         * @param messageRes 吐司显示信息
         * @param duration   吐司显示时长
         * @return CustomToast本类
         */
        fun makeText(context: Context, @StringRes messageRes: Int, duration: Int): CustomToast {
            val message = context.getString(messageRes)
            if (customToast == null) {
                synchronized(CustomToast::class.java) {
                    if (customToast == null) {
                        customToast = CustomToast(context, message, duration)
                    } else {
                        customToast?.messageText = message
                        customToast?.view = null
                        customToast?.duration = duration
                    }
                }
            } else {
                customToast?.messageText = message
                customToast?.view = null
                customToast?.duration = duration
            }
            return (customToast)!!
        }

        fun makeText(context: Context, view: View, duration: Int): CustomToast {
            if (customToast == null) {
                synchronized(CustomToast::class.java) {
                    if (customToast == null) {
                        customToast = CustomToast(context, view, duration)
                    } else {
                        customToast?.view = view
                        customToast?.duration = duration
                    }
                }
            } else {
                customToast?.view = view
                customToast?.duration = duration
            }
            return (customToast)!!
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 上下文
     */
    private val context: Context

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * Toast持续时长
     */
    private var duration: Int

    /* * * * * * * * * * * * * * * * * * * 可空属性 * * * * * * * * * * * * * * * * * * */

    /**
     * Toast文本内容
     */
    private var messageText: String? = null

    /**
     * View视图
     */
    private var view: View? = null

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 构造方法
     *
     * @param context     上下文
     * @param messageText Toast文本内容
     * @param duration    Toast持续时间（单位：毫秒）
     */
    private constructor(context: Context, messageText: String, duration: Int) {
        this.context = context
        this.messageText = messageText
        this.duration = duration
    }

    private constructor(context: Context, view: View, duration: Int) {
        this.context = context
        this.view = view
        this.duration = duration
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 显示吐司
     */
    fun show() {
        if (view == null) {
            showMyToast(context, messageText ?: return, duration)
        } else {
            showMyToast(context, view ?: return, duration)
        }
    }
}