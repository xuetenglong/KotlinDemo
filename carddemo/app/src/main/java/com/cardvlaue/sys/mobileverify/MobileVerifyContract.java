package com.cardvlaue.sys.mobileverify;

import android.content.Context;
import android.support.annotation.NonNull;
import com.cardvlaue.sys.BasePresenter;
import com.cardvlaue.sys.BaseView;

public interface MobileVerifyContract {

    interface View extends BaseView<Presenter> {

        void showSuccessMsg(@NonNull String msg);

        void showFailureMsg(@NonNull String msg);

        void showVerifyCode();

        void showqueryPwd();

        void colseActivity();
    }

    interface Presenter extends BasePresenter {

        void clickCommit(@NonNull Context context, @NonNull String phone, @NonNull String pwd,
            String code);
    }
}
