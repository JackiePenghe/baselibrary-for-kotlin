package com.sscl.baselibrary.utils;

import android.content.Context;

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

    private final Context context;
    private final String fileName;

    /*--------------------------------构造方法--------------------------------*/

    private SharedPreferencesTools(@NonNull Context context, String fileName) {
        this.context = context.getApplicationContext();
        this.fileName = fileName;
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
    public void putValue(@NonNull String key, String value) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putString(key, value).apply();
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
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    /**
     * 存储一个布尔数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValue(@NonNull String key, boolean value) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
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
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    /**
     * 存储一个浮点数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValue(@NonNull String key, float value) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putFloat(key, value).apply();
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
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putFloat(key, value).commit();
    }

    /**
     * 存储一个整形数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValue(@NonNull String key, int value) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putInt(key, value).apply();
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
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    /**
     * 存储一个长整形数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValue(@NonNull String key, long value) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putLong(key, value).apply();
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
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putLong(key, value).commit();
    }

    /**
     * 存储字符串集合数据
     *
     * @param key   Key
     * @param value 数据
     */
    public void putValue(@NonNull String key, Set<String> value) {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putStringSet(key, value).apply();
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
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().putStringSet(key, value).commit();
    }

    /**
     * 获取字符串数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 字符串数据
     */
    public String getValue(@NonNull String key, String defaultValue) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getString(key, defaultValue);
    }

    /**
     * 获取布尔数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 布尔数据
     */
    public boolean getValue(@NonNull String key, boolean defaultValue) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getBoolean(key, defaultValue);
    }

    /**
     * 获取浮点数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 浮点数据
     */
    public float getValue(@NonNull String key, float defaultValue) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getFloat(key, defaultValue);
    }

    /**
     * 获取整形数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 整形数据
     */
    public int getValue(@NonNull String key, int defaultValue) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getInt(key, defaultValue);
    }

    /**
     * 获取长整形数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 长整形数据
     */
    public long getValue(@NonNull String key, long defaultValue) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getLong(key, defaultValue);
    }

    /**
     * 获取长整形数据
     *
     * @param key          Key
     * @param defaultValue 默认值
     * @return 长整形数据
     */
    public Set<String> getValue(@NonNull String key, Set<String> defaultValue) {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getStringSet(key, defaultValue);
    }

    /**
     * 获取所有数据
     *
     * @return 数据集合
     */
    public Map<String, ?> getAll() {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE).getAll();
    }
} 