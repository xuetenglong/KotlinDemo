package com.cardvlaue.sys.userinfo;

import com.cardvlaue.sys.BasePresenter;
import com.cardvlaue.sys.BaseView;
import com.cardvlaue.sys.data.UserInfoNewResponse;

interface UserInfoContract {

    interface View extends BaseView<Presenter> {

        void showLoadingDialog();

        void dismissLoadingDialog();

        void gotoNext();

        void initFaceData(UserInfoNewResponse s);
    }

    interface Presenter extends BasePresenter {

        /**
         * 检查手机号
         *
         * @param phone 手机号
         * @return true：是 false：否
         */
        boolean checkPhoneIsMe(String phone);

        void updateInfo(UserInfoNewResponse user);

        void loadUserData();
    }

}
