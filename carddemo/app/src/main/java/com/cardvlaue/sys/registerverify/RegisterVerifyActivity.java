package com.cardvlaue.sys.registerverify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 注册验证
 */
public class RegisterVerifyActivity extends BaseActivity {

    @Inject
    RegisterVerifyPresenter mRegisterVerifyPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_white);

        RegisterVerifyFragment verifyFragment = (RegisterVerifyFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (verifyFragment == null) {
            verifyFragment = RegisterVerifyFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), verifyFragment,
                    R.id.contentFrame);
        }

        DaggerRegisterVerifyComponent.builder()
            .registerVerifyPresenterModule(new RegisterVerifyPresenterModule(this, verifyFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }

}
