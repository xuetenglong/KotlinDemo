package com.cardvlaue.sys.about;

import dagger.Module;
import dagger.Provides;

@Module
class AboutPresenterModule {

    private final AboutContract.View mView;

    AboutPresenterModule(AboutContract.View view) {
        mView = view;
    }

    @Provides
    AboutContract.View provideAboutContractView() {
        return mView;
    }

}
