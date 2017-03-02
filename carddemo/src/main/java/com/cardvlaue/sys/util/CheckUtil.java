package com.cardvlaue.sys.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.regex.Pattern;

public class CheckUtil {

    /**
     * 最小意向融资金额
     */
    public static final int MIN_MONEY = 10_000;

    /**
     * 最大意向融资金额
     */
    public static final int MAX_MONEY = 3_000_000;

    /**
     * 检查营业执照注册号是否合法
     *
     * @param id 营业执照注册号
     * @return true：合法 false：非法
     */
    public static boolean isValidRizNO(String id) {
        return Pattern.matches("[a-zA-Z0-9]{13,20}$", id);
    }

    /**
     * 检查网络可用性
     *
     * @param c Context
     * @return true：可用 false：不可用
     */
    public static boolean isOnline(Context c) {
        NetworkInfo networkInfo = ((ConnectivityManager) c
            .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 验证手机号是否合法
     *
     * @param mobilePhone 被验证手机号
     * @return 验证结果
     */
    public static boolean isMobilePhone(@NonNull String mobilePhone) {
        if (TextUtils.isEmpty(mobilePhone) || !TextUtils.isDigitsOnly(mobilePhone.trim())
            || mobilePhone.trim().length() != 11) {
            return false;
        }

        long legalPhone = Long.parseLong(mobilePhone);
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(86);
        phoneNumber.setNationalNumber(legalPhone);
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        return phoneNumberUtil.isValidNumber(phoneNumber);
    }

    /**
     * 验证固定电话是否合法
     *
     * @param phone 固定电话
     * @return 验证结果
     */
    public static boolean isLandlinePhone(@NonNull String phone) {
        return Pattern.matches("(\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})" +
                "-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$",
            phone);
    }

    /**
     * 转换为合法意向融资金额
     *
     * @param inputMoney 输入金额
     * @return 转换后金额
     */
    public static long convertIntentMoney(CharSequence inputMoney) {
        if (TextUtils.isEmpty(inputMoney) || inputMoney.length() < 4 || !TextUtils
            .isDigitsOnly(inputMoney)) {
            return MIN_MONEY;
        } else if (inputMoney.length() > 9) {
            return MAX_MONEY;
        } else {
            long intMoney = Integer.parseInt(inputMoney.toString());
            if (intMoney < MIN_MONEY) {
                return MIN_MONEY;
            } else if (intMoney > MAX_MONEY) {
                return MAX_MONEY;
            } else {
                return intMoney - intMoney % 1000;
            }
        }
    }

}