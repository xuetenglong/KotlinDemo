package com.cardvlaue.sys.newalipayverify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * <p>验证支付宝<p/> Created by cardvalue on 2016/6/27.
 */
public class NewAlipayVerifyActivity extends BaseActivity {

    @Inject
    NewAlipayVerifyPresenter mNewAlipayVerifyPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_white);

        NewAlipayVerifyFragment verifyFragment = (NewAlipayVerifyFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (verifyFragment == null) {
            verifyFragment = NewAlipayVerifyFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), verifyFragment,
                    R.id.contentFrame);
        }

        DaggerNewAlipayVerifyComponent.builder()
            .newAlipayVerifyPresenterModule(
                new NewAlipayVerifyPresenterModule(verifyFragment, this))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);


    }

}
