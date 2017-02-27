package com.cardvlaue.sys.amount;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.cardverify.UpdateApplyBO;
import com.cardvlaue.sys.creditreport.CreditReportActivity;
import com.cardvlaue.sys.creditreport.CreditReportGuideActivity;
import com.cardvlaue.sys.data.ApplyInfoResponse;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserCreditResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.face.FaceActivity;
import com.cardvlaue.sys.financeway.FinanceWayActivity;
import com.cardvlaue.sys.mobileverify.MobileVerifyActivity;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.ReadUtil;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.webshow.WebShowActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>计算额度的结果<p/>
 */
public class CountAmountActivity extends BaseActivity {

    public static final String BUS_COUNTAMOUNT_CODE = "BUS_COUNTAMOUNT_CODE";
    boolean isFirstsp;
    private Toolbar mToolbarView;
    private TextView mBackView, mTitleTextView, mSubmit, mPlanFundTerm, mPaymentMethod, mAmount;
    private TextView mAmountExpire;//额度是否已经过期
    private ImageView mIvExpire;//额度是否已经过期(图片要变化)
    private PromoteAmountDialog promoteAmountDialog2;//提升额度小技巧
    private AmountDialogNew amountDialog;//手机验证和征信验证
    private TasksRepository repository;
    private String  objectId,token, creditId, applicationId, isJxlValid, creditReportStatus,isKalaRecognize;//
    private IFinancingRest rest;//更新申请
    private Subscriber mMoneySubscriber;
    private ContentLoadingDialog mLoadingDialog, mLoadingDialog2;
    private Handler timeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_amount);
        initView();
        getUserInfo();
        timeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 2) {
                    if (mLoadingDialog2 != null) {
                        mLoadingDialog2.dismiss();
                        UpdateUserInfo();
                    }
                }
            }
        };
        mMoneySubscriber = new Subscriber<Object>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object o) {
                if (o instanceof BusIndustrySelect) {
                    BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                    if (CountAmountActivity.BUS_COUNTAMOUNT_CODE
                        .equals(busIndustrySelect.getBus())) {
                        Timber.e("收到更新聚信立状态"+busIndustrySelect.getTypeId());
                        if (busIndustrySelect.getTypeId().equals("isJxlValid")) {
                            queryApply();
                            amountDialog.setPhoneListener(null);
                        }else if(busIndustrySelect.getTypeId().equals("isFace")){
                            queryApply();
                            amountDialog.setClickFace(null);
                        }else if(busIndustrySelect.getTypeId().equals("isCredit")){
                            queryApply();
                            amountDialog.setCreditListener(null);
                        }
                    }
                }
            }
        };
        RxBus.getDefaultBus().toObserverable().subscribe(mMoneySubscriber);
    }

    public void initView() {
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        LoginResponse loginResponse = repository.getLogin();
        token = loginResponse.accessToken;


        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        objectId = loginResponse.objectId;
        applicationId = userInfoNewResponse.applicationId;

        mLoadingDialog = ContentLoadingDialog.newInstance("加载中...");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show(getSupportFragmentManager(), "tag");
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mSubmit = (TextView) findViewById(R.id.tv_submit);
        mPlanFundTerm = (TextView) findViewById(R.id.tv_planFundTerm);
        mPaymentMethod = (TextView) findViewById(R.id.tv_paymentMethod);
        mAmount = (TextView) findViewById(R.id.tv_amount);
        mAmountExpire = (TextView) findViewById(R.id.amount_expire);//额度是否已经过期
        mIvExpire = (ImageView) findViewById(R.id.iv_Expire);//额度是否已经过期(图片要变化)
        this.setSupportActionBar(mToolbarView);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.amount_calculation));
        mBackView.setOnClickListener(v -> {
            try {
                ((CVApplication) getApplicationContext()).getQueue().back(this);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //提升额度小技巧(弹出).点击按钮我知道的时候，就返回到添加流水的界面
        findViewById(R.id.tv_promote_amount).setOnClickListener(v -> {
            PromoteAmountDialog promoteAmountDialog = PromoteAmountDialog
                .newInstance("提升额度小技巧", "1.您可以返回上级界面，通过新增POS商编的方式来提升您的授信额度", "我知道了，马上提升额度");
            promoteAmountDialog.setCancelable(false);
            promoteAmountDialog.show(getFragmentManager(), "promoteAmountDialog");
            promoteAmountDialog.setOnClickOkListener(() ->
                startActivity(new Intent(CountAmountActivity.this, FinanceWayActivity.class)));
        });

        Timber.e("======多申请里面返回的==repository.getCreditId()====="+repository.getCreditId());
        if (!TextUtils.isEmpty(repository.getCreditId())) {//获取多申请里面返回的
            creditId = repository.getCreditId();
            objectId = repository.getMerchantId();
            setData();
        } else {
            getUserInfo();
        }
    }

    /**
     * 1、当计算完成，参考额度为0时，此按钮变为“返回提升额度”。 2、并在按钮上方加入提示文字：“由于您的额度为0，暂不能继续申请， 请提升完额度以后再完成后续流程
     */
    private void setData() {
        repository.getCreditInfo(objectId, token, creditId,
            new TasksDataSource.LoadResponseNewCallback<UserCreditResponse, String>() {
                @Override
                public void onResponseSuccess(UserCreditResponse userCreditResponse) {
                    double creditLine = userCreditResponse.totalCreditLine;
                    if ("N".equals(userCreditResponse.getCreditStatus()) || "E"
                        .equals(userCreditResponse.getCreditStatus())
                        || ("PM".equals(userCreditResponse.getCreditStatus())
                        && String.valueOf(creditLine) != null && !String.valueOf(creditLine)
                        .equals("0"))) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }

                        mPlanFundTerm.setText(userCreditResponse.getLoanPeriod() + "天");
                        mPaymentMethod.setText(userCreditResponse.getPaymentMethod());
                        mAmount.setText(ReadUtil.fmtMicrometer(String.valueOf(creditLine)));
                        String LastUpdateTime = "";
                        String InvalidDate = "";
                        if (!TextUtils.isEmpty(userCreditResponse.getLastUpdateTime())) {
                            LastUpdateTime = Convert
                                .convertToString(userCreditResponse.getLastUpdateTime());
                        }
                        if (!TextUtils.isEmpty(userCreditResponse.getInvalidDate())) {
                            InvalidDate = Convert
                                .convertToString(userCreditResponse.getInvalidDate());
                        }
                        int decDate = DateFormatTool.decDate((LastUpdateTime), InvalidDate);
                        Timber.e("参考额度已过期"+decDate);
                        if (decDate == 0) {
                            mSubmit.setText("重新计算额度");
                            mAmountExpire.setText("参考额度已过期");
                            mIvExpire.setImageResource(R.mipmap.icon_amount_no);
                            //图片应该也要改变的   mIvExpire
                        } else {
                            mSubmit.setText("下一步");
                            mAmountExpire.setText("参考额度已生成");
                            mIvExpire.setImageResource(R.mipmap.icon_amount);
                        }

                        if (creditLine <= 0) {
                            mSubmit.setText("重新计算额度");
                        }
                    }
                }

                @Override
                public void onResponseFailure(String f) {
//                Toast.makeText(CountAmountActivity.this, "授信查询异常，请稍后再试", Toast.LENGTH_LONG).show();
                }
            });

        mSubmit.setOnClickListener(v -> {
            if (mSubmit.getText().toString().equals("下一步")){ //弹出征信和手机验证对话框
                getPhoneCreditVerification();
                return;
            } else if (mSubmit.getText().toString().equals("重新计算额度")) { //到算算融资额度   post和支付宝
                promoteAmountDialog2 = PromoteAmountDialog
                    .newInstance("提示", "由于您的参考额度为0，暂不允许进行下一步操作！您可以返回上级界面通过新增POS商编的方式来提升您的授信额度",
                        "我知道了，马上提升额度");
            }
            if (promoteAmountDialog2 != null) { //提升额度小技巧(弹出) 点击按钮我知道的时候，就返回到添加流水的界面
                promoteAmountDialog2.show(getFragmentManager(), "promoteAmountDialog");
                promoteAmountDialog2.setOnClickOkListener(() ->
                    startActivity(new Intent(CountAmountActivity.this, FinanceWayActivity.class)));
            }
        });
    }

    //获取用户
    public void getUserInfo() {
        repository.getUserInfo(objectId, token)
            .compose(bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                switch (s.responseSuccess(this)) {
                    case -1:
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                        ToastUtil.showFailure(this, s.getError());
                        break;
                    case 0:
                        objectId=s.objectId;
                        applicationId=s.applicationId;
                        Timber.e("=========getUserInfo========s.objectId====="+s.objectId+"===s.applicationId=="+s.applicationId);
                        obtainApplyInfo(s.objectId, token, s.applicationId);
                        break;
                }
            }, Throwable::printStackTrace);
    }

    private void obtainApplyInfo(@NonNull final String objectId, @NonNull final String accessToken,
        @NonNull String applicationId) {
        Timber.e("obtainApplyInfo:%s||%s", objectId, applicationId);
        repository.getApplyInfo(objectId, accessToken, applicationId,
            new TasksDataSource.LoadResponseNewCallback<ApplyInfoResponse, String>() {
                @Override
                public void onResponseSuccess(ApplyInfoResponse response) {
                    Timber.e("获取最新的申请  =obtainApplyInfo==计算额度结果====="+JSON.toJSONString(response));
                    creditId = response.creditId;
                    isJxlValid = response.isJxlValid;
                    isKalaRecognize=response.isKalaRecognize;
                    creditReportStatus = response.creditReportStatus;
                    setData();
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    if (getIntent().getStringExtra("PhoneCreditVerification") != null) {
                        getPhoneCreditVerification();
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                }
            });
    }

    /**
     * 获取最新的申请
     */
    public void queryApply() {
        rest.queryApply(applicationId, objectId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                    Timber.e("获取最新的申请  ===计算额度结果====="+JSON.toJSONString(queryApplyBO));
                    isJxlValid = queryApplyBO.isJxlValid;
                    creditReportStatus = queryApplyBO.creditReportStatus;
                    isKalaRecognize=queryApplyBO.isKalaRecognize;
                    if (isJxlValid.equals("1") && creditReportStatus.equals("1")&&(isKalaRecognize.equals("1")||isKalaRecognize.equals("2"))) {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismiss();
                        }
                        if (amountDialog != null) {
                            amountDialog.dismiss();
                        }
                        amountDialog.setCreditListener(null);

                        mLoadingDialog2 = ContentLoadingDialog.newInstance("下一步...");
                        mLoadingDialog2.show(getSupportFragmentManager(), "tag");
                        timeHandler.sendEmptyMessageDelayed(2, 3000);
                    }
                }, throwable -> {
                    Timber.e(JSON.toJSONString(throwable));
                }
            );
    }

    /**
     * 更新申请
     */
    public void UpdateUserInfo() {
        if (!TextUtils.isEmpty(repository.getSetStep())
            && Integer.parseInt(repository.getSetStep()) > 40) {
            try {
                ((CVApplication) getApplicationContext()).getQueue().next(this, 40);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        updateApplyBO.setSetStep(40);

        UserInfoNewResponse userInfoR = repository.getUserInfo();
        applicationId=userInfoR.applicationId;
        Timber.e(userInfoR.objectId+"=============40============"+objectId+"==applicationId=="+applicationId+"==repository.getApplicationId()=="+repository.getApplicationId());
        rest.updateApply(userInfoR.objectId, token, applicationId, updateApplyBO)
            .subscribeOn(Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                Timber.e("额度计算结果============"+JSON.toJSONString(loginBO));
                switch (loginBO.responseSuccess(CountAmountActivity.this)) {
                    case -1:
                        ToastUtil.showFailure(CountAmountActivity.this, loginBO.getError());
                        break;
                    case 0:
                        try {
                            ((CVApplication) getApplicationContext()).getQueue().next(this, 40);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }, throwable ->
                Timber.e(throwable.getMessage() + "==throwable=="));
    }

    public void getPhoneCreditVerification() {
        if (mSubmit.getText().toString().equals("下一步")) //弹出征信和手机验证对话框
        {
            /**
             * 检查是否是首次安装
             */
            SharedPreferences sp = this.getSharedPreferences("share", Activity.MODE_PRIVATE);
            isFirstsp = sp.getBoolean("isFirstdd", true);
            int isJlValidStatus, isCreditReportStatus, isKalaRecognizeStatus;
            if ("1".equals(isJxlValid) && "1".equals(creditReportStatus) && ("1".equals(isKalaRecognize)||"2".equals(isKalaRecognize))) {
                UpdateUserInfo();//更新申请  直接到上传照片的界面){
                return;
            } else {

                if ("0".equals(isJxlValid))
                    isJlValidStatus = 0;
                else
                    isJlValidStatus = 1;

                if ("0".equals(creditReportStatus))
                    isCreditReportStatus = 0;
                else
                    isCreditReportStatus = 1;

                if ("0".equals(isKalaRecognize)){
                    isKalaRecognizeStatus = 0;
                } else{
                    isKalaRecognizeStatus = 1;
                }

                Timber.e("isJlValidStatus"+isJlValidStatus+"isCreditReportStatus"+isCreditReportStatus+"isKalaRecognizeStatus"+isKalaRecognizeStatus);

                amountDialog = AmountDialogNew
                    .newInstance(isJlValidStatus, isCreditReportStatus, isKalaRecognizeStatus);
                amountDialog.show(getFragmentManager(), "amountDialog");

                if("0".equals(isJxlValid)){
                    amountDialog.setPhoneListener(() ->
                        startActivity(
                            new Intent(CountAmountActivity.this, MobileVerifyActivity.class)));
                }

                if("0".equals(creditReportStatus)){
                    amountDialog.setCreditListener(() ->  {
                        if (isFirstsp) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("isFirstdd", false);
                            editor.apply();
                            startActivity(new Intent(CountAmountActivity.this,
                                CreditReportGuideActivity.class)
                                .putExtra(WebShowActivity.EXTRA_COLOR, "1001")
                                .putExtra(WebShowActivity.EXTRA_TITLE,
                                    getString(R.string.credit_report_guide))
                                .putExtra(WebShowActivity.EXTRA_URL, UrlConstants.CREDIT_REPORT));
                        } else {
                            startActivity(
                                new Intent(CountAmountActivity.this, CreditReportActivity.class));
                        }
                    });
                }

                if("0".equals(isKalaRecognize)){
                    amountDialog.setClickFace(() ->
                        startActivity(
                            new Intent(CountAmountActivity.this, FaceActivity.class)));
                }
            }

            amountDialog.setClickNext(() -> UpdateUserInfo());
        }


    }
}