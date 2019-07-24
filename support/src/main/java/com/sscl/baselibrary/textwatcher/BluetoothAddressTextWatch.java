package com.sscl.baselibrary.textwatcher;


import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import com.sscl.baselibrary.utils.DebugUtil;


/**
 * 自定义EditText输入过滤（限制只能输入十六进制字符：蓝牙MAC地址）
 * <p>
 *
 * @author alm
 * @date 17-4-28
 */
public class BluetoothAddressTextWatch implements TextWatcher {

    private static final String TAG = "BluetoothAddressTextWatch";

    private boolean mFormat;

    private boolean mInvalid;

    private int mSelection;

    private String mLastText;

    /**
     * The editText to edit text.
     */
    private EditText mEditText;

    /**
     * Creates an instance of <code>CustomTextWatcher</code>.
     *
     * @param editText the editText to edit text.
     */
    public BluetoothAddressTextWatch(EditText editText) {

        super();
        mFormat = false;
        mInvalid = false;
        mLastText = "";
        this.mEditText = editText;
        this.mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start,
                                  int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        try {

            String temp = charSequence.toString();
            if (temp.length() > 17) {
                mEditText.setText(mLastText);
                return;
            }

            // Set selection.
            if (mLastText.equals(temp)) {
                if (mInvalid) {
                    mSelection -= 1;
                } else {
                    if ((mSelection >= 1) && (temp.length() > mSelection - 1)
                            && (temp.charAt(mSelection - 1)) == ':') {
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
                        && (temp.charAt(mSelection - 1)) == ':') {
                    mSelection -= 1;
                }

                return;
            }

            // Input operation.
            mSelection += count;
            char[] lastChar = (temp.substring(start, start + count))
                    .toCharArray();
            int mid = lastChar[0];
            char char0 = '0';
            char char9 = '9';
            char chara = 'a';
            char charf = 'f';
            char charA = 'A';
            char charF = 'F';
            if (mid >= char0 && mid <= char9) {
//                 1-9.
            } else if (mid >= charA && mid <= charF) {
//                 A-F.
            } else if (mid >= chara && mid <= charf) {
//                 把 a-f转为A-F.
                for (int i = 0; i < lastChar.length; i++) {
                    lastChar[i] = (char) (lastChar[i] - 32);
                }

                temp = temp.substring(0, start) + new String(lastChar);
                mEditText.setText(temp);
            } else {
                /* Invalid input. */
                mInvalid = true;
                temp = temp.substring(0, start)
                        + temp.substring(start + count, temp.length());
                mEditText.setText(temp);
            }

        } catch (Exception e) {
            DebugUtil.warnOut(TAG, "line 133: " + e.getMessage());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

        try {

            /* Format input. */
            if (mFormat) {
                StringBuilder text = new StringBuilder();
                text.append(editable.toString().replace(":", ""));
                int length = text.length();
                int sum = (length % 2 == 0) ? (length / 2) - 1 : (length / 2);
                for (int offset = 2, index = 0; index < sum; offset += 3, index++) {

                    text.insert(offset, ":");
                }
                mLastText = text.toString();
                mEditText.setText(text);
            }
        } catch (Exception e) {
            DebugUtil.warnOut(TAG, "line 156: " + e.getMessage());
        }
    }

}
