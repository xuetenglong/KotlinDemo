package com.cardvlaue.sys.creditreport;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
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

/**
 * 征信报告引导页
 */
public class CreditReportGuideActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_report_guide);
        findViewById(R.id.btn_credit_reports_guide_commit).setOnClickListener(v -> {
            startActivity(new Intent(CreditReportGuideActivity.this, CreditReportActivity.class));
            CreditReportGuideActivity.this.finish();
        });
        ButterKnife.bind(this);

        /**
         * 应用蓝
         */
        if ("1001".equals(getIntent().getStringExtra(EXTRA_COLOR))) {
            mTitleBackView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            mBackView.setImageResource(R.mipmap.icon_back_black);
            mRootView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }

        mTitleTextView.setText(getIntent().getStringExtra(EXTRA_TITLE));

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBarView.setVisibility(View.GONE);
            }

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

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
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
        mWebView.loadUrl(getIntent().getStringExtra(EXTRA_URL));
    }

}
