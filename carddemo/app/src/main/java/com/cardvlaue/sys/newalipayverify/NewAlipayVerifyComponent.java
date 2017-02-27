package com.cardvlaue.sys.newalipayverify;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

/**
 * Created by Administrator on 2016/11/7.
 */
@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = NewAlipayVerifyPresenterModule.class)
interface NewAlipayVerifyComponent {

    void inject(NewAlipayVerifyActivity alipayVerifyActivity);
}
