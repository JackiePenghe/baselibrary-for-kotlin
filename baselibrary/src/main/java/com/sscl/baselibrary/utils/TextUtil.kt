package com.sscl.baselibrary.utils

import android.widget.EditText
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod

/**
 * 文本工具类
 *
 * @author pengh
 */
object TextUtil {
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
}