package com.sscl.baselibrary.textwatcher

import android.widget.EditText

/**
 * 自定义EditText输入过滤（限制只能输入十六进制字符：蓝牙MAC地址）
 *
 * @author jackie
 */
class BluetoothAddressTextWatch
/**
 * Creates an instance of `CustomTextWatcher`.
 *
 * @param editText the editText to edit text.
 */
constructor(editText: EditText) : HexTextAutoAddCharInputWatcher(editText, 6, ":")