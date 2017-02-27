package com.cardvlaue.sys.userdatails;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class UserDetailsPresenterModule {

    private final UserDetailsContract.View mView;

    private final Context mContext;

    UserDetailsPresenterModule(Context context, UserDetailsContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    UserDetailsContract.View provideSplashContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
