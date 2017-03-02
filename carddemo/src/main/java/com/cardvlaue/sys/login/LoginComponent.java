package com.cardvlaue.sys.login;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = LoginPresenterModule.class)
interface LoginComponent {

    void inject(LoginActivity loginActivity);

}
