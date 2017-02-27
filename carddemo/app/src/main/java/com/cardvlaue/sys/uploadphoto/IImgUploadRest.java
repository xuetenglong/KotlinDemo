package com.cardvlaue.sys.uploadphoto;

import com.cardvlaue.sys.data.source.remote.RequestConstants;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by cardvalue on 2016/6/15.
 */
public interface IImgUploadRest {


    /**
     * 上传照片
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.BASE64_TYPE,//IMG_TYPE   BASE64_TYPE
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @POST("merchants/{mid}/checklists/{listId}/files/{fileName}")
    Observable<UploadFileBO> updateUserInfo(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token,
        @Path("mid") String mid, @Path("listId") String listId,
        @Path("fileName") String fileName, @Body String img);

}

