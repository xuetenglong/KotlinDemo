package com.cardvlaue.sys.confirm;

import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.source.remote.RequestConstants;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/7/13.
 */
public interface ConfirmRest {

    /**
     * 查查询银行清单
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("banks")
    Observable<String> queryConfirmList(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token,
        @Query("where") String where);

    /**
     * 激活设备
     */
    @Headers({
        "X-Penguin-Driver-Type: " + RequestConstants.APPLICATION_ID,
        "X-Penguin-App-Version: " + RequestConstants.VERSION,
        "content-type: " + RequestConstants.JSON_TYPE
    })
    @POST("behavior/activateDriver")
    Observable<Imei> getImei(@Header("X-Penguin-Driver-Identifier") String imei,
        @Header("X-Penguin-Platform") String readKey);

    /**
     * 添加用户行为记录
     */
    @Headers({
        "X-Penguin-Driver-Type: " + "Android",
        "X-Penguin-App-Version: " + RequestConstants.VERSION,
        "content-type: " + RequestConstants.JSON_TYPE
    })
    @POST("behavior/addBehaviorRecords")
    Observable<Imei> getUserRecord(@Header("X-Penguin-Driver-Identifier") String imei,
        @Header("X-Penguin-Platform") String readKey, @Body JSONObject body);

}
