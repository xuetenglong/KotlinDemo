package com.cardvlaue.sys.posmanagement;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class PosManagementPresenterModule {

    private final PosManagementContract.View mView;

    private final Context mContext;

    PosManagementPresenterModule(Context context, PosManagementContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    PosManagementContract.View providePosManagementContractView() {
        return mView;
    }

}
