package com.sscl.baselibrary.textwatcher;


import android.widget.EditText;

import androidx.annotation.NonNull;


/**
 * 自定义EditText输入过滤（限制只能输入十六进制字符：蓝牙MAC地址）
 *
 * @author jackie
 */
public class BluetoothAddressTextWatch extends HexTextAutoAddCharInputWatcher {
    /**
     * Creates an instance of <code>CustomTextWatcher</code>.
     *
     * @param editText the editText to edit text.
     */
    public BluetoothAddressTextWatch(@NonNull EditText editText) {
        super(editText,6,":");
    }
}
