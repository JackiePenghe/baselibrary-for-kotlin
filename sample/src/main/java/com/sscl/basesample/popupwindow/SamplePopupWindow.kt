package com.sscl.basesample.popupwindow

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.sscl.baselibrary.popupwindow.BasePopupWindow
import com.sscl.basesample.R

/**
 *
 * @author pengh
 * @date 2017/11/7
 * PopupWindow
 */
class SamplePopupWindow
/**
 *
 * Create a new empty, non focusable popup window of dimension (0,0).
 *
 *
 * The popup does provide a background.
 *
 * @param context 上下文
 */
    (context: Context) : BasePopupWindow(context) {
    private lateinit var button: Button
    private lateinit var textView: TextView
   override fun setLayout(): Int {
        return R.layout.com_sscl_basesample_layout_sample_popup_window
    }

   override fun doBeforeInitOthers() {

   }

   override fun initViews() {
        button = findViewById(R.id.cancel_btn)
        textView = findViewById(R.id.popup_window_sample_tv)
    }

    @SuppressLint("SetTextI18n")
   override fun initViewData() {
        textView.text = "sample popup window"
    }

   override fun initOtherData() {}
   override fun initEvents() {
        button.setOnClickListener { dismiss() }
    }

   override fun doAfterAll() {}
}