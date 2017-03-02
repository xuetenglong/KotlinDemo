package com.cardvlaue.sys.home;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class HomePresenterModule {

    private final Context mContext;

    private final HomeContract.View mView;

    HomePresenterModule(Context context, HomeContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    HomeContract.View provideHomeContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
