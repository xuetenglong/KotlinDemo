package com.cardvlaue.sys;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the Context dependency to the {@link
 * com.cardvlaue.sys.data.source.TasksRepositoryComponent}.
 */
@Module
public final class ApplicationModule {

    private final Context mContext;

    public ApplicationModule(Context mContext) {
        this.mContext = mContext;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
