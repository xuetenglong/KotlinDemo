package com.cardvlaue.sys.amount;

import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.apply.LoadApplyBO;
import com.cardvlaue.sys.apply.LoginBO;
import com.cardvlaue.sys.apply.QueryApplyBO;
import com.cardvlaue.sys.apply.UpdateUserInfoBO;
import com.cardvlaue.sys.cardverify.UpdateApplyBO;
import com.cardvlaue.sys.data.AlipayVerifyResponse;
import com.cardvlaue.sys.data.ErrorResponse;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.ShopListsBean;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.remote.RequestConstants;
import com.cardvlaue.sys.invitat.Invitat;
import com.cardvlaue.sys.uploadphoto.NewFileListsBO;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface IFinancingRest {

    /**
     * 上传图片接口
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @PUT("merchants/{objectId}")
    Observable<LoginBO> updateUserInfo(@Path("objectId") String objectId,
        @Header("X-CRM-Merchant-Id") String id, @Header("X-CRM-Access-Token") String token,
        @Body UpdateUserInfoBO user);


    /**
     * 上传固定类型的图片接口
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @PUT("merchants/{objectId}/checklists/{checklistId}/files/{fileId}")
    Observable<LoginBO> fixedType(@Path("objectId") String objectId,
        @Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token, @Body UpdateUserInfoBO user,
        @Path("checklistId") String checklistId,
        @Path("fileId") String fileId);


    /**
     * 创建申请
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @POST("applications")
    Observable<LoginBO> createApply(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token,
        @Body LoginBO merchantId);

    /**
     * <>更新申请</>
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @PUT("applications/{applicationId}")
    Observable<UpdateApplyBO> updateApply(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token, @Path("applicationId") String applicationId,
        @Body UpdateApplyBO merchantId);

    /**
     * 更新用户
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @PUT("merchants/{objectId}")
    Observable<LoginResponse> createOrUpdateUserInfo(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("objectId") String pObjectId, @Body JSONObject body);


    /**
     * 获取用户信息数据
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param objectID merchantId
     * @return 响应
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("merchants/{objectID}")
    Observable<UserInfoNewResponse> queryUserInfo(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken, @Path("objectID") String objectID);


    /**
     * 获取申请
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("applications/{objectId}")
    Observable<LoadApplyBO> queryApply(@Path("objectId") String oid,
        @Header("X-CRM-Merchant-Id") String mid, @Header("X-CRM-Access-Token") String token);

    /**
     * 查询申请
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("applications")
    Observable<QueryApplyBO> queryApplyState(@Header("X-CRM-Merchant-Id") String mid,
        @Header("X-CRM-Access-Token") String token);//, @Query("where") String where


    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("alipay/test")
    Observable<AlipayVerifyResponse> querytest(@Header("X-CRM-Merchant-Id") String mid,
        @Header("X-CRM-Access-Token") String token);


    /**
     * 查询红包
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("merchants/{mid}/coupons")
    Observable<String> queryCoupons(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token, @Path("mid") String mid,
        @Query("where") JSONObject where);

    /**
     * 兑换红包
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @PUT("merchants/{merchantId}/coupons/{couponId}")
    Observable<String> convertCoupons(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token,
        @Path("merchantId") String mid, @Path("couponId") String couponId, @Body JSONObject where);

    /**
     * 查询固定类型文件清单(上传资料图片)
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("merchants/{mid}/newfilelists")
    Observable<NewFileListsBO> newFileLists(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token, @Path("mid") String mid);

    /**
     * 获取补件列表
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("merchants/{merchantId}/checkfilelists")
    Observable<NewFileListsBO> newChecklists(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token, @Path("merchantId") String merchantId,
        @Query("where") JSONObject where);


    /**
     * 删除文件
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @DELETE("merchants/{merchantId}/files/{fileId}")
    Observable<NewFileListsBO> newDeletelists(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token, @Path("merchantId") String merchantId,
        @Path("fileId") String fileId);


    /**
     * 修改密码
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @PUT("merchants/{merchantId}/updatePassword")
    Observable<ErrorResponse> changPwd(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token,
        @Path("merchantId") String merchantId, @Body JSONObject where);


    /**
     * 获取邀请记录
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "Content-Type: " + RequestConstants.JSON_TYPE,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("merchants/{merchantId}/inviteHistory")
    Observable<Invitat> getInviteHistory(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token,
        @Path("merchantId") String merchantId);

    /**
     * 查询商铺列表
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @return 响应
     */
    @Headers({
        "X-CRM-Application-Id: " + RequestConstants.APPLICATION_ID,
        "X-CRM-Version: " + RequestConstants.VERSION
    })
    @GET("merchants/shops")
    Observable<List<ShopListsBean>> queryShopLists(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken);
}
