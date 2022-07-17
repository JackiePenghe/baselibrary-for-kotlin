package com.sscl.baselibrary.textwatcher

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

import android.widget.EditText
import kotlin.Throws
import android.text.TextWatcher
import android.text.Editable

/**
 * 只能输入IP地址
 *
 * @author jackie
 */
class Ip4AddressTextWatcher
/**
 * 构造方法
 *
 * @param editText 文本输入框
 */ constructor(
    /**
     * 要监听的文本输入框
     */
    private val editText: EditText
) : TextWatcher {
    /*-----------------------------------------------成员变量-----------------------------------------------*/
    private var changedIndex: Int = 0

    /*-----------------------------------------------重写父类方法-----------------------------------------------*/
    public override fun onTextChanged(
        s: CharSequence, start: Int, before: Int,
        count: Int
    ) {
    }

    public override fun beforeTextChanged(
        s: CharSequence, start: Int, count: Int,
        after: Int
    ) {
    }

    public override fun afterTextChanged(s: Editable) {
        val text: Editable? = editText.getText()
        val editable: String
        if (text == null) {
            editable = ""
        } else {
            editable = text.toString()
        }
        val str: String = stringFilter(editable)
        if (!(editable == str)) {
            editText.setText(str)
            editText.setSelection(++changedIndex)
        }
    }

    @Throws(PatternSyntaxException::class)
    private fun stringFilter(str: String): String {
        val tmp: String
        val p: Pattern = Pattern.compile(REG_EX)
        val m: Matcher = p.matcher(str)
        if (m.find()) {
            tmp = m.replaceAll(".")
            for (i in 0 until tmp.length) {
                if (tmp.get(i) != str.get(i)) {
                    changedIndex = i
                    break
                }
            }
        } else {
            return str
        }
        return tmp
    }

    companion object {
        /*-----------------------------------------------静态常量-----------------------------------------------*/
        private val REG_EX: String = "[/:#*?<>|\"\n\ta-zA-Z]"
    }
    /*-----------------------------------------------构造方法-----------------------------------------------*/
}