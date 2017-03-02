package com.cardvlaue.sys.userinfo;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = UserInfoPresenterModule.class)
interface UserInfoComponent {

    void inject(UserInfoActivity activity);

}
