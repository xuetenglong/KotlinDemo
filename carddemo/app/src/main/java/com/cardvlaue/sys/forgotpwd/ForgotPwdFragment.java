package com.cardvlaue.sys.forgotpwd;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.disposables.Disposable;

public class ForgotPwdFragment extends Fragment implements ForgotPwdContract.View {

    public static final String INPUT_PHONE = "INPUT_PHONE";
    @BindView(R.id.rl_white_content)
    RelativeLayout mTitleContentView;
    @BindView(R.id.ibtn_white_back)
    ImageButton mBackView;
    @BindView(R.id.tv_white_back)
    TextView mTitleTextView;
    @BindView(R.id.et_forgot_pwd_mobile_phone)
    EditText mMobilePhoneView;
    @BindView(R.id.et_forgot_pwd_code)
    EditText mInputCodeView;
    @BindView(R.id.et_forgot_pwd_new)
    EditText mPwdView;
    @BindView(R.id.btn_forgot_pwd_code)
    Button mObtainCodeView;
    private ForgotPwdContract.Presenter mPresenter;
    private ContentLoadingDialog mLoadingDialog;

    private CountDownTimer mTimer;

    private Disposable mDisposable;

    /**
     * 登录界面输入的手机号
     */
    private String loginInputPhone;

    public static ForgotPwdFragment newInstance() {
        return new ForgotPwdFragment();
    }

    @OnClick(R.id.btn_forgot_pwd_code)
    void clickObtainCode() {
        mPresenter.clickCode(mMobilePhoneView.getText().toString(), getFragmentManager());
    }

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        getActivity().finish();
    }

    @Override
    public void setPresenter(@NonNull ForgotPwdContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginInputPhone = getActivity().getIntent().getStringExtra(INPUT_PHONE);

        mLoadingDialog = ContentLoadingDialog.newInstance("密码修改中...");
        mLoadingDialog.setCancelable(false);

        mTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                if (mObtainCodeView != null) {
                    mObtainCodeView.setEnabled(false);

                    String tickText = "验证码(" + l / 1000 + ")";
                    mObtainCodeView.setText(tickText);
                }
            }

            @Override
            public void onFinish() {
                if (mObtainCodeView != null && getActivity() != null && !getActivity()
                    .isFinishing()) {
                    mObtainCodeView.setEnabled(true);

                    mObtainCodeView.setText(getString(R.string.forgot_pwd_obtain_code));
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_pwd, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleContentView
            .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_main_color));
        mBackView.setImageResource(R.mipmap.icon_back);
        mTitleTextView.setTextColor(Color.WHITE);
        mTitleTextView.setText(R.string.forgot_pwd_name);
        if (!TextUtils.isEmpty(loginInputPhone)) {
            mMobilePhoneView.setText(loginInputPhone);
            mMobilePhoneView.setSelection(mMobilePhoneView.getText().length());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDisposable = RxBus2.Companion.get().toObservable().subscribe(o -> {
            if (ForgotPwdPresenter.BUS_CODE.equals(o)) {
                if (mTimer != null) {
                    mTimer.start();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mTimer != null) {
            mTimer.cancel();
        }

        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        mPresenter.unsubscribe();
    }

    @Override
    public void showSuccessMsg(@NonNull String msg) {
        ToastUtil.showSuccess(getContext(), msg);
    }

    @Override
    public void showFailMsg(@NonNull String msg) {
        ToastUtil.showFailure(getContext(), msg);
    }

    @Override
    public void finishActivity() {
        getActivity().finish();
    }

    @Override
    public void showDialog() {
        mLoadingDialog.show(getFragmentManager(), "showDialog");
    }

    @Override
    public void dismissDialog() {
        if (mLoadingDialog.isVisible()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @OnClick(R.id.btn_forgot_pwd_commit)
    void clickCommit() {
        mPresenter
            .clickCommit(mMobilePhoneView.getText().toString(), mInputCodeView.getText().toString(),
                mPwdView.getText().toString());
    }
}
