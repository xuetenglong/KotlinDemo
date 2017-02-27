package com.cardvlaue.sys.userdatails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 个人信息
 */
public class UserDetailsActivity extends BaseActivity {

    @Inject
    UserDetailsPresenter mUserDetailsPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_blue);

        UserDetailsFragment detailsFragment = (UserDetailsFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (detailsFragment == null) {
            detailsFragment = UserDetailsFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), detailsFragment,
                    R.id.contentFrame);
        }

        DaggerUserDetailsComponent.builder()
            .userDetailsPresenterModule(new UserDetailsPresenterModule(this, detailsFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }
}
