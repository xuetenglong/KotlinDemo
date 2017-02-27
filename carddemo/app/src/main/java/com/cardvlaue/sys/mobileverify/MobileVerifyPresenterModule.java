package com.cardvlaue.sys.mobileverify;

import dagger.Module;
import dagger.Provides;

@Module
public class MobileVerifyPresenterModule {

    private final MobileVerifyContract.View mView;

    public MobileVerifyPresenterModule(MobileVerifyContract.View view) {
        mView = view;
    }

    @Provides
    MobileVerifyContract.View provideMobileVerifyContractView() {
        return mView;
    }

}
