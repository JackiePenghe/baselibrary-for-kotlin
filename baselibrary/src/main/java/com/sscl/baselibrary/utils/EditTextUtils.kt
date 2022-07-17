package com.sscl.baselibrary.utils

import android.app.Activity
import android.widget.EditText
import android.content.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import java.lang.Exception
import java.lang.reflect.Method

/**
 * 输入框工具类
 *
 * @author jackie
 */
object EditTextUtils {
    /**
     * 隐藏系统键盘
     *
     * @param activity Activity
     * @param editText EditText
     */
    fun hideSoftInputMethod(activity: Activity, editText: EditText?) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val methodName: String = "setShowSoftInputOnFocus"
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
}