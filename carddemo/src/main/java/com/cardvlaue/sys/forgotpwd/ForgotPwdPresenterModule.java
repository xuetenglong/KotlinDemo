package com.cardvlaue.sys.forgotpwd;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class ForgotPwdPresenterModule {

    private final Context mContext;

    private final ForgotPwdContract.View mView;

    ForgotPwdPresenterModule(Context context, ForgotPwdContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    ForgotPwdContract.View provideForgotPwdContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
