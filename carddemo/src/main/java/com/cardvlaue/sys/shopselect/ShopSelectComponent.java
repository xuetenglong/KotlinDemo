package com.cardvlaue.sys.shopselect;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = ShopSelectPresenterModule.class)
interface ShopSelectComponent {

    void inject(ShopSelectActivity shopSelectActivity);
}

