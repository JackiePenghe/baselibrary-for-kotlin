package com.sscl.baselibrary.utils

import android.app.Activity
import android.os.Environment
import android.os.Build
import android.annotation.SuppressLint
import android.graphics.Color
import kotlin.Throws
import android.view.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

/**
 * 系统类型检测工具类（可识别MIUI，EMUI，Flyme系统）
 *
 * @author alm
 */
object SystemUtil {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private const val KEY_EMUI_VERSION_CODE = "ro.build.version.emui"
    private const val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private const val KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    private const val KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage"
    private const val MEIZU_FLAG_DARK_STATUS_BAR_ICON = "MEIZU_FLAG_DARK_STATUS_BAR_ICON"

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开属性
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 判断当前系统是否为Flyme
     *
     * @return true表示当前为Flyme系统
     */
    val isFlyme: Boolean
        get() = try {
            Build::class.java.getMethod("hasSmartBar")
            true
        } catch (e: Exception) {
            false
        }

    /**
     * 判断当前系统是否为Emui
     *
     * @return true表示当前为Emui系统
     */
    val isEmui: Boolean
        get() = isPropertiesExist(KEY_EMUI_VERSION_CODE)

    /**
     * 判断当前系统是否为Miui
     *
     * @return true表示当前为Miui系统
     */
    val isMiui: Boolean
        get() = isPropertiesExist(
            KEY_MIUI_VERSION_CODE,
            KEY_MIUI_VERSION_NAME,
            KEY_MIUI_INTERNAL_STORAGE
        )

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 公开方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    fun flymeSetStatusBarLightMode(window: Window, dark: Boolean): Boolean {
        var result = false
        try {
            val lp = window.attributes
            val darkFlag = WindowManager.LayoutParams::class.java
                .getDeclaredField(MEIZU_FLAG_DARK_STATUS_BAR_ICON)
            val meizuFlags = WindowManager.LayoutParams::class.java
                .getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            value = if (dark) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            window.attributes = lp
            result = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUI6及以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    fun miuiSetStatusBarLightMode(window: Window, dark: Boolean): Boolean {
        val clazz: Class<*> = window.javaClass
        try {
            val darkModeFlag: Int
            @SuppressLint("PrivateApi") val layoutParams =
                Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            if (dark) {
                //状态栏透明且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
            } else {
                //清除黑色字体
                extraFlagField.invoke(window, 0, darkModeFlag)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 私有方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 判断系统中是否存在某（几）个属性
     *
     * @param keys 属性
     * @return true表示存在
     */
    private fun isPropertiesExist(vararg keys: String): Boolean {
        return keys.isNotEmpty()
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态内部类
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * 获取系统属性的类
     */
    private class BuildProperties private constructor() {
        /**
         * 系统属性
         */
        private val properties: Properties = Properties()

        /**
         * 判断系统是否存在某个属性key
         *
         * @param key 属性key
         * @return true表示系统存在这个属性key
         */
        fun containsKey(key: Any): Boolean {
            return properties.containsKey(key)
        }

        /**
         * 判断系统是否存在某个属性value
         *
         * @param value 属性value
         * @return true表示系统存在这个属性value
         */
        fun containsValue(value: Any): Boolean {
            return properties.containsValue(value)
        }

        /**
         * 获取系统属性的set集合
         *
         * @return 系统属性的set集合
         */
        fun entrySet(): Set<Map.Entry<Any, Any>> {
            return properties.entries
        }

        /**
         * 通过属性名获取系统属性
         *
         * @param name 属性名
         * @return 系统属性
         */
        fun getProperty(name: String): String {
            return properties.getProperty(name)
        }

        /**
         * 通过属性名和属性值获取系统属性
         *
         * @param name         属性名
         * @param defaultValue 属性值
         * @return 系统属性
         */
        fun getProperty(name: String, defaultValue: String): String {
            return properties.getProperty(name, defaultValue)
        }

        /**
         * 判断系统属性是否为空
         *
         * @return true表示为空
         */
        val isEmpty: Boolean
            get() = properties.isEmpty

        /**
         * 获取系统属性所有的key
         *
         * @return 系统属性所有的key
         */
        fun keys(): Enumeration<Any> {
            return properties.keys()
        }

        /**
         * 将系统属性所有的key转为set集合
         *
         * @return set集合
         */
        fun keySet(): Set<Any> {
            return properties.keys
        }

        /**
         * 获取系统属性的大小
         *
         * @return 系统属性的大小
         */
        fun size(): Int {
            return properties.size
        }

        /**
         * 获取系统属性所有的values
         *
         * @return 系统属性所有的values
         */
        fun values(): Collection<Any> {
            return properties.values
        }

        companion object {
            /**
             * 创建BuildProperties本类实例
             *
             * @return BuildProperties本类实例
             * @throws IOException IO异常
             */
            @Throws(IOException::class)
            fun newInstance(): BuildProperties {
                return BuildProperties()
            }
        }

        /**
         * 构造方法
         *
         * @throws IOException IO异常
         */
        init {
            // 读取系统配置信息build.prop类
            properties.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
        }
    }
}