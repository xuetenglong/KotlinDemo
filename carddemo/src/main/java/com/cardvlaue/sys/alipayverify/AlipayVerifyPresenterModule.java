package com.cardvlaue.sys.alipayverify;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Administrator on 2016/11/7.
 */
@Module
class AlipayVerifyPresenterModule {

    private final AlipayVerifyContract.View mView;

    private final Context mContext;

    AlipayVerifyPresenterModule(AlipayVerifyContract.View view, Context context) {
        mView = view;
        mContext = context;
    }

    @Provides
    AlipayVerifyContract.View provideAlipayVerifyContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
