package com.cardvlaue.sys.userdatails;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.applyinfo.AppSubmitActivity;
import com.cardvlaue.sys.changepassword.ChangePasswordActivity;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.main.MainFragment;
import com.cardvlaue.sys.my.MyFragment;
import com.cardvlaue.sys.util.RxBus2;

public class UserDetailsFragment extends Fragment implements UserDetailsContract.View {

    /**
     * 后退键
     */
    @BindView(R.id.ibtn_white_back)
    ImageButton mBackView;
    @BindView(R.id.rl_white_content)
    RelativeLayout mTitleContentView;
    @BindView(R.id.tv_white_back)
    TextView mTitleTextView;
    /**
     * 姓名
     */
    @BindView(R.id.ll_user_details_name)
    TextView mNameView;
    /**
     * 手机号
     */
    @BindView(R.id.ll_user_details_phone)
    TextView mPhoneView;
    /**
     * 身份证
     */
    @BindView(R.id.ll_user_details_id)
    TextView mIdView;
    private UserDetailsContract.Presenter mPresenter;
    private ContentLoadingDialog mOutingDialog;

    public static UserDetailsFragment newInstance() {
        return new UserDetailsFragment();
    }

    @Override
    public void showLoadingDialog() {
        mOutingDialog.show(getFragmentManager(), "UserDetailsFragment_showLoadingDialog");
    }

    @Override
    public void dismissLoadingDialog() {
        if (mOutingDialog != null) {
            mOutingDialog.dismiss();
        }
    }

    @Override
    public void initData(String name, String phone, String id) {
        if (!TextUtils.isEmpty(name)) {
            mNameView.setText(name);
        }
        if (!TextUtils.isEmpty(phone)) {
            mPhoneView.setText(phone);
        }
        if (!TextUtils.isEmpty(id)) {
            mIdView.setText(id);
        }
    }

    /**
     * 修改密码
     */
    @OnClick(R.id.ll_user_details_forgot_pwd)
    void clickChangePwd() {
        startActivity(new Intent(getContext(), ChangePasswordActivity.class));
    }

    /**
     * 点击退出
     */
    @OnClick(R.id.btn_user_detail_logout)
    public void clickLogout() {
        UserDetailsDialog outDialog = UserDetailsDialog.newInstance(1, "退出后您将不能查看相关信息，确定要退出当前账号吗？");
        outDialog.setOnItemClickListener(position -> {
            mPresenter.userLogout();
        });
        outDialog.show(getFragmentManager(), "clickLogout");
    }

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        getActivity().finish();
    }

    @Override
    public void clickOut() {
        RxBus2.Companion.get().send(MyFragment.BUS_INIT_DATA);

        // RxBus2.Companion.get().send(MainFragment.TAB_APPLY);
        // startActivity(new Intent(getContext(), MainActivity.class));

        startActivity(new Intent(getContext(), MainActivity.class).putExtra("apply", "UserDetails"));
        getActivity().finish();

    }

    @Override
    public void setPresenter(@NonNull UserDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOutingDialog = ContentLoadingDialog.newInstance("退出中...");
        mOutingDialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleContentView
            .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_blue));
        mBackView.setImageResource(R.mipmap.icon_back);
        mTitleTextView.setText(getString(R.string.user_details_name));
        mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.subscribe();
    }

}
