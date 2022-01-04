package com.sscl.baselibrary.utils;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * 吐丝工具类
 *
 * @author pengh
 */
public class ToastUtil {

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 长时间的吐司
     *
     * @param context 上下文
     * @param message 信息
     */
    public static void toastLong(@NonNull Context context, String message) {
        showToast(context, message, CustomToast.LENGTH_LONG);
    }

    /**
     * 长时间的吐司
     *
     * @param context    上下文
     * @param messageRes 信息
     */
    public static void toastLong(@NonNull Context context, @StringRes int messageRes) {
        showToast(context, messageRes, CustomToast.LENGTH_LONG);
    }

    /**
     * 长时间的吐司
     *
     * @param context 上下文
     * @param view    View
     */
    public static void toastLong(@NonNull Context context, @NonNull View view) {
        showToast(context, view, CustomToast.LENGTH_LONG);
    }

    /**
     * 长时间的吐司
     *
     * @param context 上下文
     * @param view    View
     */
    public static void toastShort(@NonNull Context context, @NonNull View view) {
        showToast(context, view, CustomToast.LENGTH_SHORT);
    }

    /**
     * 设置是否重用未消失的Toast
     *
     * @param reuse true表示开启重用
     */
    @SuppressWarnings("unused")
    public static void setToastReuse(boolean reuse) {
        CustomToast.setReuse(reuse);
    }

    /**
     * 获取当前Toast是否开启重用
     *
     * @return true表示开启重用
     */
    @SuppressWarnings("unused")
    public static boolean isToastReuse() {
        return ToastHandler.isReuse();
    }

    /**
     * 短时间的吐司
     *
     * @param context 上下文
     * @param message 信息
     */
    @SuppressWarnings("unused")
    public static void toastShort(@NonNull Context context, String message) {
        showToast(context, message, CustomToast.LENGTH_SHORT);
    }

    /**
     * 取消Toast
     */
    public static void cancel() {
        CustomToast.handlerCancelToast();
    }

    /**
     * 短时间的吐司
     *
     * @param context    上下文
     * @param messageRes 信息
     */
    public static void toastShort(@NonNull Context context, @StringRes int messageRes) {
        showToast(context, messageRes, CustomToast.LENGTH_SHORT);
    }

    /**
     * 自定义时间的吐司
     *
     * @param context 上下文
     * @param message 信息
     */
    @SuppressWarnings("unused")
    public static void toast(@NonNull Context context, @NonNull String message, int duration) {
        showToast(context, message, duration);
    }

    /**
     * 自定义时间的吐司
     *
     * @param context    上下文
     * @param messageRes 信息
     */
    public static void toast(@NonNull Context context, @StringRes int messageRes, int duration) {
        showToast(context, messageRes, duration);
    }

    /**
     * 自定义时间的吐司
     *
     * @param context    上下文
     * @param view 信息
     */
    public static void toast(@NonNull Context context,@NonNull  View view, int duration) {
        showToast(context, view, duration);
    }

    /**
     * 弹出Toast
     *
     * @param context  上下文
     * @param message  信息
     * @param duration 持续时间
     */
    private static void showToast(@NonNull Context context, @NonNull String message, int duration) {
        CustomToast.makeText(context, message, duration).show();
    }

    /*--------------------------------私有方法--------------------------------*/

    /**
     * 弹出Toast
     *
     * @param context    上下文
     * @param messageRes 信息
     * @param duration   持续时间
     */
    private static void showToast(@NonNull Context context, @StringRes int messageRes, int duration) {
        CustomToast.makeText(context, messageRes, duration).show();
    }

    /**
     * 弹出Toast
     *
     * @param context  上下文
     * @param view     View
     * @param duration 持续时间
     */
    private static void showToast(Context context, View view, int duration) {
        CustomToast.makeText(context, view, duration).show();
    }
}
