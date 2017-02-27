package com.cardvlaue.sys.bill;

import com.cardvlaue.sys.data.source.remote.RequestConstants;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/7/12.
 */
public interface BillRest {

    /**
     * 查询对账清单
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("applications/{applicationId}/cashList")
    Observable<CashList> queryCashList(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token,
        @Path("applicationId") String creditId,
        @Query("where") String where);
}
