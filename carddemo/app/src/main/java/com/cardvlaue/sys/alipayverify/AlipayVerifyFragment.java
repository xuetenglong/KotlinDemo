package com.cardvlaue.sys.alipayverify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.newalipayverify.NewAlipayVerifyActivity;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.webshow.WebShowActivity;

/**
 * Created by cardvalue on 2016/6/27.
 */
public class AlipayVerifyFragment extends Fragment implements AlipayVerifyContract.View {

    private static final long ALL_TIME = 120000;//倒计时总时间
    private static final long STEP_TIME = 1000;//间隔时间
    private AlipayVerifyContract.Presenter mPresenter;
    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView, mResendCode, mTitleRight;
    private EditText mUsername, mPassword, mCode;
    private TextView mCommitView;
    private LinearLayout ly_code;
    /**
     * 加载框
     */
    private ContentLoadingDialog mLoadingDialog;
    private Handler timeHandler;
    private CountDownTimer mTimer;

    public static AlipayVerifyFragment newInstance() {
        return new AlipayVerifyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alipay_verify, container, false);
        mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
        mLoadingDialog.setCancelable(false);
        mToolbarView = (Toolbar) root.findViewById(R.id.title_default_toolbar);
        mToolbarView.setBackgroundResource(R.color.white);
        mBackView = (TextView) root.findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) root.findViewById(R.id.title_default_middle);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mTitleRight = (TextView) root.findViewById(R.id.title_default_right);
        mTitleRight.setTextColor(ContextCompat.getColor(getContext(), R.color.app_main_color));
        mTitleRight.setText("扫二维码登录");
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText("验证支付宝");
        mBackView.setOnClickListener(view -> getActivity().finish());

        mUsername = (EditText) root.findViewById(R.id.et_user);
        mPassword = (EditText) root.findViewById(R.id.et_pwd);
        mCode = (EditText) root.findViewById(R.id.et_img_code);
        mCommitView = (TextView) root.findViewById(R.id.btn_submit);
        mResendCode = (TextView) root.findViewById(R.id.resendCode);
        ly_code = (LinearLayout) root.findViewById(R.id.ly_code);

        mTitleRight.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), NewAlipayVerifyActivity.class));
            getActivity().finish();
        });

        root.findViewById(R.id.tv_credit_reports_how).setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(WebShowActivity.EXTRA_TITLE, "支付宝授权");
            intent.putExtra(WebShowActivity.EXTRA_URL, UrlConstants.ALIPAY_PAYAGREEMENT);
            intent.setClass(getActivity(), WebShowActivity.class);
            startActivity(intent);
        });

        timeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 3) {
                    getActivity().finish();
                }
            }
        };

        mTimer = new CountDownTimer(ALL_TIME, STEP_TIME) {

            @Override
            public void onTick(long millisUntilFinished) {
                mResendCode.setEnabled(false);
                mResendCode.setText("验证码(" + millisUntilFinished / 1000 + ")");
            }

            @Override
            public void onFinish() {
                mResendCode.setEnabled(true);
                mResendCode.setText("获取验证码");
            }
        };
        return root;
    }

    @Override
    public void setPresenter(AlipayVerifyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCommitView.setOnClickListener(view1 ->
            mPresenter.clickCommit(mUsername.getText().toString().trim(),
                mPassword.getText().toString().trim(),
                mCode.getText().toString().trim()));

        mResendCode.setOnClickListener(view1 -> {
            mPresenter.resendCode();
            mTimer.start();
        });
    }

    @Override
    public void colseDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void codevisible() {
        ly_code.setVisibility(View.VISIBLE);
        mTimer.start();
    }

    @Override
    public void colseActivity() {
        ToastUtil.showSuccess(getContext(), "支付宝登录成功");
        timeHandler.sendEmptyMessageDelayed(3, 2000);
    }

    @Override
    public void showLoadingDialog() {
        if (!mLoadingDialog.isAdded()) {
            mLoadingDialog.show(getFragmentManager(), "showLoadingDialog");
        }
    }

    @Override
    public void resendCode() {
        mTimer.cancel();
        //mResendCode.setEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }
}
