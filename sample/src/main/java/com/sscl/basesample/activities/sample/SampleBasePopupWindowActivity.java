package com.sscl.basesample.activities.sample;

import android.graphics.Point;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.sscl.baselibrary.utils.DebugUtil;
import com.sscl.basesample.R;
import com.sscl.basesample.popupwindow.SamplePopupWindow;


/**
 * @author pengh
 */
public class SampleBasePopupWindowActivity extends AppCompatActivity {

    private static final String TAG = "SampleBasePopupWindowAc";
    private RelativeLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_sscl_basesample_activity_sample_base_popup_window);
        content = findViewById(R.id.content_sample_popupWindow);
        Button button = findViewById(R.id.show_popup_window_btn);
        button.setOnClickListener(v -> showPopupWindow());
    }

    private void showPopupWindow() {
        DebugUtil.warnOut(TAG, "弹出popupWindow");
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        DebugUtil.warnOut(TAG, "point.x = " + point.x + ",point.y = " + point.y);
        SamplePopupWindow samplePopupWindow = new SamplePopupWindow(SampleBasePopupWindowActivity.this);
        samplePopupWindow.setWidth(point.x * 7 / 8);
        samplePopupWindow.setHeight(point.y * 7 / 8);
        samplePopupWindow.setFocusable(true);
        samplePopupWindow.setOutsideTouchable(true);
        samplePopupWindow.show(content);
    }
}
