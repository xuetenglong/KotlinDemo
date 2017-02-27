package com.cardvlaue.sys.main;

import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
interface MainContract {

    interface View extends RxBaseView<Presenter> {

    }

    interface Presenter extends RxBasePresenter {

        /**
         * 获取用户数据
         */
        void loadUserInfo();

        /**
         * 上传通讯录
         */
        void uploadContract();
    }

}
