package com.sscl.baselibrary.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sscl.baselibrary.R
import com.sscl.baselibrary.bean.DeviceInfo
import java.lang.reflect.Method

/**
 * 工具类
 *
 * @author alm
 */
object Tool {

    /*--------------------------------公开方法--------------------------------*/

    /**
     * 检测byte数组中的内容有效性（全0为无效）
     *
     * @param byteArray byte数组
     * @return true表示有效
     */
    fun checkByteValid(byteArray: ByteArray): Boolean {
        for (aByte: Byte in byteArray) {
            if (aByte.toInt() != 0) {
                return true
            }
        }
        return false
    }

    fun showEditTextPassword(passwordEt: EditText, showPassword: Boolean) {
        if (showPassword) {
            passwordEt.inputType = InputType.TYPE_CLASS_TEXT
            //setTransformationMethod 支持将输入的字符转换，包括清除换行符、转换为掩码
            passwordEt.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
        } else {
            passwordEt.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordEt.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        passwordEt.setSelection(passwordEt.text.toString().length)
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
            val setShowSoftInputOnFocus: Method = cls.getMethod(
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
    fun getPhoneInfo(context: Context): DeviceInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val selfPermission =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            if (PackageManager.PERMISSION_GRANTED != selfPermission) {
                return null
            }
        }
        val telephonyManager =
            context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        @Suppress("DEPRECATION") val deviceId: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
        return DeviceInfo(manufacturer, model, deviceId)
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
}