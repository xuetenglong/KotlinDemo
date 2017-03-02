package com.cardvlaue.sys.posmanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 商编管理
 */
public class PosManagementActivity extends BaseActivity {

    @Inject
    PosManagementPresenter mPosManagementPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_white);

        PosManagementFragment posManagementFragment = (PosManagementFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (posManagementFragment == null) {
            posManagementFragment = PosManagementFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), posManagementFragment,
                    R.id.contentFrame);
        }

        DaggerPosManagementComponent.builder()
            .posManagementPresenterModule(
                new PosManagementPresenterModule(this, posManagementFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }
}
