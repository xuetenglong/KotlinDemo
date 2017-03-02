package com.cardvlaue.sys.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 启动页
 */
public class SplashActivity extends BaseActivity {

    @Inject
    SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        SplashFragment splashFragment = (SplashFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (splashFragment == null) {
            splashFragment = SplashFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), splashFragment,
                    R.id.contentFrame);
        }

        DaggerSplashComponent.builder()
            .splashPresenterModule(new SplashPresenterModule(splashFragment, this))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }

}
