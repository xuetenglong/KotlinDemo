package com.cardvlaue.sys.posmanagement;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = PosManagementPresenterModule.class)
interface PosManagementComponent {

    void inject(PosManagementActivity posManagementActivity);
}
