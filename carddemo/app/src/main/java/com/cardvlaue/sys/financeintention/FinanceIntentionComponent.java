package com.cardvlaue.sys.financeintention;

import com.cardvlaue.sys.data.source.TasksRepositoryComponent;
import com.cardvlaue.sys.util.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = FinanceIntentionPresenterModule.class)
interface FinanceIntentionComponent {

    void inject(FinanceIntentionActivity activity);

}
