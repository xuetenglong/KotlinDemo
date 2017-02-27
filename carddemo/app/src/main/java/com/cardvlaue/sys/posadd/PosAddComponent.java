package com.cardvlaue.sys.posadd;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = PosAddPresenterModule.class)
interface PosAddComponent {

    void inject(PosAddActivity posAddActivity);
}
