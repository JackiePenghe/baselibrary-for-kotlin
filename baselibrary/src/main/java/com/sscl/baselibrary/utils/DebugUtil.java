package com.sscl.baselibrary.utils;

import android.util.Log;

/**
 * Debug日志打印工具类
 *
 * @author pengh
 */
public class DebugUtil {

    /*--------------------------------静态变量--------------------------------*/

    /**
     * log的TAG
     */
    private static String TAG = "BaseLibrary->";
    /**
     * 是否打印日志信息的标志
     */
    private static boolean mDebug = false;

    /*--------------------------------静态方法--------------------------------*/

    /**
     * 获取当前日志打印标志
     *
     * @return 日志打印标志
     */
    public static boolean isDebug() {
        return mDebug;
    }

    /**
     * 设置日志打印标志
     *
     * @param debug 日志打印标志
     */
    public static void setDebugFlag(boolean debug) {
        mDebug = debug;
    }

    /**
     * 设置默认的tag
     *
     * @param tag 默认的tag
     */
    public static void setDefaultTAG(String tag) {
        TAG = tag;
    }

    /**
     * 等同于Log.i
     *
     * @param tag     tag
     * @param message 日志信息
     */
    @SuppressWarnings("WeakerAccess")
    public static void infoOut(String tag, String message) {
        if (!mDebug) {
            return;
        }
        Log.i(TAG + tag, message);
    }

    /**
     * 等同于Log.i
     *
     * @param message 日志信息
     */
    public static void infoOut(String message) {
        infoOut(TAG, message);
    }

    /**
     * 等同于Log.e
     *
     * @param tag     tag
     * @param message 日志信息
     */
    @SuppressWarnings("WeakerAccess")
    public static void errorOut(String tag, String message) {
        if (!mDebug) {
            return;
        }
        Log.e(TAG + tag, message);
    }

    /**
     * 等同于Log.e
     *
     * @param message 日志信息
     */
    public static void errorOut(String message) {
        errorOut(TAG, message);
    }

    /**
     * 等同于Log.d
     *
     * @param tag     tag
     * @param message 日志信息
     */
    @SuppressWarnings("WeakerAccess")
    public static void debugOut(String tag, String message) {
        if (!mDebug) {
            return;
        }
        Log.d(TAG + tag, message);
    }

    /**
     * 等同于Log.d
     *
     * @param message 日志信息
     */
    public static void debugOut(String message) {
        debugOut(TAG, message);
    }

    /**
     * 等同于Log.w
     *
     * @param tag     tag
     * @param message 日志信息
     */
    public static void warnOut(String tag, String message) {
        if (!mDebug) {
            return;
        }
        Log.w(TAG + tag, message);
    }

    /**
     * 等同于Log.w
     *
     * @param message 日志信息
     */
    public static void warnOut(String message) {
        warnOut(TAG, message);
    }


    /**
     * 等同于Log.v
     *
     * @param tag     tag
     * @param message 日志信息
     */
    @SuppressWarnings("WeakerAccess")
    public static void verOut(String tag, String message) {
        if (!mDebug) {
            return;
        }
        Log.v(TAG + tag, message);
    }

    /**
     * 等同于Log.v
     *
     * @param message 日志信息
     */
    public static void verOut(String message) {
        verOut(TAG, message);
    }
}
