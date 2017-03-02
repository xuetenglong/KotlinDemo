package com.cardvlaue.sys.cardverify;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import com.cardvlaue.sys.dialog.CodeObtainDialog;
import com.cardvlaue.sys.uploadphoto.TipConstant;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by Administrator on 2016/7/4.
 */
class CardPrivatePresenterImpl implements CardPrivatePresenter {

    public static final String TAG = "CardPrivatePresenterImpl";
    public static final String BUS_CODE = "CardPrivate_BUS_CODE";
    private CardPrivateView regAndForgotView;
    private PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    CardPrivatePresenterImpl(CardPrivateView view) {
        regAndForgotView = view;
    }

    @Override
    public void getSmsCode(Context context, String phone, String type, FragmentManager manager) {
        long userPhoneNumber;
        try {
            userPhoneNumber = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            regAndForgotView.fail("非法手机号");
            return;
        }

        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(86);
        phoneNumber.setNationalNumber(userPhoneNumber);

        if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
            regAndForgotView.fail(TipConstant.PHONE_NOT_NULL);
            return;
        }
        if (type.equals(CardPrivateActivity.TYPE_PRIVATE)) {
            CodeObtainDialog.newInstance(phone, BUS_CODE).show(manager, TAG);
        } else if (type.equals(CardPrivateActivity.TYPE_PUBLIC)) {
            CodeObtainDialog.newInstance(phone, BUS_CODE).show(manager, TAG);
        }
    }

  /* @Override
    public void cardAccount(Context context, MerchantsByMobilePhoneBO bo) {
        long userPhoneNumber;
        try {
            userPhoneNumber = Long.parseLong(bo.getMobilePhone());
        } catch (NumberFormatException e) {
            regAndForgotView.fail("非法手机号");
            return;
        }

        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(86);
        phoneNumber.setNationalNumber(userPhoneNumber);

        if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
            regAndForgotView.fail("非法手机号");
            return;
        }
        if (TextUtils.isEmpty(bo.getMobilePhoneVerifyCode())) {
            return;
        }
        if (!CheckUtil.isValidPwd(bo.getPassword())) {
            return;
        }
        if (!regAndForgotView.isCheckedAgree()) {
            return;
        }
       if (isLoading) {
            return;
        }

        isLoading = true;
            createAuth(context, bo);
            merchantsByMobilePhone(context, bo);*//**//*
        }
    }*/

/*    public void merchantsByMobilePhone(final Context context, final MerchantsByMobilePhoneBO bo) {
        regAndForgotRest.merchantsByMobilePhone(bo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MerchantsByMobilePhoneBO>() {
                    @Override
                    public void onCompleted() {
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                    }

                    @Override
                    public void onNext(MerchantsByMobilePhoneBO merchantsByMobilePhoneBO) {
                        if (!merchantsByMobilePhoneBO.requestSuccess()) {
                            regAndForgotView.fail(merchantsByMobilePhoneBO.getError());
                        }*//* else if (!TextUtils.isEmpty(merchantsByMobilePhoneBO.getAccessToken())) {
                            switch (bo.getType()) {
                                    forgotDialog.setOnClickOkListener(new TipDialog.OnClickOk() {
                                        @Override
                                        public void ok() {
                                        }
                                    });
                                    break;
                                    break;
                            }
                        }*//*
                    }
                });
    }*/

}
