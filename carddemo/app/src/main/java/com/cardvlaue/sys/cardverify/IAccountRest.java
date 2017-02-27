package com.cardvlaue.sys.cardverify;

import com.cardvlaue.sys.data.source.remote.RequestConstants;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by cardvalue on 2016/5/11.
 */
public interface IAccountRest {

    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION,
    })
    @POST("merchants")
    Observable<LoginBO> login(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token);
}
