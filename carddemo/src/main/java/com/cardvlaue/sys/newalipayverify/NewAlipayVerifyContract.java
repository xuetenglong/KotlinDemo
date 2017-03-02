package com.cardvlaue.sys.newalipayverify;

import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;

/**
 * Created by cardvalue on 2016/6/27.
 */
interface NewAlipayVerifyContract {

    interface View extends RxBaseView<Presenter> {

        void closeDialog();

        void showLoadingDialog();

        void flag(String url);

        void Status(String status);

        void failure();

    }

    interface Presenter extends RxBasePresenter {

        void clickCommit();

        void getalipayStatus();
/*
        void clickCommit(@NonNull String phone, @NonNull String pwd, String code);

        *//**
         * 发送验证码
         *//*
        void resendCode();*/
    }
}
