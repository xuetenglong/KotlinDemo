package com.cardvlaue.sys.financeintention;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 融资意向
 */
public class FinanceIntentionActivity extends BaseActivity {

    @Inject
    FinanceIntentionPresenter mFinanceIntentionPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_white);

        FinanceIntentionFragment intentionFragment = (FinanceIntentionFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (intentionFragment == null) {
            intentionFragment = FinanceIntentionFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), intentionFragment,
                    R.id.contentFrame);
        }

        DaggerFinanceIntentionComponent.builder()
            .financeIntentionPresenterModule(
                new FinanceIntentionPresenterModule(this, intentionFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }

}
