package com.cardvlaue.sys.login;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.forgotpwd.ForgotPwdActivity;
import com.cardvlaue.sys.forgotpwd.ForgotPwdFragment;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.registerverify.RegisterOkDialog;
import com.cardvlaue.sys.registerverify.RegisterVerifyActivity;
import com.cardvlaue.sys.registerverify.RegisterVerifyFragment;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.webshow.WebShowActivity;
import com.trello.rxlifecycle.components.support.RxFragment;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class LoginFragment extends RxFragment implements LoginContract.View {

    /**
     * 手机号输入框
     */
    @BindView(R.id.et_login_mobilePhone)
    EditText mMobilePhoneView;
    /**
     * 密码输入框
     */
    @BindView(R.id.et_login_password)
    EditText mPasswordView;
    /**
     * 注册按钮上方可点击的文本
     */
    @BindView(R.id.tv_login_forgot_pwd)
    TextView mForgotPwdView;
    /**
     * 注册按钮上方不可点击的文本
     */
    @BindView(R.id.tv_login_forgot_hint)
    TextView mForgotHintView;
    /**
     * 默认登录按钮
     */
    @BindView(R.id.btn_login_commit)
    Button mCommitView;
    /**
     * 登录/注册切换
     */
    @BindView(R.id.tv_login_change_type)
    TextView mChangeView;
    /**
     * 密码是否可见 View
     */
    @BindView(R.id.ib_login_pwd_shown)
    ImageButton mPwdShownView;
    /**
     * 是否需要确认服务协议
     */
    @BindView(R.id.ll_login_agreement)
    LinearLayout mAgreementGroupView;
    /**
     * 服务协议勾选框
     */
    @BindView(R.id.ckb_login_agreement)
    CheckBox mCheckAgreementView;
    private LoginContract.Presenter mPresenter;
    private ContentLoadingDialog mLoadingDialog;

    private boolean isFirstOpen = true;
    private String phoneTmp;
    /**
     * <p>界面类型<p/> false：登录 true：注册
     */
    private boolean faceType;
    /**
     * false：不可见 true：可见
     */
    private boolean pwdShownState;
    private Disposable mDisposable;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @OnClick(R.id.ib_login_back)
    void clickBack() {
        if (MainActivity.isStart() == 0) {
            startActivity(new Intent(getContext(), MainActivity.class));
        }
        getActivity().finish();
    }

    @Override
    public void initMobilePhone(String phone) {
        phoneTmp = phone;
        mMobilePhoneView.setText(phoneTmp);
    }

    @Override
    public void loginSuccess() {
        ToastUtil.showSuccess(getContext(), "登录成功");
        getActivity().finish();
        startActivity(new Intent(getContext(), MainActivity.class));
    }

    /**
     * 事件提交
     */
    @OnClick(R.id.btn_login_commit)
    void clickCommit() {
        String phoneStr = mMobilePhoneView.getText().toString().trim();
        String pwdStr = mPasswordView.getText().toString().trim();
        mPresenter.clickCommit(phoneStr, pwdStr, getFragmentManager(), faceType);
        if (!faceType) {
            mPasswordView.getText().clear();
        }
    }

    @OnClick(R.id.tv_login_forgot_pwd)
    void clickUnderlineText() {
        if (faceType) {
            // 查看注册协议
            startActivity(new Intent(getContext(), WebShowActivity.class)
                .putExtra(WebShowActivity.EXTRA_TITLE, getString(R.string.forgot_pwd_agreement))
                .putExtra(WebShowActivity.EXTRA_URL, UrlConstants.USER_AGREEMENT));
        } else {
            // 忘记密码
            startActivity(new Intent(getContext(), ForgotPwdActivity.class)
                .putExtra(ForgotPwdFragment.INPUT_PHONE, mMobilePhoneView.getText().toString()));
        }
    }

    @OnClick(R.id.tv_login_change_type)
    void clickChangeType() {
        mPasswordView.getText().clear();
        if (faceType) {
            faceType = false;
            showLogin();
        } else {
            faceType = true;
            showRegister();
            Timber.e("SHOW:%s", faceType);
        }
    }

    /**
     * 切换到登录
     */
    private void showLogin() {
        mPasswordView.setHint(getString(R.string.login_pwd_hint));
        mPwdShownView.setVisibility(View.VISIBLE);
        mForgotHintView.setVisibility(View.GONE);
        mForgotPwdView.setText("忘记密码?");
        mCommitView.setText(getString(R.string.login_name));
        mChangeView.setText(getString(R.string.register_name));
        mMobilePhoneView.setText(phoneTmp);
    }

    /**
     * 切换到注册
     */
    private void showRegister() {
        changeAgreementShown(View.GONE);
        mCheckAgreementView.setChecked(true);
        mPasswordView.setHint("6-20字符不能全数字，不含空格");
        mPwdShownView.setVisibility(View.GONE);
        mForgotHintView.setVisibility(View.VISIBLE);
        mForgotPwdView.setText("《卡得万利注册协议》");
        mCommitView.setText(getString(R.string.register_name));
        mChangeView.setText(getString(R.string.login_name));
        mMobilePhoneView.getText().clear();
    }

    @OnClick(R.id.tv_login_service_text)
    void clickServiceText() {
        startActivity(new Intent(getContext(), WebShowActivity.class)
            .putExtra(WebShowActivity.EXTRA_TITLE, getString(R.string.forgot_pwd_agreement))
            .putExtra(WebShowActivity.EXTRA_URL, UrlConstants.USER_AGREEMENT));
    }

    @OnClick(R.id.ib_login_pwd_shown)
    void clickPwdShown() {
        if (pwdShownState) {
            pwdShownState = false;
//            PasswordTransformationMethod.getInstance()
            mPasswordView
                .setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            mPasswordView.setSelection(mPasswordView.getText().length());
            mPwdShownView.setImageResource(R.mipmap.icon_pwd_invisible);
            mPasswordView.setSelection(mPasswordView.getText().toString().trim().length());
        } else {
            pwdShownState = true;
//            HideReturnsTransformationMethod.getInstance()
            mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mPasswordView.setSelection(mPasswordView.getText().length());
            mPwdShownView.setImageResource(R.mipmap.icon_pwd_visible);
            mPasswordView.setSelection(mPasswordView.getText().toString().trim().length());
        }
    }

    @Override
    public boolean checkAgreementBox() {
        return mCheckAgreementView.isChecked();
    }

    @Override
    public boolean checkAgreementShown() {
        return mAgreementGroupView.isShown();
    }

    @Override
    public void changeAgreementShown(int state) {
        mAgreementGroupView.setVisibility(state);
    }

    @OnTextChanged(R.id.et_login_mobilePhone)
    void changePhone(CharSequence charSequence) {
        mPresenter.verifyMobilePhoneAuthorize(charSequence.toString(), faceType);
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show(getFragmentManager(), "showLoadingDialog");
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void setPresenter(@NonNull LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoadingDialog = ContentLoadingDialog.newInstance("登录中...");
        mLoadingDialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 给控件添加下划线
        mForgotPwdView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mForgotPwdView.getPaint().setAntiAlias(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.e("onActivityCreated-login");
        mDisposable = RxBus2.Companion.get().toObservable().subscribe(o -> {
            if (RegisterOkDialog.BUS_REGISTER_OK.equals(o)) {
                Timber.e("注册成功，关闭登录");
                getActivity().finish();
            }
        });

        RxBus.getDefaultBus().toObserverable().compose(bindToLifecycle()).subscribe(o -> {
            if (LoginPresenter.BUS_CODE_SENT.equals(o)) {
                // 短信验证码已发送
                String phoneStr = mMobilePhoneView.getText().toString();
                String pwdStr = mPasswordView.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneStr) && !TextUtils.isEmpty(pwdStr)) {
                    startActivity(new Intent(getContext(), RegisterVerifyActivity.class)
                        .putExtra(RegisterVerifyFragment.EXTRA_MOBILE_PHONE,
                            mMobilePhoneView.getText().toString())
                        .putExtra(RegisterVerifyFragment.EXTRA_PASSWORD, pwdStr));
                    mPasswordView.getText().clear();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFirstOpen) {
            isFirstOpen = false;
            mPresenter.subscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

}
