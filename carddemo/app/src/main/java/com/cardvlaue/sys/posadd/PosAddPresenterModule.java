package com.cardvlaue.sys.posadd;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class PosAddPresenterModule {

    private final PosAddContract.View mView;

    private final Context mContext;

    PosAddPresenterModule(Context context, PosAddContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    PosAddContract.View providePosAddContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
