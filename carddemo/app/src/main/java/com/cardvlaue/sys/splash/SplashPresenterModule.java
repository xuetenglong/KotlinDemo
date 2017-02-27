package com.cardvlaue.sys.splash;

import android.content.Context;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the {@link
 * SplashPresenter}.
 */
@Module
class SplashPresenterModule {

    private final SplashContract.View mView;

    private final Context mContext;

    SplashPresenterModule(SplashContract.View view, Context context) {
        mView = view;
        mContext = context;
    }

    @Provides
    SplashContract.View provideSplashContractView() {
        return mView;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    LocationClient provideLocationClient() {
        LocationClient locationClient = new LocationClient(mContext);
        LocationClientOption bdOption = new LocationClientOption();
        bdOption.setOpenGps(true);
        locationClient.setLocOption(bdOption);
        return locationClient;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

}
