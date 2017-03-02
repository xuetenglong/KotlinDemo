package com.cardvlaue.sys.home;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = HomePresenterModule.class)
interface HomeComponent {

    void inject(HomeFragment homeFragment);
}
