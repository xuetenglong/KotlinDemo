package com.cardvlaue.sys.data.source;

import android.content.Context;
import com.cardvlaue.sys.data.source.local.TasksLocalDataSource;
import com.cardvlaue.sys.data.source.remote.TasksRemoteDataSource;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * This is used by Dagger to inject the required arguments into the {@link TasksRepository}.
 */
@Module
public class TasksRepositoryModule {

    @Singleton
    @Provides
    @Local
    TasksDataSource provideTasksLocalDataSource(Context context) {
        return new TasksLocalDataSource(context);
    }

    @Singleton
    @Provides
    @Remote
    TasksDataSource provideTasksRemoteDataSource() {
        return new TasksRemoteDataSource();
    }
}
