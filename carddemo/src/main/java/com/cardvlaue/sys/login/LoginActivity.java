package com.cardvlaue.sys.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    @Inject
    LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), loginFragment,
                    R.id.contentFrame);
        }

        DaggerLoginComponent.builder()
            .loginPresenterModule(new LoginPresenterModule(this, loginFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }

    /**
     * 后退返回首页
     */
    @Override
    public void onBackPressed() {
        if (MainActivity.isStart() == 0) {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

}
