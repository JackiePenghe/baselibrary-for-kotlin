package com.sscl.baselibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import java.lang.reflect.Method;

/**
 * 输入框工具类
 *
 * @author jackie
 */
public class EditTextUtils {

    /**
     * 隐藏系统键盘
     *
     * @param activity Activity
     * @param editText EditText
     */
    public static void hideSoftInputMethod(Activity activity, EditText editText) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        String methodName = "setShowSoftInputOnFocus";
        Class<EditText> cls = EditText.class;
        Method setShowSoftInputOnFocus;
        try {
            setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
            setShowSoftInputOnFocus.setAccessible(true);
            setShowSoftInputOnFocus.invoke(editText, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏输入法
     *
     * @param view The view.
     */
    public static void hideSoftInput(Context context, final View view) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
