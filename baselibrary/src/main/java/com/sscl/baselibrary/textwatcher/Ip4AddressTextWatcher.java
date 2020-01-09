package com.sscl.baselibrary.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 只能输入IP地址
 *
 * @author jackie
 */
public class Ip4AddressTextWatcher implements TextWatcher {

    /*-----------------------------------------------静态常量-----------------------------------------------*/

    private static final String REG_EX = "[/:#*?<>|\"\n\ta-zA-Z]";

    /*-----------------------------------------------成员变量-----------------------------------------------*/

    /**
     * 要监听的文本输入框
     */
    private EditText editText;

    private int changedIndex;

    /*-----------------------------------------------构造方法-----------------------------------------------*/

    /**
     * 构造方法
     *
     * @param editText 文本输入框
     */
    public Ip4AddressTextWatcher(@NonNull EditText editText) {
        this.editText = editText;
    }

    /*-----------------------------------------------重写父类方法-----------------------------------------------*/

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        Editable text = editText.getText();
        String editable;
        if (text == null) {
            editable = "";
        } else {
            editable = text.toString();
        }

        String str = stringFilter(editable);
        if (!editable.equals(str)) {
            editText.setText(str);
            editText.setSelection(++changedIndex);
        }
    }


    private String stringFilter(String str) throws PatternSyntaxException {
        String tmp;
        Pattern p = Pattern.compile(REG_EX);
        Matcher m = p.matcher(str);
        if (m.find()) {
            tmp = m.replaceAll(".");
            for (int i = 0; i < tmp.length(); i++) {
                if (tmp.charAt(i) != str.charAt(i)) {
                    changedIndex = i;
                    break;
                }
            }
        } else {
            return str;
        }
        return tmp;

    }
}
