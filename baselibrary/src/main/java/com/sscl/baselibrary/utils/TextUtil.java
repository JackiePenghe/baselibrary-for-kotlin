package com.sscl.baselibrary.utils;

import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;

/**
 * 文本工具类
 *
 * @author pengh
 */
public class TextUtil {

    public static void showEditTextPassword(@NonNull EditText passwordEt, boolean showPassword){
        if (showPassword) {
            passwordEt.setInputType(InputType.TYPE_CLASS_TEXT);
            //setTransformationMethod 支持将输入的字符转换，包括清除换行符、转换为掩码
            passwordEt.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
        } else {
            passwordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEt.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
        }
        passwordEt.setSelection(passwordEt.getText().toString().length());
    }
}
