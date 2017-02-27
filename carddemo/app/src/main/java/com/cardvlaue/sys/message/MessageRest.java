package com.cardvlaue.sys.message;

import org.json.JSONObject;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/9/9.
 */
public interface MessageRest {

    /**
     * <P>查询消息    获取消息</P>
     */
    @Headers({
        "X-CRM-Application-Id: " + "android",
        "X-CRM-Version: " + "2.0.1",
        "Content-Type: application/json"
    })
    @GET("messages")
    Observable<String> attemgetMessage(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Query("where") JSONObject jsonObject,
        @Query("skip") int skip,
        @Query("limit") int limit
    );


    /**
     * 删除消息
     */
    @Headers({
        "X-CRM-Application-Id: " + "android",
        "X-CRM-Version: " + "2.0.1",
        "Content-Type: application/json"
    })
    @DELETE("messages/type/{type}/id/{id}")
    Observable<String> deleteMessage(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
                                /*     @Query("where") JSONObject jsonObject,*/
        @Path("type") int type,
        @Path("id") int id
    );


    /**
     * 获取消息
     */
    @Headers({
        "X-CRM-Application-Id: " + "android",
        "X-CRM-Version: " + "2.0.1",
        "Content-Type: application/json"
    })
    @GET("messages/type/{type}/id/{id}")
    Observable<String> getMessageDetail(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
                                /*  @Query("where") JSONObject jsonObject,*/
        @Path("type") int type,
        @Path("id") int id
    );

}
