package com.cardvlaue.sys.cardverify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.tongdun.android.shell.FMAgent;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.confirm.ConfirmListActivity;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.login.LoginPresenter;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.uploadphoto.UploadPhotoActivity;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.RemoveAllSpace;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.webshow.WebShowActivity;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>验证银行卡  对私<p/>
 */
public class CardPrivateActivity extends BaseActivity implements CardPrivateView {

    // public static final String START_COUNTDOWN_TIME = "RegAndForgotActivity_START_COUNTDOWN_TIME";  // 开启倒计时
    public static final String TYPE_PRIVATE = "1";//对私
    public static final String TYPE_PUBLIC = "2";//对公
    public static final int CALLBACK_MSG_GETBANK_PRIVATE = 100000 + 6;    //对私的银行卡
    private static final long ALL_TIME = 60000;//倒计时总时间
    private static final long STEP_TIME = 1000;//间隔时间
    public static Handler handler;
    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private TextView mDirectDebitBankName;//开户行
    private EditText mDirectDebitAcctNo;//银行账号
    private EditText mDirectDebitAcctPhone;//预留手机号码
    private TextView mCode;//点击获得验证码
    private EditText mMobilePhoneVerifyCode;//输入手机验证码
    private String mdirectDebitAcctId;
    private String mdirectDebitAcctName;

    /*   private   String mdirectDebitAcctId;
     */
    private String mdirectDebitBankName;
    private String mdirectDebitBankCode;//银行卡的行号rest
    private String mdirectDebitAcctNo;
    private String mdirectDebitAcctPhone;
    private String mmobilePhoneVerifyCode;
    private String appId, objectId, token, phone;
    //获取短信验证码
    private CardPrivatePresenter regAndForgotPresenter;
    private CountDownTimer mTimer;
    private ILoginRest loginRest;//获取验证码
    private IFinancingRest rest;//更新申请
    private TasksRepository repository;
    private String verifyCode = "";   // 判断有没有获取验证码
    private Subscriber<Object> mCodeSubscriber;
    private ContentLoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_private);
        loginRest = HttpConfig.getClient().create(ILoginRest.class);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        initView();
        countdownTime();

        //点击提交
        findViewById(R.id.bank_submit).setOnClickListener(v -> {
            mdirectDebitBankName = mDirectDebitBankName.getText().toString();
            mdirectDebitAcctNo = mDirectDebitAcctNo.getText().toString();
            mdirectDebitAcctPhone = mDirectDebitAcctPhone.getText().toString();
            mmobilePhoneVerifyCode = mMobilePhoneVerifyCode.getText().toString();
            //非空判断  mdirectDebitBankName
            if (!CheckingForm
                .checkForBankPrivate(mdirectDebitBankCode, RemoveAllSpace.removeAllSpace(mdirectDebitAcctNo),
                    mdirectDebitAcctPhone,
                    mmobilePhoneVerifyCode)) {
                ToastUtil.showFailure(CardPrivateActivity.this, CheckingForm.LastError); //失败
                Timber.e("空的");
                return;
            } else {
                Timber.e("非空的");
                if (verifyCode.equals("")) {
                    ToastUtil.showFailure(CardPrivateActivity.this, "请先获取验证码"); //失败
                } else {
                    mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
                    mLoadingDialog.setCancelable(false);
                    mLoadingDialog.show(getSupportFragmentManager(), "tag");
                    card();
                }
            }
        });

    }

    /**
     * 初始化空间
     */
    public void initView() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.card_security));

        mDirectDebitBankName = (TextView) findViewById(R.id.tv_directDebitBankName);
        mDirectDebitAcctNo = (EditText) findViewById(R.id.tv_directDebitAcctNo);
        mDirectDebitAcctPhone = (EditText) findViewById(R.id.tv_directDebitAcctPhone);
        mCode = (TextView) findViewById(R.id.tv_code);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        appId = repository.getUserInfo().applicationId;

        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            appId = repository.getApplicationId();
        }
        queryApply2();
        mBackView.setOnClickListener(v -> finish());

        findViewById(R.id.tv_protocol).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(WebShowActivity.EXTRA_TITLE,
                getString(R.string.credit_reports_credit_how_why));
            intent.putExtra(WebShowActivity.EXTRA_URL, UrlConstants.BIND_BANK_CARD);
            intent.setClass(CardPrivateActivity.this, WebShowActivity.class);
            startActivity(intent);
        });

        //选择开户的银行
        mDirectDebitBankName.setOnClickListener(v -> {
            Intent intentBank = new Intent(CardPrivateActivity.this, ConfirmListActivity.class);
            if (mDirectDebitBankName.getText().toString().trim().toString().equals("")) {
                intentBank.putExtra("title", "");
                intentBank.putExtra("type", "3");
                intentBank.putExtra("tagName", "directDebitBankName");
                startActivity(intentBank);
                return;
            } else {
                intentBank.putExtra("title", mDirectDebitBankName.getText().toString());
                intentBank.putExtra("type", "3");
                intentBank.putExtra("tagName", "directDebitBankName");
                startActivity(intentBank);
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == CALLBACK_MSG_GETBANK_PRIVATE) {//选择银行卡
                    Bundle bundle = msg.getData();
                    mDirectDebitBankName.setText(bundle.get("title").toString().trim());
                    mdirectDebitBankCode = bundle.get("id").toString().trim();
                }
            }
        };
        /**
         * 获取图形验证码
         */
        mCode.setOnClickListener(v -> {
            Toast.makeText(CardPrivateActivity.this, "获取验证码", Toast.LENGTH_LONG).show();
            regAndForgotPresenter.getSmsCode(CardPrivateActivity.this,
                mDirectDebitAcctPhone.getText().toString(), CardPrivateActivity.TYPE_PRIVATE,
                getSupportFragmentManager());
        });
        mMobilePhoneVerifyCode = (EditText) findViewById(R.id.tv_mobilePhoneVerifyCode);

        mTimer = new CountDownTimer(ALL_TIME, STEP_TIME) {

            @Override
            public void onTick(long millisUntilFinished) {
                mCode.setEnabled(false);
                mCode.setText("验证码(" + millisUntilFinished / 1000 + ")");
            }

            @Override
            public void onFinish() {
                mCode.setEnabled(true);
                mCode.setText("获取验证码");
            }
        };
        regAndForgotPresenter = new CardPrivatePresenterImpl(this);
    }

    /**
     * 倒计时
     */
    private void countdownTime() {
        mCodeSubscriber = new Subscriber<Object>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object o) {
                if (CardPrivatePresenterImpl.BUS_CODE.equals(o)) {
                    verifyCode = "code";
                    mTimer.start();
                }
            }
        };

        RxBus.getDefaultBus().toObserverable().subscribe(mCodeSubscriber);
    }


    @Override
    public void fail(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    /**
     * 创建授权
     */
    public void card() {
        MerchantsByMobilePhoneBO bo = new MerchantsByMobilePhoneBO();
        bo.setIpAddress(repository.getIpAddress());//IP地址
        bo.setBlackBox(FMAgent.onEvent(this));
        createAuth(CardPrivateActivity.this, bo);


    }


    private void createAuth(final Context context, final MerchantsByMobilePhoneBO bo) {
        AuthorizationsBO auth = new AuthorizationsBO();
        auth.setType(6);
        auth.setMobilePhone(bo.getMobilePhone());
        auth.setTime(
            DateFormat.format(LoginPresenter.DATE_FORMAT, System.currentTimeMillis()).toString());
        auth.setDeviceNumber(DeviceUtil.getDeviceInfo(context));
        auth.setAgent(DeviceUtil.getUA(context));
        if (!TextUtils.isEmpty(repository.getIpAddress())) {
            auth.setIp(repository.getIpAddress());
        }
        if (!TextUtils.isEmpty(repository.getGpsAddress())) {
            auth.setGps(repository.getGpsAddress());
        }
        auth.setBlackBox(FMAgent.onEvent(this));
        loginRest.createAuth(auth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<AuthorizationsBO>() {
                @Override
                public void onCompleted() {
                    // isLoading = false;
                    Timber.e("银行卡对私");
                }

                @Override
                public void onError(Throwable e) {
                    // isLoading = false;
                    mLoadingDialog.dismiss();
                    Timber.e(e + "银行卡对私e" + e.getMessage());
                }

                @Override
                public void onNext(AuthorizationsBO authorizationsBO) {
                    Timber.e(authorizationsBO.getCreatedAt() + "银行卡对私授权CreatedAt" + authorizationsBO
                        .getObjectid());
                    if (authorizationsBO.getObjectid() != 0) {
                        updateApply();
                    }
                }
            });
    }


    /**
     * 更新申请
     */
    public void updateApply() {
        //mdirectDebitBankCode  directDebitBankCode
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        updateApplyBO.setDirectDebitBankName(mDirectDebitBankName.getText().toString());
        updateApplyBO.setDirectDebitAcctNo(RemoveAllSpace.removeAllSpace(mDirectDebitAcctNo.getText().toString()));
        updateApplyBO.setMobilePhoneVerifyCode(mMobilePhoneVerifyCode.getText().toString());
        updateApplyBO.setDirectDebitAcctPhone(mDirectDebitAcctPhone.getText().toString());
        updateApplyBO.setDirectDebitAcctName(mdirectDebitAcctName);
        updateApplyBO.setDirectDebitAcctId(mdirectDebitAcctId);
        updateApplyBO.setDirectDebitBankCode(mdirectDebitBankCode);//对私  放款银行卡的id
        Timber.e("updateApply===" + JSON.toJSONString(updateApplyBO));
        rest.updateApply(objectId, token, appId, updateApplyBO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                Timber.e("更新申请银行卡对私1025========" + loginBO.getUpdatedAt() + "==getUpdatedAt===="
                    + JSON
                    .toJSONString(loginBO));
                mLoadingDialog.dismiss();
                if (!TextUtils.isEmpty(loginBO.getError())) {
                    ToastUtil.showFailure(CardPrivateActivity.this, loginBO.getError());
                    if (loginBO.getCode() == 400) {
                        Intent intent = new Intent(CardPrivateActivity.this, MainActivity.class);
                        intent.putExtra("apply", "011111");
                        CardPrivateActivity.this.startActivity(intent);
                    }
                } else {
                    queryApply();
                }

            }, throwable ->
            {
                mLoadingDialog.dismiss();
                Timber.e("更新申请银行卡对私" + throwable.getMessage() + "==throwable==");
            });

    }


    /**
     * 获取最新的申请  点击按钮的时候  调用的
     */

    public void queryApply() {
        rest.queryApply(appId, objectId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                // hosPresenter.queryApplySuccess(queryApplyBO);
                Timber.e("获取最新的申请银行卡对私e" + JSON.toJSONString(queryApplyBO));
                if (TextUtils.isEmpty(queryApplyBO.getError())) {
                     /*   Intent intent = new Intent(CardPrivateActivity.this, MainActivity.class);
                        intent.putExtra("apply", "011111");
                        startActivity(intent);*/

                    Intent intentUpload = new Intent(CardPrivateActivity.this,
                        UploadPhotoActivity.class);
                    intentUpload.putExtra("confirmation", "confirmation");
                    CardPrivateActivity.this.startActivity(intentUpload);
                    ToastUtil.showSuccess(CardPrivateActivity.this, "绑卡成功");
                    CardPrivateActivity.this.finish();

                } else {
                    ToastUtil.showFailure(CardPrivateActivity.this, queryApplyBO.getError());
                }

            }, throwable -> {
                // hosPresenter.requestError("申请查询异常，请稍后再试");
                Timber.e("申请查询异常，请稍后再试银行卡对私e" + JSON.toJSONString(throwable));
            });
    }


    public void queryApply2() {
        Timber.e("首次进来的queryApply  appId：" + appId + "objectId:" + objectId + "====" + token);
        rest.queryApply(appId, objectId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                //mSecondaryBankDDA.setText(queryApplyBO.getSecondaryBankDDA());//银行卡（对公）
                Timber.e("获取最新的申请银行卡对私首次进来：" + JSON.toJSONString(queryApplyBO));
                if (TextUtils.isEmpty(queryApplyBO.getError())) {
                    mDirectDebitBankName.setText(queryApplyBO.directDebitBankName);//开户行（对私）
                    mdirectDebitBankCode = queryApplyBO.directDebitBankCode;
                    mDirectDebitAcctNo.setText(RemoveAllSpace.allSpace(queryApplyBO.directDebitAcctNo));//银行卡（对私）
                    mDirectDebitAcctPhone.setText(queryApplyBO.directDebitAcctPhone);//预留手机号码
                    mdirectDebitAcctName = queryApplyBO.directDebitAcctName;//法人姓名
                    mdirectDebitAcctId = queryApplyBO.directDebitAcctId;//法人身份证号
                }
                   /* if(TextUtils.isEmpty(queryApplyBO.getError())){

                        directDebitAcctId=queryApplyBO.getDirectDebitAcctId();//法人身份证号
                        secondaryBankAcctName=queryApplyBO.getSecondaryBankAcctName();//公司名称

                        mSecondaryBankName.setText(queryApplyBO.getSecondaryBankName());//开户行（对公）

                        msecondaryBankABA=queryApplyBO.getSecondaryBankABA();

                    }*/
            }, throwable ->
            {
                Timber.e("申请查询异常，请稍后再试银行卡对私e首次进来" + JSON.toJSONString(throwable));
            });

    }




}
