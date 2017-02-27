package com.cardvlaue.sys.creditreport;

import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.cardverify.MobilePhoneVerifyCodeBO;
import com.cardvlaue.sys.data.source.remote.RequestConstants;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by cardvalue on 2016/5/30.
 */
public interface ICreditReportsRest {

    /**
     * 获取验证码
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION,
        "Content-Type: " + RequestConstants.JSON_TYPE
    })
    @GET("verifyCode")
    Observable<MobilePhoneVerifyCodeBO> verifyCode(@Header("X-CRM-Access-Token") String token,
        @Header("X-CRM-Merchant-Id") String id,
        @Query("where") JSONObject type);

    /**
     * 验证征信报告
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION,
        "Content-Type: " + RequestConstants.JSON_TYPE
    })
    @POST("applications/{applicationsId}/creditReport")
    Observable<MobilePhoneVerifyCodeBO> creditReport(@Header("X-CRM-Access-Token") String token,
        @Header("X-CRM-Merchant-Id") String id,
        @Path("applicationsId") String applicationsId,
        @Body JSONObject body);
}
