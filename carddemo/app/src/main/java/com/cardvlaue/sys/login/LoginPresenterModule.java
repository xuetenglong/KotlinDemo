package com.cardvlaue.sys.login;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class LoginPresenterModule {

    private final Context mContext;

    private final LoginContract.View mView;

    LoginPresenterModule(Context context, LoginContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    LoginContract.View provideLoginContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
