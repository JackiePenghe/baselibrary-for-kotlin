package com.sscl.basesample.activities.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.basesample.R;

import java.net.URL;

public class SampleWebViewActivityActivity extends AppCompatActivity {

    private String url = "https://baidu.com";

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    }
                    break;
                case R.id.forward:
                    if (webView.canGoForward()) {
                        webView.goForward();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Button backBtn;
    private Button forwwardBtn;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_web_view_activity);

        backBtn = findViewById(R.id.back);
        forwwardBtn = findViewById(R.id.forward);
        webView = findViewById(R.id.web_view);
        backBtn.setOnClickListener(onClickListener);
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
                    ToastUtil.toastLong(SampleWebViewActivityActivity.this, "加载完成");
                } else {
                    ToastUtil.toastLong(SampleWebViewActivityActivity.this, "正在加载，请等待");
                }
            }
        };
        webView.setWebChromeClient(webChromeClient);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //没有网络时优先使用缓存
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //不使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

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