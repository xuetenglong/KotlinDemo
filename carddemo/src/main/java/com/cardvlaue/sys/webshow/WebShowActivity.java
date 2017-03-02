package com.cardvlaue.sys.webshow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.R;
import timber.log.Timber;

/**
 * 网页加载
 */
public class WebShowActivity extends BaseActivity {

    public static final String EXTRA_TITLE = "WebShowActivity_TITLE";
    public static final String EXTRA_URL = "WebShowActivity_URL";
    public static final String EXTRA_COLOR = "WebShowActivity_COLOR";

    @BindView(R.id.pb_web_show_loading)
    ProgressBar mProgressBarView;

    @BindView(R.id.wv_web_client)
    WebView mWebView;

    /**
     * 标题文字
     */
    @BindView(R.id.tv_white_back)
    TextView mTitleTextView;

    /**
     * 标题背景
     */
    @BindView(R.id.rl_white_content)
    RelativeLayout mTitleBackView;

    @BindView(R.id.ibtn_white_back)
    ImageButton mBackView;

    @BindView(R.id.ll_web_show_root)
    LinearLayout mRootView;

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        finish();
    }

    /**
     * 加载结束
     */
    private void loadOver() {
        if (mWebView.isShown()) {
            mWebView.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_show);
        ButterKnife.bind(this);

        /**
         * 应用蓝
         */
        if ("1001".equals(getIntent().getStringExtra(EXTRA_COLOR))) {
            mTitleBackView.setBackgroundColor(ContextCompat.getColor(this, R.color.app_blue));
            mBackView.setImageResource(R.mipmap.icon_back);
            mRootView.setBackgroundColor(ContextCompat.getColor(this, R.color.app_blue));
            mTitleTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        } else {
            mTitleTextView.setText("--");
        }

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        String webCache = getCacheDir().getPath();
        Timber.e("Cache:%s", webCache);
        settings.setAppCachePath(webCache + "/web");
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBarView.setVisibility(View.GONE);
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                loadOver();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                WebResourceError error) {
                super.onReceivedError(view, request, error);
                loadOver();
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBarView.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBarView.setVisibility(View.GONE);
                }

                super.onProgressChanged(view, newProgress);
            }
        });
        Timber.i("URL:%s", getIntent().getStringExtra(EXTRA_URL));
        mWebView.loadUrl(getIntent().getStringExtra(EXTRA_URL));
    }

}
