package com.cardvlaue.sys.shopadd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 添加店铺
 */
public class ShopAddActivity extends BaseActivity {

    @Inject
    ShopAddPresenter mShopAddPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_white);

        ShopAddFragment addFragment = (ShopAddFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (addFragment == null) {
            addFragment = ShopAddFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), addFragment, R.id.contentFrame);
        }

        DaggerShopAddComponent.builder()
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .shopAddPresenterModule(new ShopAddPresenterModule(this, addFragment))
            .build().inject(this);
    }

}
