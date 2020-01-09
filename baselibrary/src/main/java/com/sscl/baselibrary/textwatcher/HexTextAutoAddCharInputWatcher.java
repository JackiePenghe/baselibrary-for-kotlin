package com.sscl.baselibrary.textwatcher;


import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.sscl.baselibrary.utils.DebugUtil;


/**
 * 自定义EditText输入过滤（限制只能输入十六进制字符，并且自动填充空格分割）
 *
 * @author alm
 */
public class HexTextAutoAddCharInputWatcher implements TextWatcher {

    /*--------------------------静态常量--------------------------*/

    private static final String TAG = HexTextAutoAddCharInputWatcher.class.getSimpleName();

    /**
     * 字符0
     */
    private static final char CHAR_0 = '0';
    /**
     * 字符9
     */
    private static final char CHAR_9 = '9';
    /**
     * 字符a
     */
    private static final char CHAR_A_LOWER = 'a';
    /**
     * 字符f
     */
    private static final char CHAR_F_LOWER = 'f';
    /**
     * 字符A
     */
    private static final char CHAR_A = 'A';
    /**
     * 字符F
     */
    private static final char CHAR_F = 'F';

    /*--------------------------成员变量--------------------------*/

    /**
     * 是否需要进行格式化
     */
    private boolean mFormat;

    private int maxInput;

    /**
     * 输入的字符是否有效
     */
    private boolean mInvalid;

    /**
     * 用于记录输入框中光标的位置
     */
    private int mSelection;

    /**
     * 上一次输入框中的文本内容
     */
    private String mLastText;

    /**
     * The editText to edit text.
     */
    private EditText mEditText;

    /**
     * the characters
     */
    private String character;

    private int byteSize = 3;

    /*--------------------------构造方法--------------------------*/

    /**
     * Creates an instance of <code>CustomTextWatcher</code>.
     *
     * @param editText the editText to edit text.
     */
    @SuppressWarnings("WeakerAccess")
    public HexTextAutoAddCharInputWatcher(@NonNull EditText editText, int maxInput, @NonNull @Size(1) String character) {

        super();
        mFormat = false;
        mInvalid = false;
        mLastText = "";
        this.mEditText = editText;
        this.mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        this.maxInput = maxInput;
        this.character = character;
    }

    /*--------------------------实现父类方法--------------------------*/

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start,
                                  int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        try {
            String temp = charSequence.toString();
            if (maxInput > 0) {
                if (temp.length() > (maxInput * byteSize - 1)) {
                    mEditText.setText(mLastText);
                    return;
                }
            }
            // Set selection.
            if (mLastText.equals(temp)) {
                if (mInvalid) {
                    mSelection -= 1;
                } else {
                    if ((mSelection >= 1) && (temp.length() > mSelection - 1)
                            && (temp.charAt(mSelection - 1)) == ' ') {
                        mSelection += 1;
                    }
                }
                int length = mLastText.length();
                if (mSelection > length) {
                    mEditText.setSelection(length);
                } else {
                    mEditText.setSelection(mSelection);
                }
                mFormat = false;
                mInvalid = false;
                return;
            }

            mFormat = true;
            mSelection = start;

            // Delete operation.
            if (count == 0) {
                if ((mSelection >= 1) && (temp.length() > mSelection - 1)
                        && (temp.charAt(mSelection - 1)) == ' ') {
                    mSelection -= 1;
                }

                return;
            }

            // Input operation.
            mSelection += count;
            char[] lastChar = (temp.substring(start, start + count))
                    .toCharArray();
            int mid = lastChar[0];
            //noinspection StatementWithEmptyBody
            if (mid >= CHAR_0 && mid <= CHAR_9) {
//                 1-9. do nothing
            } else //noinspection StatementWithEmptyBody
                if (mid >= CHAR_A && mid <= CHAR_F) {
//                 A-F. do nothing
                } else if (mid >= CHAR_A_LOWER && mid <= CHAR_F_LOWER) {
//                 把 a-f转为A-F.
                    for (int i = 0; i < lastChar.length; i++) {
                        char c = lastChar[i];
                        if (c >= CHAR_A_LOWER && c <= CHAR_F_LOWER) {
                            lastChar[i] = (char) (lastChar[i] - 32);
                        }
                    }

                    temp = temp.substring(0, start) + new String(lastChar);
                    mEditText.setText(temp);
                } else {
                    /* Invalid input. */
                    mInvalid = true;
                    temp = temp.substring(0, start)
                            + temp.substring(start + count);
                    mEditText.setText(temp);
                }

        } catch (Exception e) {
            DebugUtil.warnOut(TAG, e.getMessage());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

        try {
            /* Format input. */
            if (mFormat) {
                StringBuilder text = new StringBuilder();
                text.append(editable.toString().replace(String.valueOf(character), ""));
                int length = text.length();
                int sum = (length % 2 == 0) ? (length / 2) - 1 : (length / 2);
                for (int offset = 2, index = 0; index < sum; offset += byteSize, index++) {
                    text.insert(offset, character);
                }
                mLastText = text.toString();
                mEditText.setText(text);
            }
        } catch (Exception e) {
            DebugUtil.warnOut(TAG, "line 197: " + e.getMessage());
        }
    }

}
