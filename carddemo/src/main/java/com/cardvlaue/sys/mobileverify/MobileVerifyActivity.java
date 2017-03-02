package com.cardvlaue.sys.mobileverify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 手机验证 / 聚信立验证
 */
public class MobileVerifyActivity extends BaseActivity {

    @Inject
    MobileVerifyPresenter mMobileVerifyPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_white);

        MobileVerifyFragment verifyFragment = (MobileVerifyFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (verifyFragment == null) {
            verifyFragment = MobileVerifyFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), verifyFragment,
                    R.id.contentFrame);
        }

        DaggerMobileVerifyComponent.builder()
            .mobileVerifyPresenterModule(new MobileVerifyPresenterModule(verifyFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }
}
