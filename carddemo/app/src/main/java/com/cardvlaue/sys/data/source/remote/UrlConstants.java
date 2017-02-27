package com.cardvlaue.sys.data.source.remote;

/**
 * 微信-网址常量
 */
public class UrlConstants {

    /**
     * 测试接口  https://www.cvbaoli.com/testpenguin/
     */
    public static final String BASE_URL = "https://www.cvbaoli.com/testpenguin/";
    /**
     * 融资攻略
     */
    public static final String STRATEGY = BASE_URL + "new/m/more/question";
    /**
     * 征信报告服务协议
     */
    public static final String CREDIT_AGREEMENT = BASE_URL + "regular/creditRegular";
    /**
     * 如何授权 <p> https://www.cvbaoli.com/testpenguin/public/getCreditReport
     */
    public static final String CREDIT_HOW_AUTH = BASE_URL + "public/getCreditReport";
    /**
     * 绑卡授权
     */
    public static final String BIND_BANK_CARD = BASE_URL + "regular/bindBankCard";
    /**
     * 支付宝授权
     */
    public static final String ALIPAY_PAYAGREEMENT = BASE_URL + "regular/aliPayAgreement";
    /**
     * 红包规则
     */
    public static final String CREDIT_RULE = BASE_URL + "new/m/more/regular";
    /**
     * 行业地址
     */
    public static final String ADDRESS_SEARCH = BASE_URL + "new/m/getMultiLngAndLat";
    /**
     * 版本更新
     * <p/>
     * https://www.cvbaoli.com/testpenguin/new/m/clientVersion/query?type=android
     */
    public static final String VERSION_CHECK = BASE_URL + "new/m/clientVersion/query";
    /**
     * 查询首页图片
     * <p/>
     * https://www.cvbaoli.com/testpenguin/new/m/clientConfig/query?pageName=home
     */
    public static final String HOME_IMAGE = BASE_URL + "new/m/clientConfig/query";
    /**
     * 征信报告授权引导
     * <p/>
     * https://www.cvbaoli.com/penguin/regular/showCreditReport
     */
    public static final String CREDIT_REPORT = BASE_URL + "regular/showCreditReport";
    /**
     * 商编填写示例
     * <p/>
     * https://www.cvbaoli.com/testpenguin/regular/posHelp
     */
    public static final String POS_HELP = BASE_URL + "regular/posHelp";
    /**
     * 支付宝扫一扫帮助
     * <p/>
     * https://www.cvbaoli.com/testpenguin/regular/alipayHelp
     */
    public static final String PAY_HELP = BASE_URL + "regular/alipayHelp";
    private static final String NOT_TITLE = "?isApp=1";
    /**
     * 公司简介
     */
    public static final String INTRODUCTION = BASE_URL + "new/m/more/aboutUs" + NOT_TITLE;
    /**
     * 资质荣誉
     */
    public static final String HONOUR = BASE_URL + "new/m/more/honor" + NOT_TITLE;
    /**
     * 联系方式
     */
    public static final String CONTACT = BASE_URL + "new/m/more/contactUs" + NOT_TITLE;
    /**
     * 用户服务协议
     */
    public static final String USER_AGREEMENT = BASE_URL + "regular/showAgreement" + NOT_TITLE;

}
