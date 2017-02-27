package com.cardvlaue.sys.financeway;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 算算融资额度
 */
public class FinanceWayActivity extends BaseActivity {

    @Inject
    FinanceWayPresenter mFinanceWayPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_finance_way);

        FinanceWayFragment wayFragment = (FinanceWayFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (wayFragment == null) {
            wayFragment = FinanceWayFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), wayFragment, R.id.contentFrame);
        }

        DaggerFinanceWayComponent.builder()
            .financeWayPresenterModule(new FinanceWayPresenterModule(this, wayFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }

}
