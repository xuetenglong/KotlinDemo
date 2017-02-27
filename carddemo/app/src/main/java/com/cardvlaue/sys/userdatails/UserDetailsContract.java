package com.cardvlaue.sys.userdatails;

import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;

interface UserDetailsContract {

    interface View extends RxBaseView<Presenter> {

        /**
         * 退出
         */
        void clickOut();

        void showLoadingDialog();

        void dismissLoadingDialog();

        /**
         * 初始化界面
         *
         * @param name 姓名
         * @param phone 手机号
         * @param id 身份证
         */
        void initData(String name, String phone, String id);
    }

    interface Presenter extends RxBasePresenter {

        /**
         * 用户退出
         */
        void userLogout();
    }

}
