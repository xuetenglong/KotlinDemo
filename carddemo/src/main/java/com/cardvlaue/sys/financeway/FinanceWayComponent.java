package com.cardvlaue.sys.financeway;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = FinanceWayPresenterModule.class)
interface FinanceWayComponent {

    void inject(FinanceWayActivity activity);
}
