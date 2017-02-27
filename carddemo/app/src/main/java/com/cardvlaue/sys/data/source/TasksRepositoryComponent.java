package com.cardvlaue.sys.data.source;

import com.cardvlaue.sys.ApplicationModule;
import com.cardvlaue.sys.CVApplication;
import dagger.Component;
import javax.inject.Singleton;

/**
 * This is a Dagger component. Refer to {@link CVApplication} for the list of Dagger components used
 * in this application. <p> Even though Dagger allows annotating a {@link Component @Component} as a
 * singleton, the code itself must ensure only one instance of the class is created. This is done in
 * {@link CVApplication}.
 */
@Singleton
@Component(modules = {TasksRepositoryModule.class, ApplicationModule.class})
public interface TasksRepositoryComponent {

    TasksRepository getTasksRepository();
}
