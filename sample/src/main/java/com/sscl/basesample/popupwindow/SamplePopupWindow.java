package com.sscl.basesample.popupwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sscl.baselibrary.popupwindow.BasePopupWindow;
import com.sscl.basesample.R;

/**
 *
 * @author pengh
 * @date 2017/11/7
 * PopupWindow
 */

public class SamplePopupWindow extends BasePopupWindow {

    private Button button;
    private TextView textView;

    /**
     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
     *
     * <p>The popup does provide a background.</p>
     *
     * @param context 上下文
     */
    public SamplePopupWindow(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayout() {
        return R.layout.com_sscl_basesample_layout_sample_popup_window;
    }

    @Override
    protected void doBeforeInitOthers() {
    }

    @Override
    protected void initViews() {
        button = findViewById(R.id.cancel_btn);
        textView = findViewById(R.id.popup_window_sample_tv);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViewData() {
        textView.setText("sample popup window");
    }

    @Override
    protected void initOtherData() {

    }

    @Override
    protected void initEvents() {
        button.setOnClickListener(v -> dismiss());
    }

    @Override
    protected void doAfterAll() {
    }

}
