package com.cardvlaue.sys.mobileverify;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = MobileVerifyPresenterModule.class)
public interface MobileVerifyComponent {

    void inject(MobileVerifyActivity mobileVerifyActivity);
}
