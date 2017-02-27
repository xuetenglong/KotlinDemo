package com.cardvlaue.sys.invitat;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/9/6.
 */
public interface InvitatRest {

    /**
     * 获取客户端最新分享信息接口：
     */
    @GET("new/m/clientActivities/query")
    Observable<ShareLatest> getLatestShare(@Query("username") String username,
        @Query("platform") String platform);
}
