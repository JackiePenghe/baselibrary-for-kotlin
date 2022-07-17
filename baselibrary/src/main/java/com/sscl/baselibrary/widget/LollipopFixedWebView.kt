package com.sscl.baselibrary.widget

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebView


class LollipopFixedWebView : WebView {
    /**
     * Construct a new WebView with a Context object.
     *
     * @param context A Context object used to access application assets.
     */
    constructor(context: Context) : super(getFixedContext(context)) {}

    /**
     * Construct a new WebView with layout parameters.
     *
     * @param context A Context object used to access application assets.
     * @param attrs   An AttributeSet passed to our parent.
     */
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(getFixedContext(context), attrs) {
    }

    /**
     * Construct a new WebView with layout parameters and a default style.
     *
     * @param context      A Context object used to access application assets.
     * @param attrs        An AttributeSet passed to our parent.
     * @param defStyleAttr
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        getFixedContext(context),
        attrs,
        defStyleAttr
    ) {
    }

    companion object {
        fun getFixedContext(context: Context): Context {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return context.createConfigurationContext(Configuration())
            }
            return context
        }
    }
}