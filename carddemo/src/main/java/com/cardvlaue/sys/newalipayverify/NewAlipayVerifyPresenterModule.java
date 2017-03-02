package com.cardvlaue.sys.newalipayverify;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2016/11/7.
 */
@Module
class NewAlipayVerifyPresenterModule {

    private final NewAlipayVerifyContract.View mView;

    private final Context mContext;

    NewAlipayVerifyPresenterModule(NewAlipayVerifyContract.View view, Context context) {
        mView = view;
        mContext = context;
    }

    @Provides
    NewAlipayVerifyContract.View provideNewAlipayVerifyContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
