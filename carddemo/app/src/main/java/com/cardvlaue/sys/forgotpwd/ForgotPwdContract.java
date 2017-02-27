package com.cardvlaue.sys.forgotpwd;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;

public interface ForgotPwdContract {

    interface View extends RxBaseView<Presenter> {

        void showSuccessMsg(@NonNull String msg);

        void showFailMsg(@NonNull String msg);

        void finishActivity();

        void showDialog();

        void dismissDialog();

        boolean isActive();
    }

    interface Presenter extends RxBasePresenter {

        void clickCode(@NonNull String mobilePhone, FragmentManager manager);

        void clickCommit(@NonNull String mobilePhone, @NonNull String code, @NonNull String pwd);
    }
}
