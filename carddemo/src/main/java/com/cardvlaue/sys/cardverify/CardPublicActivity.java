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
import com.cardvlaue.sys.data.UserInfoNewResponse;
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
 * <p>验证银行卡  对公<p/>
 */
public class CardPublicActivity extends BaseActivity implements CardPrivateView {

    public static final int CALLBACK_MSG_GETBANK_PUBLIC = 100000 + 7;    //对私的银行卡
    public static final String START_COUNTDOWN_TIME = "RegAndForgotActivity_START_COUNTDOWN_TIME";  // 开启倒计时
    private static final long ALL_TIME = 60000;//倒计时总时间
    private static final long STEP_TIME = 1000;//间隔时间
    public static Handler handler;
    public String msecondaryBankDDA;
    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private TextView mSecondaryBankName;//开户行
    private EditText mSecondaryBankDDA;//银行账号
    private TextView mDirectDebitBankName;//还款开户行
    private EditText mDirectDebitAcctNo;//还款银行账号
    private EditText mDirectDebitAcctPhone;//预留手机号码
    private TextView mCode;//获取图片验证码
    private EditText mMobilePhoneVerifyCode;//输入验证码
    private CardPrivatePresenter regAndForgotPresenter; //获取短信验证码
    private CountDownTimer mTimer;
    private String msecondaryBankABA;//银行卡的id(对公)
    private String mdirectDebitBankCode;//银行卡的id(对私)
    private String mdirectDebitAcctNo;
    private String mdirectDebitAcctPhone;
    private String mmobilePhoneVerifyCode;
    private String mdirectDebitBankName;
    private IFinancingRest rest;//更新申请
    private ILoginRest loginRest;//获取验证码
    private TasksRepository repository;
    private String appId, objectId, token, phone;
    private Subscriber<Object> mCodeSubscriber;
    private String directDebitAcctId;//法人身份证号**
    private String directDebitAcctNo;//首选扣款账户卡号
    private String directDebitAcctName;//法人姓名**    还款账户名称
    private String directDebitAcctPhone;//法人预留手机号
    private String secondaryBankAcctName;//公司名称
    private ContentLoadingDialog mLoadingDialog;
    private String verifyCode = "";   // 判断有没有获取验证码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_public);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        loginRest = HttpConfig.getClient().create(ILoginRest.class);
        initView();
        countdownTime();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == CardPrivateActivity.CALLBACK_MSG_GETBANK_PRIVATE) {//选择银行卡
                    Bundle bundle = msg.getData();
                    mSecondaryBankName.setText(bundle.get("title").toString().trim());
                    //secondaryBankABA
                    msecondaryBankABA = bundle.get("id").toString().trim();
                } else if (msg.what == CALLBACK_MSG_GETBANK_PUBLIC) {//选择银行卡
                    Bundle bundle = msg.getData();
                    mDirectDebitBankName.setText(bundle.get("title").toString().trim());
                    mdirectDebitBankCode = bundle.get("id").toString().trim();
                }
            }
        };
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

        mSecondaryBankName = (TextView) findViewById(R.id.tv_secondaryBankName);
        mSecondaryBankDDA = (EditText) findViewById(R.id.tv_secondaryBankDDA);
        mDirectDebitBankName = (TextView) findViewById(R.id.tv_directDebitBankName);
        mDirectDebitAcctNo = (EditText) findViewById(R.id.tv_directDebitAcctNo);
        mDirectDebitAcctPhone = (EditText) findViewById(R.id.tv_directDebitAcctPhone);
        mCode = (TextView) findViewById(R.id.tv_code);
        mMobilePhoneVerifyCode = (EditText) findViewById(R.id.tv_mobilePhoneVerifyCode);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();

        findViewById(R.id.tv_protocol).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(WebShowActivity.EXTRA_TITLE, "银行卡授权");
            intent.putExtra(WebShowActivity.EXTRA_URL, UrlConstants.BIND_BANK_CARD);
            intent.setClass(CardPublicActivity.this, WebShowActivity.class);
            startActivity(intent);
        });

        LoginResponse loginResponse = repository.getLogin();
        token = loginResponse.accessToken;

        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        objectId = userInfoNewResponse.objectId;
        appId = userInfoNewResponse.applicationId;
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            appId = repository.getApplicationId();
        }
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        queryApply();
        mBackView.setOnClickListener(v -> finish());

        //选择开户行放款(对公)
        mSecondaryBankName.setOnClickListener(v -> {
            Intent intentBank = new Intent(CardPublicActivity.this, ConfirmListActivity.class);
            if (mSecondaryBankName.getText().toString().trim().toString().equals("")) {
                intentBank.putExtra("title", "");
                intentBank.putExtra("type", "1");
                intentBank.putExtra("tagName", "secondaryBankName");
                startActivity(intentBank);
                return;
            } else {
                intentBank.putExtra("title", mSecondaryBankName.getText().toString());
                intentBank.putExtra("type", "1");
                intentBank.putExtra("tagName", "secondaryBankName");
                startActivity(intentBank);
            }
        });

        //选择开户行还款(对私)
        mDirectDebitBankName.setOnClickListener(v -> {
            Intent intentdirectDebitBank = new Intent(CardPublicActivity.this,
                ConfirmListActivity.class);
            if (mDirectDebitBankName.getText().toString().trim().toString().equals("")) {
                intentdirectDebitBank.putExtra("title", "");
                intentdirectDebitBank.putExtra("type", "2");
                intentdirectDebitBank.putExtra("tagName", "secondaryBankName1");
                startActivity(intentdirectDebitBank);
                return;
            } else {
                intentdirectDebitBank.putExtra("title", mDirectDebitBankName.getText().toString());
                intentdirectDebitBank.putExtra("type", "2");
                intentdirectDebitBank.putExtra("tagName", "secondaryBankName1");
                startActivity(intentdirectDebitBank);
            }
        });

        //点击提交按钮
        findViewById(R.id.bank_submit).setOnClickListener(v -> {
            msecondaryBankDDA = mSecondaryBankDDA.getText().toString();
            mdirectDebitBankName = mDirectDebitBankName.getText().toString();
            mdirectDebitAcctNo = mDirectDebitAcctNo.getText().toString();
            mdirectDebitAcctPhone = mDirectDebitAcctPhone.getText().toString();
            mmobilePhoneVerifyCode = mMobilePhoneVerifyCode.getText().toString();
            //非空判断  mdirectDebitBankName
            if (!CheckingForm
                .checkForBankPublic(msecondaryBankABA, RemoveAllSpace
                        .removeAllSpace(msecondaryBankDDA), mdirectDebitBankCode,
                    RemoveAllSpace.removeAllSpace(mdirectDebitAcctNo), mdirectDebitAcctPhone, mmobilePhoneVerifyCode)) {
                ToastUtil.showFailure(CardPublicActivity.this, CheckingForm.LastError); //失败
                return;
            } else {
                if (verifyCode.equals("")) {
                    ToastUtil.showFailure(CardPublicActivity.this, "请先获取验证码"); //失败
                } else {
                    mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
                    mLoadingDialog.setCancelable(false);
                    mLoadingDialog.show(getSupportFragmentManager(), "tag");
                    card();
                }

            }
        });

        /**
         * 获取图形验证码
         */
        mCode.setOnClickListener(v -> {
            Toast.makeText(CardPublicActivity.this, "获取验证码", Toast.LENGTH_LONG).show();
            regAndForgotPresenter
                .getSmsCode(CardPublicActivity.this, mDirectDebitAcctPhone.getText().toString(),
                    CardPrivateActivity.TYPE_PUBLIC, getSupportFragmentManager());
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
        createAuth(CardPublicActivity.this, bo);
    }


    private void createAuth(final Context context, final MerchantsByMobilePhoneBO bo) {
        AuthorizationsBO auth = new AuthorizationsBO();
        auth.setType(6);
        auth.setMobilePhone(bo.getMobilePhone());
        auth.setTime(
            DateFormat.format(LoginPresenter.DATE_FORMAT, System.currentTimeMillis()).toString());
        auth.setDeviceNumber(DeviceUtil.getDeviceInfo(context));
        auth.setAgent(DeviceUtil.getUA(context));
        auth.setGps(repository.getGpsAddress());
        auth.setIp(repository.getIpAddress());
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
                    Timber.e(authorizationsBO.getCreatedAt() + "银行卡对私CreatedAt" + authorizationsBO
                        .getObjectid());
                    if (authorizationsBO.getObjectid() != 0) {
                        createApply();
                    }
                }
            });
    }

    /**
     * 更新申请
     */
    public void createApply() {
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        updateApplyBO
            .setDirectDebitBankName(mDirectDebitBankName.getText().toString());//DirectDebitBankName
        updateApplyBO.setDirectDebitAcctNo(RemoveAllSpace.removeAllSpace(mDirectDebitAcctNo.getText().toString()));
        updateApplyBO.setMobilePhoneVerifyCode(mMobilePhoneVerifyCode.getText().toString());
        updateApplyBO.setDirectDebitAcctPhone(mDirectDebitAcctPhone.getText().toString());
        updateApplyBO.setSecondaryBankAcctName(secondaryBankAcctName);
        updateApplyBO.setDirectDebitAcctName(directDebitAcctName);
        updateApplyBO.setDirectDebitAcctId(directDebitAcctId);
        updateApplyBO.setSecondaryBankABA(msecondaryBankABA);  //secondaryBankName  // 开户银行   (放款)账户信息(对公)secondaryBankName  卡对应的id
        updateApplyBO.setSecondaryBankName(mSecondaryBankName.getText().toString());
        updateApplyBO.setSecondaryBankDDA(RemoveAllSpace.removeAllSpace(msecondaryBankDDA));//对公银行账号不能为空
        updateApplyBO.setDirectDebitBankCode(mdirectDebitBankCode);//对公  放款银行卡的id

        Timber.e("更新申请的参数objectId：" + objectId + "appId:" + appId + "updateApplyBO:" + JSON
            .toJSONString(updateApplyBO));
        rest.updateApply(objectId, token, appId, updateApplyBO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                mLoadingDialog.dismiss();
                Timber.e("更新申请银行卡对私" + loginBO.getUpdatedAt() + "==getUpdatedAt====" + JSON
                    .toJSONString(loginBO));
                if (!TextUtils.isEmpty(loginBO.getError())) {
                    ToastUtil.showFailure(CardPublicActivity.this, loginBO.getError());
                } else if (loginBO.getCode() == 400) {
                    Intent intent = new Intent(CardPublicActivity.this, MainActivity.class);
                    intent.putExtra("apply", "011111");
                    CardPublicActivity.this.startActivity(intent);
                    ToastUtil.showFailure(CardPublicActivity.this, loginBO.getError());
                } else {
                    queryApply2();
                }
            }, throwable ->
            {
                mLoadingDialog.dismiss();
                Timber.e("更新申请银行卡对私" + throwable.getMessage() + "==throwable==");
            });


    }

    /**
     * 获取最新的申请
     */

    public void queryApply2() {
        Timber.e("queryApply2queryApply  appId：" + appId + "objectId:" + objectId + "====" + token);
        rest.queryApply(appId, objectId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                mLoadingDialog.dismiss();
                Timber.e("获取最新的申请银行卡对私222222222222" + JSON.toJSONString(queryApplyBO));
                if (!TextUtils.isEmpty(queryApplyBO.getError())) {
                    ToastUtil.showFailure(CardPublicActivity.this, queryApplyBO.getError());
                    if (queryApplyBO.getCode() == 400) {
                        Intent intent = new Intent(CardPublicActivity.this, MainActivity.class);
                        intent.putExtra("apply", "011111");
                        CardPublicActivity.this.startActivity(intent);
                    }
                } else {
                      /*  Intent intent = new Intent(CardPublicActivity.this, MainActivity.class);
                        intent.putExtra("apply", "011111");
                        startActivity(intent);*/

                    //上传确认书
                    Intent intentUpload = new Intent(CardPublicActivity.this,
                        UploadPhotoActivity.class);
                    intentUpload.putExtra("confirmation", "confirmation");
                    CardPublicActivity.this.startActivity(intentUpload);
                    CardPublicActivity.this.finish();
                    ToastUtil.showSuccess(CardPublicActivity.this, "绑卡成功");
                }
            }, throwable ->
            {
                Timber.e("申请查询异常，请稍后再试银行卡对私e" + JSON.toJSONString(throwable));
            });

    }


    public void queryApply() {
        Timber.e("首次进来的queryApply  appId：" + appId + "objectId:" + objectId + "====" + token);
        rest.queryApply(appId, objectId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                if (TextUtils.isEmpty(queryApplyBO.getError())) {
                    directDebitAcctName = queryApplyBO.directDebitAcctName;//法人姓名
                    directDebitAcctId = queryApplyBO.directDebitAcctId;//法人身份证号
                    secondaryBankAcctName = queryApplyBO.secondaryBankAcctName;//公司名称

                    mSecondaryBankName.setText(queryApplyBO.secondaryBankName);//开户行（对公）

                    msecondaryBankABA = queryApplyBO.secondaryBankABA;

                    mSecondaryBankDDA.setText(RemoveAllSpace.allSpace(queryApplyBO.secondaryBankDDA));//银行卡（对公）
                    mDirectDebitBankName.setText(queryApplyBO.directDebitBankName);//开户行（对私）
                    //mdirectDebitBankCode
                    mdirectDebitBankCode = queryApplyBO.directDebitBankCode;
                    if (TextUtils.isEmpty(mdirectDebitBankCode)) {
                        mDirectDebitBankName.setText("");
                    }

                    mDirectDebitAcctNo.setText(RemoveAllSpace.allSpace(queryApplyBO.directDebitAcctNo));//银行卡（对私）
                    mDirectDebitAcctPhone.setText(queryApplyBO.directDebitAcctPhone);//预留手机号码
                }
                Timber.e("获取最新的申请银行卡对私e" + JSON.toJSONString(queryApplyBO));
            }, throwable ->
            {
                Timber.e("申请查询异常，请稍后再试银行卡对私e首次进来" + JSON.toJSONString(throwable));
            });

    }


}
