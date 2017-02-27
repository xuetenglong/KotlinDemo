package com.cardvlaue.sys.shopselect;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class ShopSelectPresenterModule {

    private final ShopSelectContract.View mView;

    private final Context mContext;

    ShopSelectPresenterModule(Context context, ShopSelectContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    ShopSelectContract.View provideShopSelectContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
