package com.cardvlaue.sys.applyinfo;

import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.source.remote.RequestConstants;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * <p>申请相关的<p/> Created by Administrator on 2016/6/24.
 */
public interface ApplyRest {

    /**
     * 查询确认清单 增加传入参数type表示清单类型，1：申请信息，2：融资方案，3：融资保理通知书
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("applications/{applicationId}/confirmlists")
    Observable<String> queryConfirmList(@Header("X-CRM-Access-Token") String token,
        @Header("X-CRM-Merchant-Id") String id,
        @Path("applicationId") String applicationId, @Query("where") JSONObject where);


}
