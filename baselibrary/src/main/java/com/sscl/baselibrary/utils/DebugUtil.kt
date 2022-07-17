package com.sscl.baselibrary.utils

import android.util.Log

/**
 * Debug日志打印工具类
 *
 * @author pengh
 */
object DebugUtil {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 私有静态常量属性 * * * * * * * * * * * * * * * * * * */

    private const val MAX_LENGTH = 2000

    /* * * * * * * * * * * * * * * * * * * 私有可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * log的TAG
     */
    private var TAG = "BaseLibrary->"
    /**
     * 获取当前日志打印标志
     *
     * @return 日志打印标志
     */
    /**
     * 是否打印日志信息的标志
     */
    var isDebug = false
        private set

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 设置日志打印标志
     *
     * @param debug 日志打印标志
     */
    fun setDebugFlag(debug: Boolean) {
        isDebug = debug
    }

    /**
     * 设置默认的tag
     *
     * @param tag 默认的tag
     */
    fun setDefaultTAG(tag: String) {
        TAG = tag
    }

    /**
     * 等同于Log.i
     *
     * @param tag     tag
     * @param message 日志信息
     */
    fun infoOut(tag: String, message: String) {
        if (!isDebug) {
            return
        }
        val length = message.length
        if (length <= MAX_LENGTH) {
            Log.i("$TAG->$tag", message)
            return
        }
        val count: Int = if (length % MAX_LENGTH != 0) {
            length / MAX_LENGTH + 1
        } else {
            length / MAX_LENGTH
        }
        for (i in 0 until count) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.i("$TAG->$tag", "->" + message.substring(i * MAX_LENGTH, length))
            } else {
                val str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH)
                if (i == 0) {
                    Log.i("$TAG->$tag", "$str->")
                    continue
                }
                Log.i("$TAG->$tag", "->$str->")
            }
        }
    }

    /**
     * 等同于Log.i
     *
     * @param message 日志信息
     */
    fun infoOut(message: String) {
        infoOut(TAG, message)
    }

    /**
     * 等同于Log.e
     *
     * @param tag     tag
     * @param message 日志信息
     */
    fun errorOut(tag: String, message: String) {
        if (!isDebug) {
            return
        }
        val length = message.length
        if (length <= MAX_LENGTH) {
            Log.e("$TAG->$tag", message)
            return
        }
        val count: Int = if (length % MAX_LENGTH != 0) {
            length / MAX_LENGTH + 1
        } else {
            length / MAX_LENGTH
        }
        for (i in 0 until count) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.e("$TAG->$tag", "->" + message.substring(i * MAX_LENGTH, length))
            } else {
                val str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH)
                if (i == 0) {
                    Log.e("$TAG->$tag", "$str->")
                    continue
                }
                Log.e("$TAG->$tag", "->$str->")
            }
        }
    }

    /**
     * 等同于Log.e
     *
     * @param message 日志信息
     */
    fun errorOut(message: String) {
        errorOut(TAG, message)
    }

    /**
     * 等同于Log.d
     *
     * @param tag     tag
     * @param message 日志信息
     */
    fun debugOut(tag: String, message: String) {
        if (!isDebug) {
            return
        }
        val length = message.length
        if (length <= MAX_LENGTH) {
            Log.d("$TAG->$tag", message)
            return
        }
        val count: Int = if (length % MAX_LENGTH != 0) {
            length / MAX_LENGTH + 1
        } else {
            length / MAX_LENGTH
        }
        for (i in 0 until count) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.d("$TAG->$tag", "->" + message.substring(i * MAX_LENGTH, length))
            } else {
                val str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH)
                if (i == 0) {
                    Log.d("$TAG->$tag", "$str->")
                    continue
                }
                Log.d("$TAG->$tag", "->$str->")
            }
        }
    }

    /**
     * 等同于Log.d
     *
     * @param message 日志信息
     */
    fun debugOut(message: String) {
        debugOut(TAG, message)
    }

    /**
     * 等同于Log.w
     *
     * @param tag     tag
     * @param message 日志信息
     */
    fun warnOut(tag: String, message: String) {
        if (!isDebug) {
            return
        }
        val length = message.length
        if (length <= MAX_LENGTH) {
            Log.w("$TAG->$tag", message)
            return
        }
        val count: Int = if (length % MAX_LENGTH != 0) {
            length / MAX_LENGTH + 1
        } else {
            length / MAX_LENGTH
        }
        for (i in 0 until count) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.w("$TAG->$tag", "->" + message.substring(i * MAX_LENGTH, length))
            } else {
                val str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH)
                if (i == 0) {
                    Log.w("$TAG->$tag", "$str->")
                    continue
                }
                Log.w("$TAG->$tag", "->$str->")
            }
        }
    }

    /**
     * 等同于Log.w
     *
     * @param message 日志信息
     */
    fun warnOut(message: String) {
        warnOut(TAG, message)
    }

    /**
     * 等同于Log.v
     *
     * @param tag     tag
     * @param message 日志信息
     */
    fun verOut(tag: String, message: String) {
        if (!isDebug) {
            return
        }
        val length = message.length
        if (length <= MAX_LENGTH) {
            Log.v("$TAG->$tag", message)
            return
        }
        val count: Int = if (length % MAX_LENGTH != 0) {
            length / MAX_LENGTH + 1
        } else {
            length / MAX_LENGTH
        }
        for (i in 0 until count) {
            if ((i + 1) * MAX_LENGTH > length) {
                Log.v("$TAG->$tag", "->" + message.substring(i * MAX_LENGTH, length))
            } else {
                val str = message.substring(i * MAX_LENGTH, (i + 1) * MAX_LENGTH)
                if (i == 0) {
                    Log.v("$TAG->$tag", "$str->")
                    continue
                }
                Log.v("$TAG->$tag", "->$str->")
            }
        }
    }

    /**
     * 等同于Log.v
     *
     * @param message 日志信息
     */
    fun verOut(message: String) {
        verOut(TAG, message)
    }
}