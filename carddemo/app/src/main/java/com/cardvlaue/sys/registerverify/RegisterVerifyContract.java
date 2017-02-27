package com.cardvlaue.sys.registerverify;

import android.support.v4.app.FragmentManager;
import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;

interface RegisterVerifyContract {

    interface View extends RxBaseView<Presenter> {

        void showDialog();

        void dismissDialog();

        /**
         * 注册成功
         */
        void registerSuccess();
    }

    interface Presenter extends RxBasePresenter {

        /**
         * 重新获取短信验证码
         *
         * @param mobilePhone 手机号
         * @param fm 显示对话框用
         */
        void clickReSendCode(String mobilePhone, FragmentManager fm);

        /**
         * 点击注册
         *
         * @param mobilePhone 手机号
         * @param pwd 密码
         * @param verifyCode 短信验证码
         * @param inviteCode 邀请码
         */
        void clickCommit(String mobilePhone, String pwd, String verifyCode,
            final String inviteCode);
    }

}
