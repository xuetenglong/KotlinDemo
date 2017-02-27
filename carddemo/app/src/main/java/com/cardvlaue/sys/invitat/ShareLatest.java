package com.cardvlaue.sys.invitat;

import com.cardvlaue.sys.data.ErrorResponse;

/**
 * Created by Administrator on 2016/9/7.
 */
class ShareLatest extends ErrorResponse {

    private String resultCode;

    private String resultMsg;

    private String resultData;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }
}
