package com.cardvlaue.sys.main;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = MainPresenterModule.class)
interface MainComponent {

    void inject(MainActivity mainActivity);
}
