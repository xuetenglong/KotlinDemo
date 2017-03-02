package com.cardvlaue.sys.data;

public class SplashResponse {

    public String resultCode;

    public String resultMsg;

    public SplashDataResponse resultData;

    public boolean requestSuccess() {
        return "1".equals(resultCode);
    }

}
