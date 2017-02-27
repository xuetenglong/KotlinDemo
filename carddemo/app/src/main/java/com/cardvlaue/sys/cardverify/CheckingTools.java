package com.cardvlaue.sys.cardverify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/4.
 */
public class CheckingTools {


    /**
     * 验证身份证
     */
    public static boolean isAuthCard(String card) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("([0-9]{17}([0-9]|X))|([0-9]{15})");
        m = p.matcher(card);
        b = m.matches();
        return b;
    }


    //Bank=;     //验证银行对私
    public static boolean BankPrivate(String bank) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("\\d{10,30}");
        m = p.matcher(bank);
        b = m.matches();
        return b;
    }


    //Bank=;     //验证银行对公
    public static boolean BankPublic(String bank) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("\\d{9,30}");
        m = p.matcher(bank);
        b = m.matches();
        return b;
    }

    /**
     * 验证手机号码
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8,7][0-9]{9}$");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

}
