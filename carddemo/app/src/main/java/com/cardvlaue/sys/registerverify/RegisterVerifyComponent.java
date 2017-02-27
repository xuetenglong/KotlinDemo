package com.cardvlaue.sys.registerverify;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = RegisterVerifyPresenterModule.class)
interface RegisterVerifyComponent {

    void inject(RegisterVerifyActivity activity);

}
