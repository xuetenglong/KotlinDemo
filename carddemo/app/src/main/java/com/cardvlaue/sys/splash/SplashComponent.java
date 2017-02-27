package com.cardvlaue.sys.splash;

import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link CVApplication} for the list of Dagger components used
 * in this application. <p> Because this component depends on the {@link TasksRepositoryComponent},
 * which is a singleton, a scope must be specified. All fragment components use a custom scope for
 * this purpose.
 */
@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = SplashPresenterModule.class)
interface SplashComponent {

    void inject(SplashActivity splashActivity);
}
