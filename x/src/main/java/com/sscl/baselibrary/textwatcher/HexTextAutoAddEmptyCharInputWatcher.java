package com.sscl.baselibrary.textwatcher;


import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.utils.DebugUtil;


/**
 * 自定义EditText输入过滤（限制只能输入十六进制字符，并且自动填充空格分割）
 *
 * @author alm
 */
public class HexTextAutoAddEmptyCharInputWatcher extends HexTextAutoAddCharInputWatcher {

    /**
     * Creates an instance of <code>CustomTextWatcher</code>.
     *
     * @param editText  the editText to edit text.
     * @param maxInput 最大输入字节数
     */
    public HexTextAutoAddEmptyCharInputWatcher(@NonNull EditText editText, int maxInput) {
        super(editText, maxInput, " ");
    }
}
