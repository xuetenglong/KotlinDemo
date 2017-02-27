package com.cardvlaue.sys.creditreport;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.CountAmountActivity;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.webshow.WebShowActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>征信报告验证的页面<p/>
 */
public class CreditReportActivity extends BaseActivity implements ICreditReportView {

    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private EditText mAccountView;
    private EditText mPwdView;
    private EditText mImgCodeView;
    private SimpleDraweeView mImgView;
    private EditText mIdView;
    private ICreditReportsPresenter creditReportsPresenter;
    private String sessionId;
    private IFinancingRest rest;//获取最新申请
    private ContentLoadingDialog mLoadingDialog;
    private String appId, objectId, token;
    private TasksRepository repository;
    private Handler timeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_report);
        initWindow();
        timeHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 11033) {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismissAllowingStateLoss();
                    }
                    BusIndustrySelect select = new BusIndustrySelect(
                        CountAmountActivity.BUS_COUNTAMOUNT_CODE);
                    select.setTypeId("isCredit");
                    RxBus.getDefaultBus().send(select);
                    finish();

                }
            }
        };
    }

    private void initWindow() {
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mAccountView = (EditText) findViewById(R.id.et_credit_reports_account);
        mPwdView = (EditText) findViewById(R.id.et_credit_reports_pwd);
        mImgCodeView = (EditText) findViewById(R.id.et_credit_reports_img_code);
        mImgView = (SimpleDraweeView) findViewById(R.id.sdv_credit_reports_img);
        mIdView = (EditText) findViewById(R.id.et_credit_reports_id_code);
        setSupportActionBar(mToolbarView);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.credit_reports_name));
        creditReportsPresenter = new CreditReportsPresenterImpl(this, this);
        creditReportsPresenter.getVerificationCode();
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        appId = repository.getUserInfo().applicationId;

        //返回键
        mBackView.setOnClickListener(v -> finish());

        /**
         * 查看征信报告协议
         */
        findViewById(R.id.tv_credit_reports_agreement).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(WebShowActivity.EXTRA_TITLE,
                getString(R.string.credit_reports_credit_agreement_web));
            intent.putExtra(WebShowActivity.EXTRA_URL, UrlConstants.CREDIT_AGREEMENT);
            intent.setClass(CreditReportActivity.this, WebShowActivity.class);
            startActivity(intent);
        });
        /**
         * 查看如何授权
         */
        findViewById(R.id.tv_credit_reports_how).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(WebShowActivity.EXTRA_TITLE, "征信授权");
            intent.putExtra(WebShowActivity.EXTRA_URL, UrlConstants.CREDIT_HOW_AUTH);
            intent.setClass(CreditReportActivity.this, WebShowActivity.class);
            startActivity(intent);
        });
        /**
         * 刷新图片验证码
         */
        findViewById(R.id.sdv_credit_reports_img).setOnClickListener(v -> {
            sessionId = null;
            creditReportsPresenter.getVerificationCode();
        });

        /**
         * 提交验证
         */
        findViewById(R.id.btn_credit_reports_commit).setOnClickListener(v -> {
            String accountStr = mAccountView.getText().toString();
            String pwdStr = mPwdView.getText().toString();
            String codeStr = mImgCodeView.getText().toString();
            String idStr = mIdView.getText().toString();

            if (TextUtils.isEmpty(accountStr)) {
                ToastUtil.showFailure(CreditReportActivity.this, "登录名不能为空");
                return;
            }
            if (TextUtils.isEmpty(pwdStr)) {
                ToastUtil.showFailure(CreditReportActivity.this, "密码不能为空");
                return;
            }
            if (TextUtils.isEmpty(codeStr)) {
                ToastUtil.showFailure(CreditReportActivity.this, "图片验证不能为空");
                return;
            }
            if (TextUtils.isEmpty(idStr)) {
                ToastUtil.showFailure(CreditReportActivity.this, "身份校验不能为空");
                return;
            }
            if (TextUtils.isEmpty(sessionId)) {
                ToastUtil.showFailure(CreditReportActivity.this, "sessionId不能为空"); //失败
                return;
            }

            JSONObject parameter = new JSONObject();
            parameter.put("username", accountStr);
            parameter.put("password", pwdStr);
            parameter.put("verityCode", codeStr);
            parameter.put("tradeCode", idStr);
            parameter.put("sessionId", sessionId);

            JSONObject parameternNew = new JSONObject();
            parameternNew.put("p",
                "cvbaoli-" + XXTEA2.encryptToBase64String(parameter.toJSONString(), "083a8248815"));
            mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show(getSupportFragmentManager(), "credit_tag");
            creditReportsPresenter.creditReport(parameternNew);
        });
    }

    @Override
    public void toast(String msg) {
        ToastUtil.showSuccess(this, msg);
    }

    @Override
    public void imgCodeLoadSuccess(String url, String id) {
        if (!TextUtils.isEmpty(id)) {
            sessionId = id;
        }
        Timber.e("imgCodeLoadSuccess:%s", url);
        mImgView.setImageURI(Uri.parse(url));
    }

    @Override
    public void creditReportSuccess() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        queryApply();
        //queryApply();和发送消息应该都是可以的
    }

    @Override
    public void creditFailure() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
        }
    }

    public void queryApply() {
        rest.queryApply(appId, objectId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                Timber.e("征信时候返回" + JSON.toJSONString(queryApplyBO));
                if (TextUtils.isEmpty(queryApplyBO.getError())) {
                    timeHandler.sendEmptyMessageDelayed(11033, 2000);
                } else {
                    ToastUtil.showFailure(CreditReportActivity.this, queryApplyBO.getError());
                }
            }, throwable -> Timber.e("征信时候返回e" + JSON.toJSONString(throwable)));
    }
}