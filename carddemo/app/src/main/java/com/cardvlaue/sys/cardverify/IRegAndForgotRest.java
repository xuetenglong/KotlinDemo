package com.cardvlaue.sys.cardverify;

import com.cardvlaue.sys.data.source.remote.RequestConstants;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * <p>注册和忘记密码相关方法<p/> Created by cardvalue on 2016/4/12.
 */
public interface IRegAndForgotRest {

    /**
     * 发送手机验证码
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION,
        "Content-Type: " + RequestConstants.JSON_TYPE
    })
    @POST("mobilePhoneVerifyCode")
    Observable<MobilePhoneVerifyCodeBO> mobilePhoneVerifyCode(@Body MobilePhoneVerifyCodeBO code);


}