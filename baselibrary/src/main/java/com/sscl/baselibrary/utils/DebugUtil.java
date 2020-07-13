package com.sscl.baselibrary.utils;

import android.util.Log;

/**
 * Debug日志打印工具类
 *
 * @author pengh
 */
public class DebugUtil {

    private static final int MAX_LENGTH = 4000;

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
        int length = message.length();
        if (length <= MAX_LENGTH) {
            Log.i(TAG + "->" + tag, message);
            return;
        }
        int count;
        if (length % MAX_LENGTH != 0) {
            count = length / MAX_LENGTH + 1;
        } else {
            count = length / MAX_LENGTH;
        }
        for (int i = 0; i < count; i++) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.i(TAG + "->" + tag, "->" + message.substring(i * MAX_LENGTH, length));
            } else {
                String str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH);
                if (i == 0) {
                    Log.i(TAG + "->" + tag, str + "->");
                    continue;
                }
                Log.i(TAG + "->" + tag, "->" + str + "->");
            }
        }
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

        int length = message.length();
        if (length <= MAX_LENGTH) {
            Log.e(TAG + "->" + tag, message);
            return;
        }
        int count;
        if (length % MAX_LENGTH != 0) {
            count = length / MAX_LENGTH + 1;
        } else {
            count = length / MAX_LENGTH;
        }
        for (int i = 0; i < count; i++) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.e(TAG + "->" + tag, "->" + message.substring(i * MAX_LENGTH, length));
            } else {
                String str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH);
                if (i == 0) {
                    Log.e(TAG + "->" + tag, str + "->");
                    continue;
                }
                Log.e(TAG + "->" + tag, "->" + str + "->");
            }
        }
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

        int length = message.length();
        if (length <= MAX_LENGTH) {
            Log.d(TAG + "->" + tag, message);
            return;
        }
        int count;
        if (length % MAX_LENGTH != 0) {
            count = length / MAX_LENGTH + 1;
        } else {
            count = length / MAX_LENGTH;
        }
        for (int i = 0; i < count; i++) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.d(TAG + "->" + tag, "->" + message.substring(i * MAX_LENGTH, length));
            } else {
                String str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH);
                if (i == 0) {
                    Log.d(TAG + "->" + tag, str + "->");
                    continue;
                }
                Log.d(TAG + "->" + tag, "->" + str + "->");
            }
        }
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

        int length = message.length();
        if (length <= MAX_LENGTH) {
            Log.w(TAG + "->" + tag, message);
            return;
        }
        int count;
        if (length % MAX_LENGTH != 0) {
            count = length / MAX_LENGTH + 1;
        } else {
            count = length / MAX_LENGTH;
        }
        for (int i = 0; i < count; i++) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.w(TAG + "->" + tag, "->" + message.substring(i * MAX_LENGTH, length));
            } else {
                String str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH);
                if (i == 0) {
                    Log.w(TAG + "->" + tag, str + "->");
                    continue;
                }
                Log.w(TAG + "->" + tag, "->" + str + "->");
            }
        }
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

        int length = message.length();
        if (length <= MAX_LENGTH) {
            Log.v(TAG + "->" + tag, message);
            return;
        }
        int count;
        if (length % MAX_LENGTH != 0) {
            count = length / MAX_LENGTH + 1;
        } else {
            count = length / MAX_LENGTH;
        }
        for (int i = 0; i < count; i++) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.v(TAG + "->" + tag, "->" + message.substring(i * MAX_LENGTH, length));
            } else {
                String str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH);
                if (i == 0) {
                    Log.v(TAG + "->" + tag, str + "->");
                    continue;
                }
                Log.v(TAG + "->" + tag, "->" + str + "->");
            }
        }
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
