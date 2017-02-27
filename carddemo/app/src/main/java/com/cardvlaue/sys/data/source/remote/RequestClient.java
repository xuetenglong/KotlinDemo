package com.cardvlaue.sys.data.source.remote;

import com.cardvlaue.sys.retrofit.FastJsonConvertFactory;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

class RequestClient {

    /**
     * PHP 服务器
     * <p/>
     * 默认超时时间
     *
     * @return RequestService
     */
    static RequestService newJsonClient() {
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                    .addHeader("X-CRM-Application-Id", RequestConstants.APPLICATION_ID)
                    .addHeader("X-CRM-Version", RequestConstants.VERSION)
                    .addHeader("Content-Type", RequestConstants.JSON_TYPE)
                    .build();
                return chain.proceed(request);
            })
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RequestConstants.BASE_URL)
            .client(client)
            .addConverterFactory(FastJsonConvertFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

        return retrofit.create(RequestService.class);
    }

    /**
     * 超时时间
     *
     * @return RequestService
     */
    static RequestService hasTimeOutClient() {
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                    .addHeader("X-CRM-Application-Id", RequestConstants.APPLICATION_ID)
                    .addHeader("X-CRM-Version", RequestConstants.VERSION)
                    .addHeader("Content-Type", RequestConstants.JSON_TYPE)
                    .build();
                return chain.proceed(request);
            })
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(3, TimeUnit.MINUTES)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RequestConstants.BASE_URL)
            .client(client)
            .addConverterFactory(FastJsonConvertFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

        return retrofit.create(RequestService.class);
    }
}
