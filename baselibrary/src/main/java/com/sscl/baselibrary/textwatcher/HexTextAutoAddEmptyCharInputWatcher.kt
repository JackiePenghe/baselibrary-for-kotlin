package com.sscl.baselibrary.textwatcher

import android.widget.EditText

/**
 * 自定义EditText输入过滤（限制只能输入十六进制字符，并且自动填充空格分割）
 *
 * @author alm
 */
class HexTextAutoAddEmptyCharInputWatcher
/**
 * Creates an instance of `CustomTextWatcher`.
 *
 * @param editText  the editText to edit text.
 * @param maxInput 最大输入字节数
 */
constructor(editText: EditText, maxInput: Int) :
    HexTextAutoAddCharInputWatcher(editText, maxInput, " ")