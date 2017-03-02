package com.cardvlaue.sys.forgotpwd;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = ForgotPwdPresenterModule.class)
interface ForgotPwdComponent {

    void inject(ForgotPwdActivity forgotPwdActivity);
}
