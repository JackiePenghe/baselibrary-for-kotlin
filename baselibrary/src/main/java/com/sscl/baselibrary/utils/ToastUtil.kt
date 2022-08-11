package com.sscl.baselibrary.utils

import android.content.Context
import android.view.View
import androidx.annotation.StringRes

/**
 * 吐丝工具类
 *
 * @author pengh
 */
object ToastUtil {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 长时间的吐司
     *
     * @param context 上下文
     * @param message 信息
     */
    fun toastLong(context: Context, message: String) {
        showToast(context, message, CustomToast.LENGTH_LONG)
    }

    /**
     * 长时间的吐司
     *
     * @param context    上下文
     * @param messageRes 信息
     */
    fun toastLong(context: Context, @StringRes messageRes: Int) {
        showToast(context, messageRes, CustomToast.LENGTH_LONG)
    }

    /**
     * 长时间的吐司
     *
     * @param context 上下文
     * @param view    View
     */
    fun toastLong(context: Context, view: View) {
        showToast(context, view, CustomToast.LENGTH_LONG)
    }

    /**
     * 长时间的吐司
     *
     * @param context 上下文
     * @param view    View
     */
    fun toastShort(context: Context, view: View) {
        showToast(context, view, CustomToast.LENGTH_SHORT)
    }
    /**
     * 获取当前Toast是否开启重用
     *
     * @return true表示开启重用
     */
    /**
     * 设置是否重用未消失的Toast
     */
    @kotlin.jvm.JvmStatic
    var isToastReuse: Boolean
        get() = ToastHandler.isReuse
        set(reuse) {
            CustomToast.setReuse(reuse)
        }

    /**
     * 短时间的吐司
     *
     * @param context 上下文
     * @param message 信息
     */
    fun toastShort(context: Context, message: String) {
        showToast(context, message, CustomToast.LENGTH_SHORT)
    }

    /**
     * 取消Toast
     */
    fun cancel() {
        CustomToast.handlerCancelToast()
    }

    /**
     * 短时间的吐司
     *
     * @param context    上下文
     * @param messageRes 信息
     */
    @kotlin.jvm.JvmStatic
    fun toastShort(context: Context, @StringRes messageRes: Int) {
        showToast(context, messageRes, CustomToast.LENGTH_SHORT)
    }

    /**
     * 自定义时间的吐司
     *
     * @param context 上下文
     * @param message 信息
     */
    fun toast(context: Context, message: String, duration: Int) {
        showToast(context, message, duration)
    }

    /**
     * 自定义时间的吐司
     *
     * @param context    上下文
     * @param messageRes 信息
     */
    fun toast(context: Context, @StringRes messageRes: Int, duration: Int) {
        showToast(context, messageRes, duration)
    }

    /**
     * 自定义时间的吐司
     *
     * @param context    上下文
     * @param view 信息
     */
    @kotlin.jvm.JvmStatic
    fun toast(context: Context, view: View, duration: Int) {
        showToast(context, view, duration)
    }

    /*--------------------------------私有方法--------------------------------*/

    /**
     * 弹出Toast
     *
     * @param context  上下文
     * @param message  信息
     * @param duration 持续时间
     */
    private fun showToast(context: Context, message: String, duration: Int) {
        CustomToast.makeText(context, message, duration).show()
    }

    /**
     * 弹出Toast
     *
     * @param context    上下文
     * @param messageRes 信息
     * @param duration   持续时间
     */
    private fun showToast(context: Context, @StringRes messageRes: Int, duration: Int) {
        CustomToast.makeText(context, messageRes, duration).show()
    }

    /**
     * 弹出Toast
     *
     * @param context  上下文
     * @param view     View
     * @param duration 持续时间
     */
    private fun showToast(context: Context, view: View, duration: Int) {
        CustomToast.makeText(context, view, duration).show()
    }
}