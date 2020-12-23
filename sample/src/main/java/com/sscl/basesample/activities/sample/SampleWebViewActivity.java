package com.sscl.basesample.activities.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.basesample.R;

public class SampleWebViewActivity extends AppCompatActivity {

    //    private String url = "https://baidu.com";
    private final String url = "http://main.wadd.vip/wdf";

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    if (webView == null) {
                        break;
                    }
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        ToastUtil.toastLong(SampleWebViewActivity.this, "已是最后一个网页");
                    }
                    break;
                case R.id.forward:
                    if (webView == null) {
                        break;
                    }
                    if (webView.canGoForward()) {
                        webView.goForward();
                    } else {
                        ToastUtil.toastLong(SampleWebViewActivity.this, "已是最前一个网页");
                    }
                    break;
                case R.id.refresh:
                    if (webView == null) {
                        break;
                    }
                    webView.reload();
                    break;
                case R.id.stop:
                    if (webView == null) {
                        break;
                    }
                    webView.stopLoading();
                    break;
                default:
                    break;
            }
        }
    };
    private Button backBtn;
    private Button forwwardBtn;
    private Button refreshBtn;
    private Button stopBtn;
    private EditText urlEt;
    private WebView webView;
    private final TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (webView == null) {
                    return false;
                }
                String s = urlEt.getText().toString();
                if (!s.startsWith("http://") && !s.startsWith("https://")) {
                    s = "http://" + s;
                }
                webView.loadUrl(s);
                return true;
            }
            return false;
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_web_view_activity);

        backBtn = findViewById(R.id.back);
        forwwardBtn = findViewById(R.id.forward);
        refreshBtn = findViewById(R.id.refresh);
        stopBtn = findViewById(R.id.stop);
        webView = findViewById(R.id.web_view);
        urlEt = findViewById(R.id.url);
        urlEt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        urlEt.setOnEditorActionListener(onEditorActionListener);

        backBtn.setOnClickListener(onClickListener);
        forwwardBtn.setOnClickListener(onClickListener);
        refreshBtn.setOnClickListener(onClickListener);
        stopBtn.setOnClickListener(onClickListener);
        WebViewClient webViewClient = new WebViewClient();
        webView.setWebViewClient(webViewClient);
        WebChromeClient webChromeClient = new WebChromeClient() {
            /**
             * Tell the host application the current progress of loading a page.
             *
             * @param view        The WebView that initiated the callback.
             * @param newProgress Current page loading progress, represented by
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    ToastUtil.toastLong(SampleWebViewActivity.this, "加载完成");
                    urlEt.setText(webView.getUrl());
                } else {
                    ToastUtil.toastLong(SampleWebViewActivity.this, "正在加载，请等待");
                }
            }
        };
        webView.setWebChromeClient(webChromeClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //没有网络时优先使用缓存
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //不使用缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 允许访问文件
        webSettings.setAllowFileAccess(true);
        // 设置显示缩放按钮
        webSettings.setBuiltInZoomControls(true);
        // 支持缩放
        webSettings.setSupportZoom(true);
        // 这个很关键
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //设置为电脑版网页
//        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
        webView.loadUrl(url);
        webView.requestFocus();

    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}