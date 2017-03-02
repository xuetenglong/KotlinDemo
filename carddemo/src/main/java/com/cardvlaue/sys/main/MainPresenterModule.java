package com.cardvlaue.sys.main;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class MainPresenterModule {

    private final Context mContext;

    private final MainContract.View mView;

    MainPresenterModule(Context context, MainContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    MainContract.View provideMainContractView() {
        return mView;
    }

}
