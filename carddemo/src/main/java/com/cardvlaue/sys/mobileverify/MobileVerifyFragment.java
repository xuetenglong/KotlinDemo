package com.cardvlaue.sys.mobileverify;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.AmountCountdownActivity;
import com.cardvlaue.sys.amount.CountAmountActivity;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.amount.PromoteAmountDialog;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MobileVerifyFragment extends Fragment implements MobileVerifyContract.View {

    private MobileVerifyContract.Presenter mPresenter;

    private EditText mMobilePhoneView, mPwdView, mCodeView;

    private TextView mCommitView;

    private LinearLayout mVerifyView;

    private Toolbar mToolbarView;

    private TextView mBackView;

    private TextView mTitleTextView;

    private TextView queryPwdText;

    private MobileVerifyDialog mobileVerifyDialog;

    private RelativeLayout mDetail;

    private Handler timeHandler;

    private ContentLoadingDialog mLoadingDialog;//加载框

    private IFinancingRest rest;//更新申请

    private TasksRepository repository;

    public static MobileVerifyFragment newInstance() {
        return new MobileVerifyFragment();
    }

    @Override
    public void setPresenter(@NonNull MobileVerifyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mobile_verify, container, false);
        mobileVerifyDialog = new MobileVerifyDialog();
        mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
        mLoadingDialog.setCancelable(false);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        repository = ((CVApplication) getActivity().getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        mobileVerifyDialog
            .setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mMobilePhoneView = (EditText) view.findViewById(R.id.et_mobile_verify_phone);
        mPwdView = (EditText) view.findViewById(R.id.et_mobile_verify_pwd);
        mCommitView = (TextView) view.findViewById(R.id.mobile_verify_submit);
        mCodeView = (EditText) view.findViewById(R.id.et_mobile_verify_code);
        queryPwdText = (TextView) view.findViewById(R.id.queryPwdText);
        mVerifyView = (LinearLayout) view.findViewById(R.id.ly_mobile);
        mToolbarView = (Toolbar) view.findViewById(R.id.title_default_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbarView);
        mBackView = (TextView) view.findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) view.findViewById(R.id.title_default_middle);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.mobile_verify_name));
        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        Timber.e("==userInfoNewResponse.mobilePhone==" + userInfoNewResponse.mobilePhone);
        if (userInfoNewResponse != null) {
            mMobilePhoneView.setText(userInfoNewResponse.mobilePhone);
        }
        mDetail = (RelativeLayout) view.findViewById(R.id.iv_detail);
        timeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 11031) {
                    getUserInfo();
                }
            }
        };
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCommitView.setOnClickListener(v -> {
            mLoadingDialog.show(getFragmentManager(), "tag");
            mPresenter.clickCommit(getContext(), mMobilePhoneView.getText().toString(),
                mPwdView.getText().toString(), mCodeView.getText().toString());
        });

        mBackView.setOnClickListener(v -> getActivity().finish());

        view.findViewById(R.id.tv_radio).setOnClickListener(v ->
            mobileVerifyDialog.show(getActivity().getFragmentManager(), "dialog_fragment"));
        view.findViewById(R.id.ll_test).setEnabled(false);

        mDetail.setOnClickListener(v ->
            PromoteAmountDialog.newInstance("什么是手机服务密码",
                "手机服务密码是你的号码在移动运营公司进行获取服务时需要提供的一个身份凭证，比如查询通话详单就需要提供身份证明或者服务密码。这个密码是有移动运营商提供，可以自己修改的。",
                "我知道了")
                .show(getActivity().getFragmentManager(), "promoteAmountDialog"));
    }

    @Override
    public void showSuccessMsg(@NonNull String msg) {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
        }
        BusIndustrySelect select = new BusIndustrySelect(
            AmountCountdownActivity.BUS_AMOUNT_CODE);
        select.setTypeId("isJxlValid");
        RxBus.getDefaultBus().send(select);
        ToastUtil.showSuccess(getContext(), "验证成功");
        timeHandler.sendEmptyMessageDelayed(11031, 3000);

    }

    @Override
    public void showFailureMsg(@NonNull String msg) {
        mLoadingDialog.dismissAllowingStateLoss();
        ToastUtil.showFailure(getContext(), msg);
    }

    @Override
    public void showVerifyCode() {
        queryPwdText.setText("短信验证");
        mCodeView.setHint("请输入短信验证码");
        mCodeView.getText().clear();
        mVerifyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showqueryPwd() {
        queryPwdText.setText("账单查询码");
        mCodeView.setHint("请输入账单查询码");
        mCodeView.getText().clear();
        mVerifyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void colseActivity() {
        getActivity().finish();
    }

    /**
     * 更新用户
     */
    public void getUserInfo() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isJxlValid", "1");
        LoginResponse login = repository.getLogin();
        rest.createOrUpdateUserInfo(repository.getMerchantId(), login.accessToken,
            repository.getMerchantId(), jsonObject)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginResponse -> {
                Timber.e("==更新用户response====" + JSON.toJSONString(loginResponse));
                if (TextUtils.isEmpty(loginResponse.getError())) {
                    BusIndustrySelect select = new BusIndustrySelect(
                        CountAmountActivity.BUS_COUNTAMOUNT_CODE);
                    select.setTypeId("isJxlValid");
                    RxBus.getDefaultBus().send(select);
                    getActivity().finish();
                } else {
                    ToastUtil.showFailure(getActivity(), loginResponse.getError());
                }
            }, throwable -> {
                Timber.e("createOrUpdateUserInfothrowableEEE -> %s", throwable.getMessage());
            });
    }
}
