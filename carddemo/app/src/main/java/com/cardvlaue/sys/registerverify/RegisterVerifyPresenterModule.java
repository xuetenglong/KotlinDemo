package com.cardvlaue.sys.registerverify;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class RegisterVerifyPresenterModule {

    private final RegisterVerifyContract.View mView;

    private final Context mContext;

    public RegisterVerifyPresenterModule(Context context, RegisterVerifyContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    RegisterVerifyContract.View provideSplashContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
