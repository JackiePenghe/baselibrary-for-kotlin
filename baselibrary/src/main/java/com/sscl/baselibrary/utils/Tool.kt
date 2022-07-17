package com.sscl.baselibrary.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sscl.baselibrary.R
import com.sscl.baselibrary.bean.PhoneInfo
import com.sscl.baselibrary.receiver.ScreenStatusReceiver
import com.sscl.baselibrary.receiver.ScreenStatusReceiver.OnScreenStatusChangedListener
import java.lang.reflect.Method
import java.util.*
import java.util.regex.Pattern

/**
 * 工具类
 *
 * @author alm
 */
object Tool {
    /*--------------------------------静态常量--------------------------------*/
    /**
     * 屏幕状态的监听广播接收者
     */
    private val SCREEN_STATUS_RECEIVER = ScreenStatusReceiver()
    private const val IP_V4_REGEX =
        "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$"
    /*--------------------------------公开方法--------------------------------*/
    /**
     * 开始监听屏幕状态
     *
     * @param context 上下文
     */
    @kotlin.jvm.JvmStatic
    fun startScreenStatusListener(context: Context): Intent? {
        return context.registerReceiver(SCREEN_STATUS_RECEIVER, screenStatusReceiverIntentFilter)
    }

    /**
     * 停止监听屏幕状态
     *
     * @param context 上下文
     */
    @kotlin.jvm.JvmStatic
    fun stopScreenStatusListener(context: Context) {
        context.unregisterReceiver(SCREEN_STATUS_RECEIVER)
    }

    /**
     * 设置屏幕状态更改的监听
     *
     * @param onScreenStatusChangedListener 屏幕状态更改的监听
     */
    @kotlin.jvm.JvmStatic
    fun setOnScreenStatusChangedListener(onScreenStatusChangedListener: OnScreenStatusChangedListener) {
        SCREEN_STATUS_RECEIVER.setOnScreenStatusChangedListener(onScreenStatusChangedListener)
    }

    /**
     * 检测系统环境是否是中文简体
     *
     * @return true表示为中文简体
     */
    val isZhCn: Boolean
        get() {
            val aDefault = Locale.getDefault()
            val aDefaultStr = aDefault.toString()
            val zhCn = "zh_CN"
            return zhCn.lowercase() == aDefaultStr.lowercase()
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
        Process.killProcess(Process.myPid())
    }

    /**
     * 设置输入框输入类型
     *
     * @param activity  Activity
     * @param editText  输入框
     * @param inputType 输入类型
     */
    @kotlin.jvm.JvmStatic
    fun setInpType(activity: Activity, editText: EditText, inputType: Int) {
        activity.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        editText.inputType = inputType
        try {
            val cls = EditText::class.java
            val setShowSoftInputOnFocus: Method
            setShowSoftInputOnFocus = cls.getMethod(
                "setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType
            )
            setShowSoftInputOnFocus.isAccessible = true
            setShowSoftInputOnFocus.invoke(editText, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 解除输入法的内存泄漏bug
     *
     * @param activity Activity
     */
    fun releaseInputMethodManagerMemory(activity: Activity) {
        //解除输入法内存泄漏
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val inputMethodManagerClass = InputMethodManager::class.java
                @SuppressLint("DiscouragedPrivateApi", "BlockedPrivateApi") val mCurRootViewField =
                    inputMethodManagerClass.getDeclaredField("mCurRootView")
                @SuppressLint("DiscouragedPrivateApi") val mNextServedViewField =
                    inputMethodManagerClass.getDeclaredField("mNextServedView")
                @SuppressLint("DiscouragedPrivateApi") val mServedViewField =
                    inputMethodManagerClass.getDeclaredField("mServedView")
                mCurRootViewField.isAccessible = true
                mNextServedViewField.isAccessible = true
                mServedViewField.isAccessible = true
                val mCurRootView = mCurRootViewField[inputMethodManager]
                if (null != mCurRootView) {
                    val context = (mCurRootView as View).context
                    if (context === activity) {
                        //将该对象设为null，破环GC引用链，防止输入法内存泄漏
                        mCurRootViewField[inputMethodManager] = null
                    }
                }
                val mNextServedView = mNextServedViewField[inputMethodManager]
                if (null != mNextServedView) {
                    val context = (mNextServedView as View).context
                    if (activity === context) {
                        mNextServedViewField[inputMethodManager] = null
                    }
                }
                val mServedView = mServedViewField[inputMethodManager]
                if (null != mServedView) {
                    val context = (mServedView as View).context
                    if (activity === context) {
                        mServedViewField[inputMethodManager] = null
                    }
                }
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 让当前线程阻塞一段时间
     *
     * @param timeMillis 让线程阻塞的时间（单位：毫秒）
     */
    fun sleep(timeMillis: Long) {
        val currentTimeMillis = System.currentTimeMillis()
        while (true) {
            if (System.currentTimeMillis() - currentTimeMillis >= timeMillis) {
                break
            }
        }
    }

    /**
     * 设置文本到粘贴板
     *
     * @param context 上下文
     * @param label   文本说明
     * @param data    文本内容
     * @return true表示设置成功
     */
    fun setDataToClipboard(context: Context, label: String, data: String): Boolean {
        val clipboardManager =
            context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                ?: return false
        val clipData = ClipData.newPlainText(label, data)
        clipboardManager.setPrimaryClip(clipData)
        return true
    }

    /**
     * 从粘贴板获取文本
     *
     * @param context 上下文
     * @return 粘贴板文本
     */
    fun getDataFromClipboard(context: Context): String? {
        return getDataFromClipboard(context, 0)
    }

    /**
     * 获取系统抛出的异常的详细信息
     *
     * @param throwable 系统抛出的异常
     * @return 系统抛出的异常的详细信息
     */
    @kotlin.jvm.JvmStatic
    fun getExceptionDetailsInfo(throwable: Throwable): String {
        val message = throwable.message
        val stackTrace = throwable.stackTrace
        val stringBuilder = StringBuilder()
        stringBuilder.append(message)
        for (traceElement in stackTrace) {
            stringBuilder.append("\nat ").append(traceElement.toString())
        }
        stringBuilder.append("\n")
        stringBuilder.append("suppressed:\n")
        val suppressed = throwable.suppressed
        // Print suppressed exceptions, if any
        for (se in suppressed) {
            for (traceElement in se.stackTrace) {
                stringBuilder.append("\nat ").append(traceElement.toString())
            }
            stringBuilder.append("\n")
        }
        stringBuilder.append("\n")
        stringBuilder.append("cause:\n")
        val ourCause = throwable.cause
        if (ourCause != null) {
            stringBuilder.append(ourCause)
            for (traceElement in ourCause.stackTrace) {
                stringBuilder.append("\nat ").append(traceElement.toString())
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    /**
     * 从粘贴板获取文本
     *
     * @param context 上下文
     * @param index   粘贴板中的文本索引
     * @return 粘贴板文本
     */
    fun getDataFromClipboard(context: Context, index: Int): String? {
        val clipboardManager =
            context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                ?: return null
        val primaryClip = clipboardManager.primaryClip ?: return null
        val itemAt = primaryClip.getItemAt(index)
        return itemAt.text.toString()
    }

    /**
     * 获取本机信息
     *
     * @param context 上下文
     * @return 本机信息
     */
    @SuppressLint("HardwareIds")
    fun getPhoneInfo(context: Context): PhoneInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val selfPermission =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            if (PackageManager.PERMISSION_GRANTED != selfPermission) {
                return null
            }
        }
        val telephonyManager =
            context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                ?: return null
        val deviceId: String
        deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Settings.System.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager.imei
        } else {
            telephonyManager.deviceId
        }
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return PhoneInfo(manufacturer, model, deviceId)
    }

    /**
     * 检查是否符合IPv4格式文本
     *
     * @param ipv4String 字符串
     * @return true表示符合
     */
    fun checkIpv4String(ipv4String: String): Boolean {
        val pattern = Pattern.compile(IP_V4_REGEX)
        val matcher = pattern.matcher(ipv4String)
        return matcher.matches()
    }

    /**
     * 获取RecyclerView第一个可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    fun getFirstVisibleItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
            ?: throw IllegalStateException("RecyclerView has no layoutManager set!")
        check(layoutManager is LinearLayoutManager) { "getFirstVisibleItemPosition only support when layoutManager is LinearLayoutManager" }
        return layoutManager.findFirstVisibleItemPosition()
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
            params.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
            activity.window.attributes = params
        }
    }

    /**
     * 获取RecyclerView第一个可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    fun getFirstCompletelyVisibleItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
            ?: throw IllegalStateException("RecyclerView has no layoutManager set!")
        check(layoutManager is LinearLayoutManager) { "getFirstVisibleItemPosition only support when layoutManager is LinearLayoutManager" }
        return layoutManager.findFirstCompletelyVisibleItemPosition()
    }

    /**
     * 获取RecyclerView最后一个可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    fun getLastVisibleItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
            ?: throw IllegalStateException("RecyclerView has no layoutManager set!")
        check(layoutManager is LinearLayoutManager) { "getFirstVisibleItemPosition only support when layoutManager is LinearLayoutManager" }
        return layoutManager.findLastVisibleItemPosition()
    }

    /**
     * 获取RecyclerView最后一个完全可见的选项位置
     *
     * @param recyclerView RecyclerView
     * @return RecyclerView第一个可见的选项位置
     */
    fun getLastCompletelyVisibleItemPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
            ?: throw IllegalStateException("RecyclerView has no layoutManager set!")
        check(layoutManager is LinearLayoutManager) { "getFirstVisibleItemPosition only support when layoutManager is LinearLayoutManager" }
        return layoutManager.findLastCompletelyVisibleItemPosition()
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
            ?: return
        if (show) {
            if (activity.currentFocus != null) {
                //有焦点打开
                imm.showSoftInput(activity.currentFocus, 0)
            } else {
                if (!needFocus) {
                    //无焦点打开
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
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            }
        }
    }

    /**
     * 转换DP值为象素值
     *
     * @param dp DP值
     * @return 象素值
     */
    fun dpToPx(dp: Double): Double {
        return Resources.getSystem().displayMetrics.density * dp
    }

    /**
     * 获取主题Primary颜色
     *
     * @param context 上下文
     * @return 主题Primary颜色
     */
    @kotlin.jvm.JvmStatic
    fun getColorPrimary(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.data
    }

    /**
     * 获取暗色主题Primary颜色
     *
     * @param context 上下文
     * @return 暗色主题Primary颜色
     */
    fun getDarkColorPrimary(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)
        return typedValue.data
    }

    /**
     * 获取暗色主题Primary颜色
     *
     * @param context 上下文
     * @return 暗色主题Primary颜色
     */
    fun getDarkColorPrimaryVariant(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimaryVariant, typedValue, true)
        return typedValue.data
    }

    /**
     * 获取主题Accent颜色
     *
     * @param context 上下文
     * @return 主题Accent颜色
     */
    fun getColorAccent(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        return typedValue.data
    }

    /**
     * 获取主题状态栏颜色
     *
     * @param context 上下文
     * @return 主题状态栏颜色
     */
    @kotlin.jvm.JvmStatic
    fun getStatusBarColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
        return typedValue.data
    }

    fun exitProcess(status: Int) {
        //退出程序
        Process.killProcess(Process.myPid())
        System.exit(status)
    }

    /**
     * 获取默认的屏幕大小
     *
     * @param context 上下文
     */
    fun getDefaultScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = windowManager.defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        return point
    }
    /*--------------------------------私有方法--------------------------------*/
    /**
     * 获取监听屏幕状态的广播接收者
     *
     * @return 监听屏幕状态的广播接收者
     */
    private val screenStatusReceiverIntentFilter: IntentFilter
        private get() {
            val intentFilter = IntentFilter()
            intentFilter.priority = Int.MAX_VALUE
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
            intentFilter.addAction(Intent.ACTION_SCREEN_ON)
            intentFilter.addAction(Intent.ACTION_USER_PRESENT)
            return intentFilter
        }
}