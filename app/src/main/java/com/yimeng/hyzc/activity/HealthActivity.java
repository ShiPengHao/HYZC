package com.yimeng.hyzc.activity;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.yimeng.hyzc.R;


/**
 * 健康教育activity
 */
public class HealthActivity extends BaseActivity {

    private static final String WEB_VIEW_URL = "http://m.hyzczg.com/";
    //    private static final String WEB_VIEW_URL = "http://www.baidu.com/";
    private WebView mWebView;
    private LinearLayout ll_loading;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_health;
    }

    @Override
    protected void initView() {
        mWebView = (WebView) findViewById(R.id.webView);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
    }

    @Override
    protected void setListener() {
        initWebSetting();
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                System.out.println(url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                ll_loading.setVisibility(View.VISIBLE);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                ll_loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void initData() {
        mWebView.loadUrl(WEB_VIEW_URL);
    }

    /**
     * 对webview进行设置
     */
    private void initWebSetting() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);

        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, new Paint());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        try {
            mWebView.getClass().getMethod("onResume").invoke(mWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            mWebView.getClass().getMethod("onPause").invoke(mWebView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ((ViewGroup) getWindow().getDecorView()).removeAllViews();
        super.onDestroy();
    }
}
