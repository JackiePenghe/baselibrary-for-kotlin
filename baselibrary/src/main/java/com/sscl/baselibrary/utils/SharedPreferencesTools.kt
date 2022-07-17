package com.sscl.baselibrary.utils

import android.content.*

/**
 * SharedPreferences 工具类
 *
 * @author jackie
 */
class SharedPreferencesTools private constructor(context: Context, fileName: String) {
    /*--------------------------------成员变量-------------------------------*/
    private val sharedPreferences: SharedPreferences
    /*--------------------------------公开方法--------------------------------*/
    /**
     * 存储一个字符串数据
     *
     * @param key   Key
     * @param value 数据
     */
    fun putValueApply(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    /**
     * 立刻存储一个字符串数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    fun putValueImmediately(key: String, value: String?): Boolean {
        return sharedPreferences.edit().putString(key, value).commit()
    }

    /**
     * 存储一个布尔数据
     *
     * @param key   Key
     * @param value 数据
     */
    fun putValueApply(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    /**
     * 立刻存储一个布尔数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    fun putValueImmediately(key: String, value: Boolean): Boolean {
        return sharedPreferences.edit().putBoolean(key, value).commit()
    }

    /**
     * 存储一个浮点数据
     *
     * @param key   Key
     * @param value 数据
     */
    fun putValueApply(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    /**
     * 立刻存储一个浮点数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    fun putValueImmediately(key: String, value: Float): Boolean {
        return sharedPreferences.edit().putFloat(key, value).commit()
    }

    /**
     * 存储一个整形数据
     *
     * @param key   Key
     * @param value 数据
     */
    fun putValueApply(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    /**
     * 立刻存储一个整形数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    fun putValueImmediately(key: String, value: Int): Boolean {
        return sharedPreferences.edit().putInt(key, value).commit()
    }

    /**
     * 存储一个长整形数据
     *
     * @param key   Key
     * @param value 数据
     */
    fun putValueApply(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    /**
     * 立刻存储一个长整形数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    fun putValueImmediately(key: String, value: Long): Boolean {
        return sharedPreferences.edit().putLong(key, value).commit()
    }

    /**
     * 存储字符串集合数据
     *
     * @param key   Key
     * @param value 数据
     */
    fun putValueApply(key: String, value: Set<String?>?) {
        sharedPreferences.edit().putStringSet(key, value).apply()
    }

    /**
     * 立刻存储字符串集合数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    fun putValueImmediately(key: String, value: Set<String?>?): Boolean {
        return sharedPreferences.edit().putStringSet(key, value).commit()
    }

    /**
     * 获取字符串数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 字符串数据
     */
    fun getValue(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    /**
     * 获取布尔数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 布尔数据
     */
    fun getValue(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * 获取浮点数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 浮点数据
     */
    fun getValue(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    /**
     * 获取整形数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 整形数据
     */
    fun getValue(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    /**
     * 获取长整形数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 长整形数据
     */
    fun getValue(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    /**
     * 获取长整形数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 长整形数据
     */
    fun getValue(key: String, defaultValue: Set<String?>?): Set<String>? {
        return sharedPreferences.getStringSet(key, defaultValue)
    }

    /**
     * 获取所有数据
     *
     * @return 数据集合
     */
    val all: Map<String, *>
        get() {
            return sharedPreferences.getAll()
        }

    companion object {
        /*--------------------------------静态成员变量--------------------------------*/
        private var sharedPreferencesTools: SharedPreferencesTools? = null
        /*--------------------------------公开静态方法--------------------------------*/
        /**
         * 初始化
         *
         * @param context 上下文
         * @return 本类单例
         */
        @kotlin.jvm.JvmStatic
        fun getInstance(context: Context, fileName: String): SharedPreferencesTools {
            if (sharedPreferencesTools == null) {
                synchronized(SharedPreferencesTools::class.java) {
                    if (sharedPreferencesTools == null) {
                        sharedPreferencesTools = SharedPreferencesTools(context, fileName)
                    }
                }
            }
            return sharedPreferencesTools!!
        }
    }

    /*--------------------------------构造方法--------------------------------*/
    init {
        sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }
}