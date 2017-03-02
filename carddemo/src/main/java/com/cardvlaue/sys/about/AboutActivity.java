package com.cardvlaue.sys.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.ApplicationModule;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 关于我们
 */
public class AboutActivity extends BaseActivity {

    @Inject
    AboutPresenter mAboutPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_blue);

        AboutFragment aboutFragment = (AboutFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (aboutFragment == null) {
            aboutFragment = AboutFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), aboutFragment,
                    R.id.contentFrame);
        }

        DaggerAboutComponent.builder().aboutPresenterModule(new AboutPresenterModule(aboutFragment))
            .applicationModule(new ApplicationModule(this))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }
}
