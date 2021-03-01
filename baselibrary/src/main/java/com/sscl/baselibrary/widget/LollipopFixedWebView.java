package com.sscl.baselibrary.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

public class LollipopFixedWebView extends WebView {
    /**
     * Construct a new WebView with a Context object.
     *
     * @param context A Context object used to access application assets.
     */
    public LollipopFixedWebView(Context context) {
        super(getFixedContext(context));
    }

    /**
     * Construct a new WebView with layout parameters.
     *
     * @param context A Context object used to access application assets.
     * @param attrs   An AttributeSet passed to our parent.
     */
    public LollipopFixedWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
    }

    /**
     * Construct a new WebView with layout parameters and a default style.
     *
     * @param context      A Context object used to access application assets.
     * @param attrs        An AttributeSet passed to our parent.
     * @param defStyleAttr
     */
    public LollipopFixedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
    }

    public static Context getFixedContext(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return context.createConfigurationContext(new Configuration());
        }
        return context;
    }
}
