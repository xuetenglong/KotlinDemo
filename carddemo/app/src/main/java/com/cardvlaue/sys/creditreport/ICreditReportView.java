package com.cardvlaue.sys.creditreport;

/**
 * Created by cardvalue on 2016/5/30.
 */
public interface ICreditReportView {

    void toast(String msg);

    /**
     * 图片验证码获取成功
     */
    void imgCodeLoadSuccess(String url, String id);

    /**
     * 征信报告授权成功
     */
    void creditReportSuccess();

    void creditFailure();
}
