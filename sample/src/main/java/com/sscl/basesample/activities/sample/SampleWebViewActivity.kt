package com.sscl.basesample.activities.sample

import com.sscl.basesample.R
import com.sscl.baselibrary.utils.ToastUtil
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView
import android.widget.TextView.OnEditorActionListener
import android.view.inputmethod.EditorInfo
import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.view.KeyEvent
import android.view.View
import android.widget.*

class SampleWebViewActivity : AppCompatActivity() {
    //    private String url = "https://baidu.com";
    private lateinit var backBtn: Button
    private lateinit var forwwardBtn: Button
    private lateinit var refreshBtn: Button
    private lateinit var stopBtn: Button
    private lateinit var urlEt: EditText
    private lateinit var webView: WebView
    private val url = "http://main.wadd.vip/wdf"
    private val onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.back -> {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    ToastUtil.toastLong(this@SampleWebViewActivity, "已是最后一个网页")
                }
            }
            R.id.forward -> {
                if (webView.canGoForward()) {
                    webView.goForward()
                } else {
                    ToastUtil.toastLong(this@SampleWebViewActivity, "已是最前一个网页")
                }
            }
            R.id.refresh -> {
                webView.reload()
            }
            R.id.stop -> {
                webView.stopLoading()
            }
            else -> {}
        }
    }
    private val onEditorActionListener = OnEditorActionListener { v, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER) {
            var s = urlEt.text.toString()
            if (!s.startsWith("http://") && !s.startsWith("https://")) {
                s = "http://$s"
            }
            webView.loadUrl(s)
            return@OnEditorActionListener true
        }
        false
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.com_sscl_basesample_activity_sample_web_view_activity)
        backBtn = findViewById(R.id.back)
        forwwardBtn = findViewById(R.id.forward)
        refreshBtn = findViewById(R.id.refresh)
        stopBtn = findViewById(R.id.stop)
        webView = findViewById(R.id.web_view)
        urlEt = findViewById(R.id.url)
        urlEt.imeOptions = EditorInfo.IME_ACTION_DONE
        urlEt.setOnEditorActionListener(onEditorActionListener)
        backBtn.setOnClickListener(onClickListener)
        forwwardBtn.setOnClickListener(onClickListener)
        refreshBtn.setOnClickListener(onClickListener)
        stopBtn.setOnClickListener(onClickListener)
        val webViewClient = WebViewClient()
        webView.webViewClient = webViewClient
        val webChromeClient: WebChromeClient = object : WebChromeClient() {
            /**
             * Tell the host application the current progress of loading a page.
             *
             * @param view        The WebView that initiated the callback.
             * @param newProgress Current page loading progress, represented by
             */
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    ToastUtil.toastLong(this@SampleWebViewActivity, "加载完成")
                    urlEt.setText(webView.getUrl())
                } else {
                    ToastUtil.toastLong(this@SampleWebViewActivity, "正在加载，请等待")
                }
            }
        }
        webView.webChromeClient = webChromeClient
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        //没有网络时优先使用缓存
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //不使用缓存
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        // 允许访问文件
        webSettings.allowFileAccess = true
        // 设置显示缩放按钮
        webSettings.builtInZoomControls = true
        // 支持缩放
        webSettings.setSupportZoom(true)
        // 这个很关键
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        //设置为电脑版网页
//        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
        webView.loadUrl(url)
        webView.requestFocus()
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The [OnBackPressedDispatcher][.getOnBackPressedDispatcher] will be given a
     * chance to handle the back button before the default behavior of
     * [Activity.onBackPressed] is invoked.
     *
     * @see .getOnBackPressedDispatcher
     */
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}