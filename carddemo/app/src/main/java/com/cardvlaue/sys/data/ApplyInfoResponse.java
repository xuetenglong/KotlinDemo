package com.cardvlaue.sys.data;

public class ApplyInfoResponse extends ErrorResponse {

    /**
     * 授信编号
     */
    public String creditId;

    /**
     * 是否通过聚信令验证	1:是;0:否
     */
    public String isJxlValid;

    /**
     * 征信报告验证状态	0-未验证，1-已验证
     */
    public String creditReportStatus;

    /**
     * 是否通过支付宝验证	1:是;0:否
     */
    public String alipayCheck;

    public String isKalaRecognize;//是否卡拉验证

}
