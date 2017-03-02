package com.cardvlaue.sys.shopadd;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class ShopAddPresenterModule {

    private final ShopAddContract.View mView;

    private final Context mContext;

    ShopAddPresenterModule(Context context, ShopAddContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    ShopAddContract.View provideShopAddContractView() {
        return mView;
    }

}
