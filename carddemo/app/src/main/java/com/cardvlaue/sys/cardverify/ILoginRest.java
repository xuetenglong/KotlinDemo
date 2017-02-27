package com.cardvlaue.sys.cardverify;

import com.cardvlaue.sys.data.source.remote.RequestConstants;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * <p>登录相关方法<p/> Created by cardvalue on 2016/4/7.
 */
public interface ILoginRest {

    /**
     * 创建授权
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION,
        "Content-Type: " + RequestConstants.JSON_TYPE
    })
    @POST("authorizations")
    Observable<AuthorizationsBO> createAuth(@Body AuthorizationsBO auth);

    /**
     * 用户登录
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION,
    })
    @GET("login")
    Observable<LoginBO> login(@Query("mobilePhone") String account,
        @Query("password") String password, @Query("pushId") String pushId);

    /**
     * 检查是否授权
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION,
        "Content-Type: " + RequestConstants.JSON_TYPE
    })
    @GET("checkMobilePhoneRegisterAuth/{phone}")
    Observable<CheckMobilePhoneRegisterAuthBO> checkAuth(@Path("phone") String phone);

}
