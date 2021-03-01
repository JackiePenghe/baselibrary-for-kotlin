package com.sscl.baselibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences 工具类
 *
 * @author jackie
 */
public class SharedPreferencesTools {

    /*--------------------------------静态成员变量--------------------------------*/

    private static SharedPreferencesTools sharedPreferencesTools;

    /*--------------------------------成员变量-------------------------------*/

    private final SharedPreferences sharedPreferences;

    /*--------------------------------构造方法--------------------------------*/

    private SharedPreferencesTools(@NonNull Context context, String fileName) {
        sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /*--------------------------------公开静态方法--------------------------------*/

    /**
     * 初始化
     *
     * @param context 上下文
     * @return 本类单例
     */
    public static SharedPreferencesTools getInstance(@NonNull Context context, String fileName) {
        if (sharedPreferencesTools == null) {
            synchronized (SharedPreferencesTools.class) {
                if (sharedPreferencesTools == null) {
                    sharedPreferencesTools = new SharedPreferencesTools(context, fileName);
                }
            }
        }
        return sharedPreferencesTools;
    }

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 存储一个字符串数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValueApply(@NonNull String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * 立刻存储一个字符串数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public boolean putValueImmediately(@NonNull String key, String value) {
        return sharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 存储一个布尔数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValueApply(@NonNull String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * 立刻存储一个布尔数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public boolean putValueImmediately(@NonNull String key, boolean value) {
        return sharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 存储一个浮点数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValueApply(@NonNull String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    /**
     * 立刻存储一个浮点数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public boolean putValueImmediately(@NonNull String key, float value) {
        return sharedPreferences.edit().putFloat(key, value).commit();
    }

    /**
     * 存储一个整形数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValueApply(@NonNull String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * 立刻存储一个整形数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public boolean putValueImmediately(@NonNull String key, int value) {
        return sharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * 存储一个长整形数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValueApply(@NonNull String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    /**
     * 立刻存储一个长整形数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public boolean putValueImmediately(@NonNull String key, long value) {
        return sharedPreferences.edit().putLong(key, value).commit();
    }

    /**
     * 存储字符串集合数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValueApply(@NonNull String key, Set<String> value) {
        sharedPreferences.edit().putStringSet(key, value).apply();
    }

    /**
     * 立刻存储字符串集合数据
     *
     * @param key   Key
     * @param value 数据
     * @return Returns true if the new values were successfully written
     * to persistent storage.
     */
    public boolean putValueImmediately(@NonNull String key, Set<String> value) {
        return sharedPreferences.edit().putStringSet(key, value).commit();
    }

    /**
     * 获取字符串数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 字符串数据
     */
    public String getValue(@NonNull String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * 获取布尔数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 布尔数据
     */
    public boolean getValue(@NonNull String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * 获取浮点数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 浮点数据
     */
    public float getValue(@NonNull String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * 获取整形数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 整形数据
     */
    public int getValue(@NonNull String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * 获取长整形数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 长整形数据
     */
    public long getValue(@NonNull String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    /**
     * 获取长整形数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 长整形数据
     */
    public Set<String> getValue(@NonNull String key, Set<String> defaultValue) {
        return sharedPreferences.getStringSet(key, defaultValue);
    }

    /**
     * 获取所有数据
     *
     * @return 数据集合
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }
} 