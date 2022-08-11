package com.sscl.baselibrary.utils

import android.app.Activity
import android.os.Environment
import android.os.Build
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Point
import android.util.Log
import kotlin.Throws
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.sscl.baselibrary.receiver.ScreenStatusReceiver
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.PrintWriter
import java.lang.Exception
import java.lang.reflect.Method
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

    /**
     * 屏幕状态的监听广播接收者
     */
    private val SCREEN_STATUS_RECEIVER = ScreenStatusReceiver()

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

    /**
     * 判断手机是否有root权限
     */
    fun hasRootPermission(): Boolean {
        val printWriter: PrintWriter
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("su")
            printWriter = PrintWriter(process.outputStream)
            printWriter.flush()
            printWriter.close()
            val value = process.waitFor()
            return returnResult(value)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            process?.destroy()
        }
        return false
    }

    /**
     * 启动app
     * com.exmaple.client/.MainActivity
     * com.exmaple.client/com.exmaple.client.MainActivity
     */
    fun startApp(packageName: String, activityName: String): Boolean {
        val isSuccess = false
        val cmd = "am start -n $packageName/$activityName \n"
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(cmd)
            val value = process.waitFor()
            return returnResult(value)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            process?.destroy()
        }
        return isSuccess
    }

    /**
     * 将文件复制到system/app 目录
     *
     * @param apkPath 特别注意格式：该路径不能是：/storage/emulated/0/app/QDemoTest4.apk 需要是：/sdcard/app/QDemoTest4.apk
     * @return 是否成功
     */
    fun copy2SystemApp(apkPath: String): Boolean {
        val printWriter: PrintWriter
        var process: Process? = null
        val appName = "chetou.apk"
        var cmd: String
        try {
            process = Runtime.getRuntime().exec("su")
            printWriter = PrintWriter(process.outputStream)
            cmd = "mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system"
            Log.e("copy2SystemApp", cmd)
            printWriter.println(cmd)
            cmd = "cat $apkPath > /system/app/$appName"
            Log.e("copy2SystemApp", cmd)
            printWriter.println(cmd)
            cmd = "chmod 777 /system/app/$appName -R"
            Log.e("copy2SystemApp", cmd)
            printWriter.println(cmd)
            cmd = "mount -o remount,ro -t yaffs2 /dev/block/mtdblock3 /system"
            Log.e("copy2SystemApp", cmd)
            printWriter.println(cmd)
            printWriter.println("reboot") //重启
            printWriter.println("exit")
            printWriter.flush()
            printWriter.close()
            val value = process.waitFor()
            return returnResult(value)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            process?.destroy()
        }
        return false
    }

    fun returnResult(value: Int): Boolean {
        // 代表成功
        return if (value == 0) {
            true
        } else if (value == 1) { // 失败
            false
        } else { // 未知情况
            false
        }
    }

    /**
     * 使用代码触发home键的效果
     *
     * @param context 上下文
     */
    fun pressHomeButton(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        // 注意:必须加上这句代码，否则就不是单例了
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addCategory(Intent.CATEGORY_HOME)
        context.startActivity(intent)
    }

    /**
     * 隐藏系统键盘
     *
     * @param activity Activity
     * @param editText EditText
     */
    fun hideSoftInputMethod(activity: Activity, editText: EditText?) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val methodName = "setShowSoftInputOnFocus"
        val cls: Class<EditText> = EditText::class.java
        val setShowSoftInputOnFocus: Method
        try {
            setShowSoftInputOnFocus = cls.getMethod(methodName, Boolean::class.javaPrimitiveType)
            setShowSoftInputOnFocus.isAccessible = true
            setShowSoftInputOnFocus.invoke(editText, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 隐藏输入法
     *
     * @param view The view.
     */
    fun hideSoftInput(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 开始监听屏幕状态
     *
     * @param context 上下文
     */
    fun startScreenStatusListener(context: Context): Intent? {
        return context.registerReceiver(
            SCREEN_STATUS_RECEIVER,
            screenStatusReceiverIntentFilter
        )
    }

    /**
     * 设置屏幕状态更改的监听
     *
     * @param onScreenStatusChangedListener 屏幕状态更改的监听
     */
    @kotlin.jvm.JvmStatic
    fun setOnScreenStatusChangedListener(onScreenStatusChangedListener: ScreenStatusReceiver.OnScreenStatusChangedListener) {
        SCREEN_STATUS_RECEIVER.setOnScreenStatusChangedListener(onScreenStatusChangedListener)
    }

    /**
     * 停止监听屏幕状态
     *
     * @param context 上下文
     */
    fun stopScreenStatusListener(context: Context) {
        context.unregisterReceiver(SCREEN_STATUS_RECEIVER)
    }

    /**
     * 重启应用程序
     *
     * @param context 上下文
     */
    @kotlin.jvm.JvmStatic
    fun restartApplication(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    /**
     * 隐藏导航栏
     *
     * @param activity activity
     */
    @kotlin.jvm.JvmStatic
    fun hideNavigationBar(activity: Activity) {
        //隐藏导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.decorView.windowInsetsController!!.hide(WindowInsets.Type.navigationBars())
        } else {
            val params = activity.window.attributes
            @Suppress("DEPRECATION")
            params.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
            activity.window.attributes = params
        }
    }

    /**
     * 设置软键状态
     *
     * @param activity 上下文
     * @param show     是否显示
     */
    fun setInputMethodState(activity: Activity, show: Boolean) {
        setInputMethodState(activity, show, false)
    }

    /**
     * 设置软键状态
     *
     * @param activity 上下文
     * @param show     是否显示
     */
    fun setInputMethodState(activity: Activity, show: Boolean, needFocus: Boolean) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            if (activity.currentFocus != null) {
                //有焦点打开
                imm.showSoftInput(activity.currentFocus, 0)
            } else {
                if (!needFocus) {
                    //无焦点打开
                    @Suppress("DEPRECATION")
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
            }
        } else {
            if (activity.currentFocus != null) {
                //有焦点关闭
                imm.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            } else {
                if (!needFocus) {
                    //无焦点关闭
                    @Suppress("DEPRECATION")
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            }
        }
    }

    /**
     * 退出程序
     */
    fun exitProcess(status: Int) {
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid())
        kotlin.system.exitProcess(status)
    }

    /**
     * 获取默认的屏幕大小
     *
     * @param context 上下文
     */
    fun getDefaultScreenSize(context: Context): Point {
        val point = Point()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val currentWindowMetrics = windowManager.currentWindowMetrics
            val bounds = currentWindowMetrics.bounds
            point.x = bounds.right - bounds.left
            point.y = bounds.bottom - bounds.top
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getSize(point)
        }
        return point
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

    /**
     * 获取监听屏幕状态的广播接收者
     *
     * @return 监听屏幕状态的广播接收者
     */
    private val screenStatusReceiverIntentFilter: IntentFilter
        get() {
            val intentFilter = IntentFilter()
            intentFilter.priority = Int.MAX_VALUE
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
            intentFilter.addAction(Intent.ACTION_SCREEN_ON)
            intentFilter.addAction(Intent.ACTION_USER_PRESENT)
            return intentFilter
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

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 私有方法
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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
         */
        init {
            // 读取系统配置信息build.prop类
            properties.load(FileInputStream(File(Environment.getRootDirectory(), "build.prop")))
        }
    }
}