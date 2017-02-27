package com.cardvlaue.sys.forgotpwd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 忘记密码
 */
public class ForgotPwdActivity extends BaseActivity {

    @Inject
    ForgotPwdPresenter mForgotPwdPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_blue);

        ForgotPwdFragment pwdFragment = (ForgotPwdFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (pwdFragment == null) {
            pwdFragment = ForgotPwdFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), pwdFragment, R.id.contentFrame);
        }

        DaggerForgotPwdComponent.builder()
            .forgotPwdPresenterModule(new ForgotPwdPresenterModule(this, pwdFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }
}
