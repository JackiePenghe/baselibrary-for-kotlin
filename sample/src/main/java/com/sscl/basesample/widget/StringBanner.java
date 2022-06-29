package com.sscl.basesample.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sscl.baselibrary.widget.banner.BaseBanner;

/**
 * @author pengh
 */
public class StringBanner extends BaseBanner<String> {

    public StringBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StringBanner(@NonNull Context context) {
        super(context);
    }

    public StringBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
