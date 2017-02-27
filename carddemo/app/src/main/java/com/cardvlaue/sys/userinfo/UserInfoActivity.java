package com.cardvlaue.sys.userinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 完善个人信息
 */
public class UserInfoActivity extends BaseActivity {

    @Inject
    UserInfoPresenter mUserInfoPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_white);

        UserInfoFragment infoFragment = (UserInfoFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (infoFragment == null) {
            infoFragment = UserInfoFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), infoFragment,
                    R.id.contentFrame);
        }

        DaggerUserInfoComponent.builder()
            .userInfoPresenterModule(new UserInfoPresenterModule(this, infoFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }
}
