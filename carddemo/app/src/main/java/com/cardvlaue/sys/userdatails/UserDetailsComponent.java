package com.cardvlaue.sys.userdatails;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = UserDetailsPresenterModule.class)
public interface UserDetailsComponent {

    void inject(UserDetailsActivity userDetailsActivity);
}
