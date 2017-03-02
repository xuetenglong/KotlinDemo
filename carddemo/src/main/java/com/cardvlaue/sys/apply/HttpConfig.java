package com.cardvlaue.sys.apply;

import com.cardvlaue.sys.data.source.remote.RequestConstants;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.retrofit.FastJsonConvertFactory;
import com.cardvlaue.sys.retrofit.FastJsonResponseBodyConverter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * <p>Retrofit 的配置<p/>
 */
public class HttpConfig {

    /**
     * 默认 JSON 客户端
     */
    public static synchronized Retrofit getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(3, TimeUnit.MINUTES)
            .build();
        return new Retrofit.Builder()
            .baseUrl(RequestConstants.BASE_URL)
            .client(client)
            .addConverterFactory(FastJsonConvertFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    }

    public static synchronized Retrofit getClientWeiXin() {
        return new Retrofit.Builder()
            .baseUrl(UrlConstants.BASE_URL)
            .client(new OkHttpClient())
            .addConverterFactory(FastJsonConvertFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    }

    public static synchronized Retrofit getRequestStringClient() {
        OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(3, TimeUnit.MINUTES)
            .build();
        return new Retrofit.Builder()
            .baseUrl(RequestConstants.BASE_URL)
            .client(client)//okHttpClient  new OkHttpClient()
            .addConverterFactory(new Converter.Factory() {
                @Override
                public Converter<?, RequestBody> requestBodyConverter(Type type,
                    Annotation[] parameterAnnotations, Annotation[] methodAnnotations,
                    Retrofit retrofit) {
                    return new Converter<String, RequestBody>() {
                        @Override
                        public RequestBody convert(String value) throws IOException {
                            return RequestBody
                                .create(MediaType.parse("application/text; charset=UTF-8"), value);
                        }
                    };
                }

                @Override
                public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                    Annotation[] annotations, Retrofit retrofit) {
                    return new FastJsonResponseBodyConverter<>(type);
                }
            })
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    }

}
