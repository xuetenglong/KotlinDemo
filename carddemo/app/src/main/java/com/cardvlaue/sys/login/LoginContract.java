package com.cardvlaue.sys.login;

import android.support.v4.app.FragmentManager;
import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;

/**
 * 登录契约类
 */
interface LoginContract {

    interface View extends RxBaseView<Presenter> {

        void initMobilePhone(String phone);

        /**
         * 登录成功
         */
        void loginSuccess();

        /**
         * 是否勾选服务协议
         *
         * @return true：已勾选 false：未勾选
         */
        boolean checkAgreementBox();

        /**
         * 服务协议是否授权
         *
         * @return true：已授权 false 未授权
         */
        boolean checkAgreementShown();

        /**
         * 服务协议选项是否可见
         *
         * @param state 显示状态
         */
        void changeAgreementShown(int state);

        /**
         * 显示进度对话框
         */
        void showLoadingDialog();

        /**
         * 隐藏进度对话框
         */
        void dismissLoadingDialog();
    }

    interface Presenter extends RxBasePresenter {

        void clickCommit(String mobilePhone, String pwd, FragmentManager manager, boolean type);

        /**
         * 检查用户是否已接受服务协议
         *
         * @param mobilePhone 手机号
         */
        void verifyMobilePhoneAuthorize(String mobilePhone, boolean type);
    }

}
