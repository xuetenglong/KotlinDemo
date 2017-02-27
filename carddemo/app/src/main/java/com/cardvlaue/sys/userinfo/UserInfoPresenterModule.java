package com.cardvlaue.sys.userinfo;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class UserInfoPresenterModule {

    private final UserInfoContract.View mView;

    private final Context mContext;

    UserInfoPresenterModule(Context context, UserInfoContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    UserInfoContract.View provideUserInfoContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }
}
