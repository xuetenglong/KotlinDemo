package com.cardvlaue.sys.shopselect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 店铺选择
 */
public class ShopSelectActivity extends BaseActivity {

    @Inject
    ShopSelectPresenter mShopSelectPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_white);

        ShopSelectFragment selectFragment = (ShopSelectFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (selectFragment == null) {
            selectFragment = ShopSelectFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), selectFragment,
                    R.id.contentFrame);
        }

        DaggerShopSelectComponent.builder()
            .shopSelectPresenterModule(new ShopSelectPresenterModule(this, selectFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }
}
