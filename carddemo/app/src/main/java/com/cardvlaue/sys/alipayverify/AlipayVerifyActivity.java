package com.cardvlaue.sys.alipayverify;

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
public class AlipayVerifyActivity extends BaseActivity {

    @Inject
    AlipayVerifyPresenter mAlipayVerifyPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_white);

        AlipayVerifyFragment verifyFragment = (AlipayVerifyFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (verifyFragment == null) {
            verifyFragment = AlipayVerifyFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), verifyFragment,
                    R.id.contentFrame);
        }

        DaggerAlipayVerifyComponent.builder()
            .alipayVerifyPresenterModule(new AlipayVerifyPresenterModule(verifyFragment, this))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }

}
