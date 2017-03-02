package com.cardvlaue.sys.registerverify;

import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.ToastUtil;
import com.trello.rxlifecycle.components.support.RxFragment;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class RegisterVerifyFragment extends RxFragment implements RegisterVerifyContract.View {

    public static final String EXTRA_MOBILE_PHONE = "mobilePhone";

    public static final String EXTRA_PASSWORD = "password";
    /**
     * 手机号
     */
    @BindView(R.id.tv_register_verify_mobile_phone)
    TextView mPhoneView;
    /**
     * 验证码输入框
     */
    @BindView(R.id.et_register_verify_sms_code)
    EditText mSmsCodeView;
    /**
     * 重新获取验证码
     */
    @BindView(R.id.btn_register_verify_obtain_code)
    Button mReSendCodeView;
    /**
     * 联系客服
     */
    @BindView(R.id.tv_register_verify_service)
    TextView mContractServiceView;
    /**
     * 邀请码输入框
     */
    @BindView(R.id.et_register_verify_invite_code)
    EditText mInviteCodeView;

    /**
     * 点击我有邀请码
     */
    @BindView(R.id.btn_invite_code)
    Button mBtnInviteCodeView;

    /**
     * 邀请码的线条
     */
    @BindView(R.id.view_register_verify_invite_code)
    View mInviteCodeLines;

    /**
     * 完成按钮
     */
    @BindView(R.id.btn_forgot_pwd_commit)
    Button mForgotPwdCommit;

    private RegisterVerifyContract.Presenter mPresenter;
    private String mMobilePhone, mPassword;
    private CountDownTimer mTimer;

    private ContentLoadingDialog mLoadingDialog;
    private Disposable mDisposable;

    public static RegisterVerifyFragment newInstance() {
        return new RegisterVerifyFragment();
    }

    /**
     * 重新获取验证码
     */
    @OnClick(R.id.btn_register_verify_obtain_code)
    void clickReLoadCode() {
        mPresenter.clickReSendCode(mMobilePhone, getFragmentManager());
    }


    /**
     * 我有邀请码
     */
    @OnClick(R.id.btn_invite_code)
    void clickInviteCode(){
        mBtnInviteCodeView.setVisibility(View.GONE);
        mInviteCodeView.setVisibility(View.VISIBLE);
        mInviteCodeLines.setVisibility(View.VISIBLE);
        scaleX(mForgotPwdCommit);
        ObjectAnimator oa=ObjectAnimator.ofFloat(mInviteCodeLines, "alpha", 0f, 1f);
        oa.setDuration(750);
        oa.start();

        ObjectAnimator mInviteCodeViewoa=ObjectAnimator.ofFloat(mInviteCodeView, "alpha", 0f, 1f);
        mInviteCodeViewoa.setDuration(750);
        mInviteCodeViewoa.start();

    }

    @OnClick(R.id.btn_forgot_pwd_commit)
    void clickCommit() {
        mPresenter.clickCommit(mMobilePhone, mPassword, mSmsCodeView.getText().toString(),
            mInviteCodeView.getText().toString());
    }

    /**
     * 点击联系客服
     */
    @OnClick(R.id.tv_register_verify_service)
    void clickContractService() {
        new AlertDialog.Builder(getContext())
            .setMessage(getString(R.string.more_phone))
            .setPositiveButton(getString(R.string.more_call_yes), (dialogInterface, i) -> {
                if (PackageManager.PERMISSION_GRANTED == ContextCompat
                    .checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)) {
                    startActivity(
                        new Intent(Intent.ACTION_CALL,
                            Uri.parse("tel:" + getString(R.string.more_phone))));
                } else {
                    ToastUtil.showFailure(getContext(), getString(R.string.more_call_permission));
                }
            })
            .setNegativeButton(getString(R.string.more_call_no), null)
            .show();
    }

    /**
     * 点击后退
     */
    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        getActivity().finish();
    }

    @Override
    public void setPresenter(RegisterVerifyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMobilePhone = getActivity().getIntent().getStringExtra(EXTRA_MOBILE_PHONE);
        mPassword = getActivity().getIntent().getStringExtra(EXTRA_PASSWORD);
        if (TextUtils.isEmpty(mMobilePhone) || TextUtils.isEmpty(mPassword)) {
            ToastUtil.showFailure(getContext(), "程序异常，请重新打开");
        }

        mLoadingDialog = ContentLoadingDialog.newInstance("注册中...");
        mLoadingDialog.setCancelable(false);

        mTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                mReSendCodeView.setEnabled(false);
                String tickText = "验证码(" + l / 1000 + ")";
                mReSendCodeView.setText(tickText);
            }

            @Override
            public void onFinish() {
                mReSendCodeView.setEnabled(true);
                mReSendCodeView.setText("重新获取");
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_verify, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContractServiceView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mContractServiceView.getPaint().setAntiAlias(true);

        //  显示手机号
        if (!TextUtils.isEmpty(mMobilePhone)) {
            char[] tmpPhone = mMobilePhone.toCharArray();
            for (int i = 3; i < 7; i++) {
                tmpPhone[i] = '*';
            }
            mPhoneView.setText(new String(tmpPhone));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startTimer();
        mDisposable = RxBus2.Companion.get().toObservable().subscribe(o -> {
            if (RegisterOkDialog.BUS_REGISTER_OK.equals(o)) {
                Timber.e("注册成功，关闭登录");
                getActivity().finish();
            }
        });
        RxBus.getDefaultBus().toObserverable().compose(bindToLifecycle()).subscribe(o -> {
            if (RegisterVerifyPresenter.BUS_CODE_SENT.equals(o)) {
                startTimer();
            }
        });
    }

    /**
     * 开启倒计时
     */
    private void startTimer() {
        if (mTimer != null) {
            mTimer.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void showDialog() {
        if (!mLoadingDialog.isVisible()) {
            mLoadingDialog.show(getFragmentManager(), "RegisterVerifyFragmentshowDialog");
        }
    }

    @Override
    public void dismissDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void registerSuccess() {
        RegisterOkDialog.newInstance("", "").show(getFragmentManager(), "registerSuccess");
    }


    /**
     * 完成按钮的动画
     * @param mMv
     */
    public void scaleX(View mMv)
    {
        float values=180f;
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;

        int screenHeigh = dm.heightPixels;

        Timber.e(screenWidth+"screenWidth"+"==screenHeigh==="+screenHeigh);
        //1080screenWidth==screenHeigh===1794   6.0的测试机
        if(screenWidth>=1080||screenHeigh>=1794){
            values= 180f;
        }else{
            values=160f;
        }


        ObjectAnimator objectAnimatorTranslate3 = ObjectAnimator.ofFloat(mMv, "translationY", 0f, values);
        objectAnimatorTranslate3.setDuration(500);
        objectAnimatorTranslate3.start();
        objectAnimatorTranslate3.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });
    }

}
