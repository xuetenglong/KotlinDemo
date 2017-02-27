package com.cardvlaue.sys.applyinfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.tongdun.android.shell.FMAgent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.CountAmountActivity;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.cardverify.AuthorizationsBO;
import com.cardvlaue.sys.cardverify.ILoginRest;
import com.cardvlaue.sys.cardverify.UpdateApplyBO;
import com.cardvlaue.sys.data.ErrorResponse;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.financeway.FinanceWayActivity;
import com.cardvlaue.sys.login.LoginPresenter;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.my.IosScrollViewLayout;
import com.cardvlaue.sys.registerverify.RegisterOkDialog;
import com.cardvlaue.sys.uploadphoto.UploadPhotoActivity;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.ToastUtil;
import java.util.List;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <P>申请信息 信息详情页面<P/>
 */
public class ApplyInfoActivity extends BaseActivity {

    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private RecyclerView mRecyclerView;
    private Button mBtnSubmit;//确定按钮
    private Button mBtnBack;//返回修改按钮
    private LinearLayout mLyBottom;//控制是否显示
    private ApplyInfoAdapter mApplyInfoAdapter;
    private ApplyRest applyRest;
    private IFinancingRest rest;//更新申请
    private ILoginRest loginRest;//授权
    private String phone;
    private String applicationId;
    private String objectId;
    private String token;
    private TasksRepository repository;
    private IosScrollViewLayout scrollView;
    private ContentLoadingDialog mLoadingDialog;
    private RegisterOkDialog registerOkDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_info);
        initView();
        mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
        mLoadingDialog.setCancelable(false);
        if (getIntent().getStringExtra("applyinfo") != null) {
            findViewById(R.id.ly_bottom).setVisibility(View.GONE);
            mTitleTextView.setText("申请信息查看");
        }
        applyRest = HttpConfig.getClient().create(ApplyRest.class);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        applicationId = repository.getUserInfo().applicationId;

        if (TextUtils.isEmpty(applicationId) && TextUtils.isEmpty(objectId) && TextUtils
            .isEmpty(token)) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "1");
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            applicationId = repository.getApplicationId();
        }
        applyRest.queryConfirmList(token, objectId, applicationId, jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                Timber.e(s + "申请信息");
                ErrorResponse error = null;
                try {
                    error = JSON.parseObject(s, ErrorResponse.class);
                    switch (error.responseSuccess(this)) {
                        case -1:
                            ToastUtil.showFailure(this, error.getError());
                            break;
                    }
                } catch (Exception e) {
                    List<Confirmlist> confirmlist = JSON.parseArray(s, Confirmlist.class);
                    mApplyInfoAdapter.update(confirmlist.get(0).getItems());
                    mRecyclerView.smoothScrollToPosition(confirmlist.get(0).getItems().size());
                }
            }, throwable -> Timber.e("CALL:" + throwable.getMessage()));
    }

    public void initView() {
        loginRest = HttpConfig.getClient().create(ILoginRest.class);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        scrollView = (IosScrollViewLayout) findViewById(R.id.scrollView);
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.apply_confirmation));

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mLyBottom = (LinearLayout) findViewById(R.id.ly_bottom);
        //创建一个线性布局管理器,然后设置布局的方向
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        if (getIntent().getStringExtra("applyinfo") != null) {
            mApplyInfoAdapter = new ApplyInfoAdapter(this, "5");
        } else {
            mApplyInfoAdapter = new ApplyInfoAdapter(this, "4");
        }

        //  mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mApplyInfoAdapter);

        //确认提交
        mBtnSubmit.setOnClickListener(v -> {
            mLoadingDialog.show(getSupportFragmentManager(), "授权接口");
            createAuth();
        });

        //返回修改
        mBtnBack.setOnClickListener(v -> updateApply());
        mBackView.setOnClickListener(v -> {
            if (getIntent().getStringExtra("applyinfo") != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("apply", "011111");
                startActivity(intent);
                finish();
               /* BusIndustrySelect select = new BusIndustrySelect(ApplyFragment.BUS_APPLY_CODE);
                select.setTypeId("apply");
                RxBus.getDefaultBus().send(select);*/
            } else if (getIntent().getStringExtra("applyinfo") == null) {
                try {
                    ((CVApplication) getApplicationContext()).getQueue().back(this);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 更新申请
     */
    public void updateApply() {
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        updateApplyBO.setIsSubmitApplication("0");
        updateApplyBO.setBlackBox(FMAgent.onEvent(this));
        updateApplyBO.setIpAddress(repository.getIpAddress());
        Timber.e("第一次的appId：" + applicationId + "objectId:" + objectId);
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            applicationId = repository.getApplicationId();
        }

        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        rest.updateApply(objectId, token, applicationId, updateApplyBO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                if (loginBO.getCode() == 400) {//新增error400,抛出400申请未通过，直接到申请列表
                    if (!TextUtils.isEmpty(loginBO.getError())) {
                        ToastUtil.showFailure(this, loginBO.getError());
                    } else {
                        ToastUtil.showFailure(this, "未知错误400");
                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("apply", "011111");
                    startActivity(intent);
                    finish();
                } else if (!TextUtils.isEmpty(loginBO.getUpdatedAt())) {
                    if (!TextUtils.isEmpty(loginBO.getUpdatedAt())) {
                        Intent intent = new Intent(this, UploadPhotoActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, throwable ->
                Timber.e(throwable.getMessage() + "==throwable=="));
    }

    /**
     * 确认提交申请（先授权）
     */
    public void updateApplyConfirm() {
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        updateApplyBO.setBlackBox(FMAgent.onEvent(this));
        updateApplyBO.setIpAddress(repository.getIpAddress());
        updateApplyBO.setType("5");
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            applicationId = repository.getApplicationId();
        }
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        rest.updateApply(objectId, token, applicationId, updateApplyBO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                Timber.e("loginBO" + JSON.toJSONString(loginBO));
                switch (loginBO.responseSuccess(this)) {
                    case 0:
                        mLoadingDialog.dismissAllowingStateLoss();
                        if (!TextUtils.isEmpty(loginBO.getError())) {
                            if (loginBO.getCode() == 269) {
                                startActivity(new Intent(this, CountAmountActivity.class));
                                finish();
                            } else if (loginBO.getCode() == 272) {
                                startActivity(new Intent(this, FinanceWayActivity.class));
                                finish();
                            } else if (loginBO.getCode() == 261) {//
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra("apply", "apply");
                                startActivity(intent);
                                finish();
                            } else if (loginBO.getCode() == 400) {
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra("apply", "011111");
                                startActivity(intent);
                                finish();
                            }
                            ToastUtil.showFailure(this, loginBO.getError());
                        } else {
                            queryApply();
                        }
                        break;
                    case -1:
                        mLoadingDialog.dismissAllowingStateLoss();
                        if (!TextUtils.isEmpty(loginBO.getError())) {
                            if (loginBO.getCode() == 269) {
                                startActivity(new Intent(this, CountAmountActivity.class));
                                finish();
                            } else if (loginBO.getCode() == 272) {
                                startActivity(new Intent(this, FinanceWayActivity.class));
                                finish();
                            } else if (loginBO.getCode() == 261) {
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra("apply", "apply");
                                startActivity(intent);
                                finish();
                            } else if (loginBO.getCode() == 275) {
                                registerOkDialog = RegisterOkDialog
                                    .newInstance("您的手机验证还未验证，请进行验证。", "立即验证");
                                registerOkDialog.show(getSupportFragmentManager(), "tag");
                                registerOkDialog.setOnClickOkListener(() -> startActivity(
                                    new Intent(this, CountAmountActivity.class)
                                        .putExtra("PhoneCreditVerification",
                                            "PhoneCredit")));//MobileVerifyActivity
                            } else if (loginBO.getCode() == 276) {
                                registerOkDialog = RegisterOkDialog
                                    .newInstance("您的征信验证还未验证，请进行验证。", "立即验证");
                                registerOkDialog.show(getSupportFragmentManager(), "tag");
                                registerOkDialog.setOnClickOkListener(() -> startActivity(
                                    new Intent(this, CountAmountActivity.class)
                                        .putExtra("PhoneCreditVerification",
                                            "PhoneCredit")));//CreditReportActivity
                            } else if (loginBO.getCode() == 277) {
                                registerOkDialog = RegisterOkDialog
                                    .newInstance("您的手机验证和征信验证还未验证，请进行验证。", "立即验证");
                                registerOkDialog.show(getSupportFragmentManager(), "tag");
                                registerOkDialog.setOnClickOkListener(() -> startActivity(
                                    new Intent(this, CountAmountActivity.class)
                                        .putExtra("PhoneCreditVerification",
                                            "PhoneCredit")));//CreditReportActivity
                            }else if(loginBO.getCode() == 279){
                                registerOkDialog = RegisterOkDialog
                                    .newInstance("您的人脸识别验证还未验证，请进行验证。", "立即验证");
                                registerOkDialog.show(getSupportFragmentManager(), "tag");
                                registerOkDialog.setOnClickOkListener(() -> startActivity(
                                    new Intent(this, CountAmountActivity.class)
                                        .putExtra("PhoneCreditVerification",
                                            "PhoneCredit")));
                            }else if (loginBO.getCode() == 400) {
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra("apply", "011111");
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtil.showFailure(this, loginBO.getError());
                            }
                        }
                        break;
                }
            }, throwable -> {
                mLoadingDialog.dismissAllowingStateLoss();
                Timber.e(throwable.getMessage() + "==throwable==");
            });
    }

    /**
     * 创建授权
     */
    private void createAuth() {
        AuthorizationsBO auth = new AuthorizationsBO();
        auth.setTime(
            DateFormat.format(LoginPresenter.DATE_FORMAT, System.currentTimeMillis()).toString());
        auth.setDeviceNumber(DeviceUtil.getDeviceInfo(this));
        auth.setAgent(DeviceUtil.getUA(this));
        auth.setBlackBox(FMAgent.onEvent(this));
        auth.setType(5);
        if (!TextUtils.isEmpty(repository.getIpAddress())) {
            auth.setIp(repository.getIpAddress());
        }
        String gpsStr = repository.getGpsAddress();
        if (!TextUtils.isEmpty(gpsStr)) {
            auth.setGps(gpsStr);
        }

        loginRest.createAuth(auth)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<AuthorizationsBO>() {
                @Override
                public void onCompleted() {
                    // isLoading = false;
                }

                @Override
                public void onError(Throwable e) {
                    // isLoading = false;
                    mLoadingDialog.dismissAllowingStateLoss();
                    Timber.e(e.getMessage());
                    ToastUtil.showFailure(ApplyInfoActivity.this, e.getMessage());
                }

                @Override
                public void onNext(AuthorizationsBO authorizationsBO) {
                    Timber
                        .e(authorizationsBO.getCreatedAt() + "CreatedAt" + authorizationsBO
                            .getObjectid());
                    if (authorizationsBO.getObjectid() != 0) {
                        updateApplyConfirm();
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                    Timber.e("获取最新的申请" + JSON.toJSONString(queryApplyBO));
                    Timber.e(JSON.toJSONString(queryApplyBO));
                    mLoadingDialog.dismissAllowingStateLoss();
                    startActivity(new Intent(this, AppSubmitActivity.class));
                    finish();
                }, throwable -> {
                    mLoadingDialog.dismissAllowingStateLoss();
                    Timber.e(JSON.toJSONString(throwable));
                }
            );
    }
}
