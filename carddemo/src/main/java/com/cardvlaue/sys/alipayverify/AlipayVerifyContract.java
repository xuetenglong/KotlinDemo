package com.cardvlaue.sys.alipayverify;

import android.support.annotation.NonNull;
import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;

/**
 * Created by cardvalue on 2016/6/27.
 */
interface AlipayVerifyContract {

    interface View extends RxBaseView<Presenter> {

        void codevisible();

        void colseDialog();

        void colseActivity();

        void showLoadingDialog();

        void resendCode();

    }

    interface Presenter extends RxBasePresenter {

        void clickCommit(@NonNull String phone, @NonNull String pwd, String code);

        /**
         * 发送验证码
         */
        void resendCode();
    }
}
