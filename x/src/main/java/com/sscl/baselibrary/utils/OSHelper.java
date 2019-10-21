package com.sscl.baselibrary.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 系统类型检测工具类（可识别MIUI，EMUI，Flyme系统）
 *
 * @author alm
 */
public class OSHelper {

    /*--------------------------------静态常量--------------------------------*/

    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String MEIZU_FLAG_DARK_STATUS_BAR_ICON = "MEIZU_FLAG_DARK_STATUS_BAR_ICON";

    /*--------------------------------公开静态方法--------------------------------*/

    /**
     * 判断当前系统是否为Flyme
     *
     * @return true表示当前为Flyme系统
     */
    public static boolean isFlyme() {
        try {
            //noinspection JavaReflectionMemberAccess
            Build.class.getMethod("hasSmartBar");
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 判断当前系统是否为Emui
     *
     * @return true表示当前为Emui系统
     */
    public static boolean isEmui() {
        return isPropertiesExist(KEY_EMUI_VERSION_CODE);
    }

    /**
     * 判断当前系统是否为Miui
     *
     * @return true表示当前为Miui系统
     */
    @SuppressWarnings("unused")
    public static boolean isMiui() {
        return isPropertiesExist(KEY_MIUI_VERSION_CODE, KEY_MIUI_VERSION_NAME, KEY_MIUI_INTERNAL_STORAGE);
    }

    /*--------------------------------私有静态方法--------------------------------*/

    /**
     * 判断系统中是否存在某（几）个属性
     *
     * @param keys 属性
     * @return true表示存在
     */
    private static boolean isPropertiesExist(@Nullable String... keys) {
        if (keys == null || keys.length == 0) {
            return false;
        }
        return true;
    }

    /*--------------------------------公开静态方法--------------------------------*/

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean flymeSetStatusBarLightMode(@NonNull Window window, boolean dark) {
        boolean result = false;
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            @SuppressWarnings("JavaReflectionMemberAccess") Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField(MEIZU_FLAG_DARK_STATUS_BAR_ICON);
            @SuppressWarnings("JavaReflectionMemberAccess") Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUI6及以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean miuiSetStatusBarLightMode(@NonNull Window window, boolean dark) {
        Class clazz = window.getClass();
        try {
            int darkModeFlag;
            @SuppressLint("PrivateApi") Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            //noinspection unchecked
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (dark) {
                //状态栏透明且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                //清除黑色字体
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*--------------------------------静态内部类--------------------------------*/

    /**
     * 获取系统属性的类
     */
    @SuppressWarnings("WeakerAccess")
    private static class BuildProperties {

        /*--------------------------------成员变量--------------------------------*/

        /**
         * 系统属性
         */
        private Properties properties;

        /*--------------------------------构造方法--------------------------------*/

        /**
         * 构造方法
         *
         * @throws IOException IO异常
         */
        private BuildProperties() throws IOException {
            properties = new Properties();
            // 读取系统配置信息build.prop类
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        }

        /*--------------------------------公开方法--------------------------------*/

        /**
         * 判断系统是否存在某个属性key
         *
         * @param key 属性key
         * @return true表示系统存在这个属性key
         */
        public boolean containsKey(@NonNull final Object key) {
            return properties.containsKey(key);
        }

        /**
         * 判断系统是否存在某个属性value
         *
         * @param value 属性value
         * @return true表示系统存在这个属性value
         */
        public boolean containsValue(@NonNull final Object value) {
            return properties.containsValue(value);
        }

        /**
         * 获取系统属性的set集合
         *
         * @return 系统属性的set集合
         */
        @NonNull
        public Set<Map.Entry<Object, Object>> entrySet() {
            return properties.entrySet();
        }

        /**
         * 通过属性名获取系统属性
         *
         * @param name 属性名
         * @return 系统属性
         */
        @NonNull
        public String getProperty(@NonNull String name) {
            return properties.getProperty(name);
        }

        /**
         * 通过属性名和属性值获取系统属性
         *
         * @param name         属性名
         * @param defaultValue 属性值
         * @return 系统属性
         */
        @NonNull
        public String getProperty(@NonNull final String name, @NonNull final String defaultValue) {
            return properties.getProperty(name, defaultValue);
        }

        /**
         * 判断系统属性是否为空
         *
         * @return true表示为空
         */
        public boolean isEmpty() {
            return properties.isEmpty();
        }

        /**
         * 获取系统属性所有的key
         *
         * @return 系统属性所有的key
         */
        @NonNull
        public Enumeration<Object> keys() {
            return properties.keys();
        }

        /**
         * 将系统属性所有的key转为set集合
         *
         * @return set集合
         */
        @NonNull
        public Set<Object> keySet() {
            return properties.keySet();
        }

        /**
         * 获取系统属性的大小
         *
         * @return 系统属性的大小
         */
        public int size() {
            return properties.size();
        }

        /**
         * 获取系统属性所有的values
         *
         * @return 系统属性所有的values
         */

        public Collection<Object> values() {
            return properties.values();
        }

        /*--------------------------------静态方法--------------------------------*/

        /**
         * 创建BuildProperties本类实例
         *
         * @return BuildProperties本类实例
         * @throws IOException IO异常
         */
        public static BuildProperties newInstance() throws IOException {
            return new BuildProperties();
        }
    }
}
