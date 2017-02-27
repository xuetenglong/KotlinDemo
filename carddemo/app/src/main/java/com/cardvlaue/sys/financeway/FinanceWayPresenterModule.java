package com.cardvlaue.sys.financeway;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class FinanceWayPresenterModule {

    private final Context mContext;

    private final FinanceWayContract.View mView;

    FinanceWayPresenterModule(Context context, FinanceWayContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    FinanceWayContract.View provideFinanceWayContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
