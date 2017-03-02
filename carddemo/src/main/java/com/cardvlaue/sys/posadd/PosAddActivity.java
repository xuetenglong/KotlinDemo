package com.cardvlaue.sys.posadd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 添加商编
 */
public class PosAddActivity extends BaseActivity {

    @Inject
    PosAddPresenter mPosAddPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_white);

        PosAddFragment posAddFragment = (PosAddFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (posAddFragment == null) {
            posAddFragment = PosAddFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), posAddFragment,
                    R.id.contentFrame);
        }

        DaggerPosAddComponent.builder()
            .posAddPresenterModule(new PosAddPresenterModule(this, posAddFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }
}
