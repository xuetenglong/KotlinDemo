package com.cardvlaue.sys.cardverify;

import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/4.
 */
public class CheckingForm {

    //错误
    public static String LastError = "";
    Pattern p = null;
    Matcher m = null;


    //校验修改密码页面的数据是否正常
    public static boolean checkForChangepwdForm(String oldPwd, String str1, String str2) {
        if (oldPwd == null || oldPwd.equals("")) {
            LastError = "旧密码不能为空";
            return false;
        } else if (str1 == null || str1.equals("") || str2 == null || str2.equals("")) {
            LastError = "新密码不能为空";
            return false;
        } else if (!str1.equals(str2)) {
            LastError = "两次输入的密码不相同";
            return false;
        } else if (str1.equals(str2) && oldPwd.equals(str1)) {
            LastError = "旧密码不能与新密码一致";
            return false;
        }
        return true;
    }

    public static boolean checkForMobile(String phone, String pwd) {
        boolean mPhone = CheckingTools.isMobile(phone);
        if (TextUtils.isEmpty(phone)) {
            LastError = "手机号不能为空";
            return false;
        } else if (!mPhone) {
            LastError = "手机号格式不正确";
            return false;
        } else if (TextUtils.isEmpty(pwd)) {
            LastError = "服务密码不能为空";
            return false;
        }
        return true;
    }


    /**
     * 对私的银行卡  第一步非空处理 checkForBankPrivate
     */
    public static boolean checkForBankPrivate(String directDebitBankName,
        String directDebitAcctNo, String directDebitAcctPhone,
        String mobilePhoneVerifyCode) {
        //  boolean misAuthCard=CheckingTools.isAuthCard(directDebitAcctId);
        boolean mBankPrivate = CheckingTools.BankPrivate(directDebitAcctNo);
        boolean mPhone = CheckingTools.isMobile(directDebitAcctPhone);
        if (TextUtils.isEmpty(directDebitBankName)) {
            LastError = "开户银行不能为空";
            return false;
        } else if (TextUtils.isEmpty(directDebitAcctNo)) {
            LastError = "银行账号不能为空";
            return false;
        } else if (!mBankPrivate) {
            LastError = "银行账号不正确";
            return false;
        } else if (TextUtils.isEmpty(directDebitAcctPhone)) {
            LastError = "银行预留手机号不能为空";
            return false;
        } else if (!mPhone) {
            LastError = "银行预留手机号格式不正确";
            return false;
        } else if (TextUtils.isEmpty(mobilePhoneVerifyCode)) {
            LastError = "验证码不能为空";
            return false;
        }
        return true;

    }


    /**
     * 对公的银行卡  第一步非空处理 checkForBankPrivate
     */
    public static boolean checkForBankPublic(String msecondaryBankABA, String msecondaryBankDDA,
        String directDebitBankName,
        String directDebitAcctNo, String directDebitAcctPhone,
        String mobilePhoneVerifyCode) {
        //  boolean misAuthCard=CheckingTools.isAuthCard(directDebitAcctId);
        boolean mBankPrivate = CheckingTools.BankPrivate(directDebitAcctNo);
        boolean mPhone = CheckingTools.isMobile(directDebitAcctPhone);
        boolean mBankPublic = CheckingTools.BankPublic(msecondaryBankDDA);

        if (TextUtils.isEmpty(msecondaryBankABA)) {
            LastError = "对公开户银行不能为空";
            return false;
        } else if (TextUtils.isEmpty(msecondaryBankDDA)) {
            LastError = "对公银行账号不能为空";
            return false;
        } else if (!mBankPublic) {
            LastError = "对公银行账号不正确";
            return false;
        } else if (TextUtils.isEmpty(directDebitBankName)) {
            LastError = "放款账户对私开户银行不能为空";
            return false;
        } else if (TextUtils.isEmpty(directDebitAcctNo)) {
            LastError = "银行账号不能为空";
            return false;
        } else if (!mBankPrivate) {
            LastError = "银行账号不正确";
            return false;
        } else if (TextUtils.isEmpty(directDebitAcctPhone)) {
            LastError = "银行预留手机号不能为空";
            return false;
        } else if (!mPhone) {
            LastError = "银行预留手机号格式不正确";
            return false;
        } else if (TextUtils.isEmpty(mobilePhoneVerifyCode)) {
            LastError = "验证码不能为空";
            return false;
        }
        return true;

    }
}
