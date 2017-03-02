package com.cardvlaue.sys.creditreport;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by cardvalue on 2016/5/30.
 */
public interface ICreditReportsPresenter {

    /**
     * 获取图片验证码
     */
    void getVerificationCode();

    /**
     * 验证征信报告
     */
    void creditReport(JSONObject body);
}
