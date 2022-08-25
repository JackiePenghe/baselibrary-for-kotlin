package com.sscl.baselibrary.textwatcher

import android.text.InputType
import androidx.annotation.*
import java.lang.Exception
import java.lang.StringBuilder

import com.sscl.baselibrary.utils.DebugUtil
import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable

/**
 * 自定义EditText输入过滤（限制只能输入十六进制字符，并且自动填充空格分割）
 *
 * @author alm
 */
open class HexTextAutoAddCharInputWatcher constructor(
    /**
     * The editText to edit text.
     */
    private val mEditText: EditText, maxInput: Int, @Size(1) character: Char
) : TextWatcher {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 静态声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    companion object {

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         * 属性声明
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        /* * * * * * * * * * * * * * * * * * *  * * * * * * * * * * * * * * * * * * */

        private val TAG: String = HexTextAutoAddCharInputWatcher::class.java.simpleName

        /**
         * 字符0
         */
        private const val CHAR_0: Char = '0'

        /**
         * 字符9
         */
        private const val CHAR_9: Char = '9'

        /**
         * 字符a
         */
        private const val CHAR_A_LOWER: Char = 'a'

        /**
         * 字符f
         */
        private const val CHAR_F_LOWER: Char = 'f'

        /**
         * 字符A
         */
        private const val CHAR_A: Char = 'A'

        /**
         * 字符F
         */
        private const val CHAR_F: Char = 'F'
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 属性声明
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /* * * * * * * * * * * * * * * * * * * 常量属性 * * * * * * * * * * * * * * * * * * */

    private val maxInput: Int

    /**
     * the characters
     */
    private val character: Char

    /**
     * 每一个代表字节的字符串的长度 即每一个字节的长度，固定值为3，包含最后的一个空格
     */
    private val byteSize: Int = 3

    /* * * * * * * * * * * * * * * * * * * 可变属性 * * * * * * * * * * * * * * * * * * */

    /**
     * 是否需要进行格式化
     */
    private var mFormat: Boolean = false

    /**
     * 输入的字符是否有效
     */
    private var mInvalid: Boolean = false

    /**
     * 用于记录输入框中光标的位置
     */
    private var mSelection: Int = 0

    /**
     * 上一次输入框中的文本内容
     */
    private var mLastText: String = ""

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 构造方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    init {
        mEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        this.maxInput = maxInput
        this.character = character
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * 实现方法
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun beforeTextChanged(
        charSequence: CharSequence, start: Int,
        count: Int, after: Int
    ) {
    }

    override fun onTextChanged(
        charSequence: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        try {
            var temp: String = charSequence.toString()
            if (maxInput > 0) {
                if (temp.length > (maxInput * byteSize - 1)) {
                    mEditText.setText(mLastText)
                    return
                }
            }
            // Set selection.
            if ((mLastText == temp)) {
                if (mInvalid) {
                    mSelection -= 1
                } else {
                    if (((mSelection >= 1) && (temp.length > mSelection - 1)
                                && ((temp[mSelection - 1]) == character))
                    ) {
                        mSelection += 1
                    }
                }
                val length: Int = mLastText.length
                if (mSelection > length) {
                    mEditText.setSelection(length)
                } else {
                    mEditText.setSelection(mSelection)
                }
                mFormat = false
                mInvalid = false
                return
            }
            mFormat = true
            mSelection = start

            // Delete operation.
            if (count == 0) {
                if (((mSelection >= 1) && (temp.length > mSelection - 1)
                            && ((temp[mSelection - 1]) == character))
                ) {
                    mSelection -= 1
                }
                return
            }

            // Input operation.
            mSelection += count
            val lastChar: CharArray = (temp.substring(start, start + count))
                .toCharArray()
            val mid: Int = lastChar[0].code
            if (mid >= CHAR_0.code && mid <= CHAR_9.code) {
//                 1-9. do nothing
            } else if (mid >= CHAR_A.code && mid <= CHAR_F.code) {
//                 A-F. do nothing
            } else if (mid >= CHAR_A_LOWER.code && mid <= CHAR_F_LOWER.code) {
//                 把 a-f转为A-F.
                for (i in lastChar.indices) {
                    val c: Char = lastChar[i]
                    if (c in CHAR_A_LOWER..CHAR_F_LOWER) {
                        lastChar[i] = (lastChar[i] - 32)
                    }
                }
                temp = temp.substring(0, start) + String(lastChar)
                mEditText.setText(temp)
            } else {
                /* Invalid input. */
                mInvalid = true
                temp = (temp.substring(0, start)
                        + temp.substring(start + count))
                mEditText.setText(temp)
            }
        } catch (e: Exception) {
            e.message?.let { DebugUtil.warnOut(TAG, it) }
        }
    }

    override fun afterTextChanged(editable: Editable) {
        try {
            /* Format input. */
            if (mFormat) {
                val text = StringBuilder()
                text.append(editable.toString().replace(character.toString(), ""))
                val length: Int = text.length
                val sum: Int = if ((length % 2 == 0)) (length / 2) - 1 else (length / 2)
                var offset = 2
                var index = 0
                while (index < sum) {
                    text.insert(offset, character)
                    offset += byteSize
                    index++
                }
                mLastText = text.toString()
                mEditText.setText(text)
            }
        } catch (e: Exception) {
            DebugUtil.warnOut(
                TAG,
                "line 197: " + e.message
            )
        }
    }
}