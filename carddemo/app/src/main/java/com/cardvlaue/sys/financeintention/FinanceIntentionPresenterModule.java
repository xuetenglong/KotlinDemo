package com.cardvlaue.sys.financeintention;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
class FinanceIntentionPresenterModule {

    private final FinanceIntentionContract.View mView;

    private final Context mContext;

    FinanceIntentionPresenterModule(Context context, FinanceIntentionContract.View view) {
        mContext = context;
        mView = view;
    }

    @Provides
    FinanceIntentionContract.View provideFinanceIntentionContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
