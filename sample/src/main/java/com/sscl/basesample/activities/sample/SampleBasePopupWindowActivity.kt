package com.sscl.basesample.activities.sample

import com.sscl.baselibrary.utils.DebugUtil.warnOut
import com.sscl.basesample.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sscl.basesample.popupwindow.SamplePopupWindow
import android.graphics.Point
import android.view.View
import android.widget.*

/**
 * @author pengh
 */
class SampleBasePopupWindowActivity : AppCompatActivity() {
    private lateinit var content: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.com_sscl_basesample_activity_sample_base_popup_window)
        content = findViewById(R.id.content_sample_popupWindow)
        val button = findViewById<Button>(R.id.show_popup_window_btn)
        button.setOnClickListener { v: View? -> showPopupWindow() }
    }

    private fun showPopupWindow() {
        warnOut(TAG, "弹出popupWindow")
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        warnOut(TAG, "point.x = " + point.x + ",point.y = " + point.y)
        val samplePopupWindow = SamplePopupWindow(this@SampleBasePopupWindowActivity)
        samplePopupWindow.width = point.x * 7 / 8
        samplePopupWindow.height = point.y * 7 / 8
        samplePopupWindow.isFocusable = true
        samplePopupWindow.isOutsideTouchable = true
        samplePopupWindow.show(content)
    }

    companion object {
        private const val TAG = "SampleBasePopupWindowAc"
    }
}