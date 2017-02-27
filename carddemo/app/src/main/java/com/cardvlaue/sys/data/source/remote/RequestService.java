package com.cardvlaue.sys.data.source.remote;

import android.support.v4.util.ArrayMap;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.AddressSearchResponse;
import com.cardvlaue.sys.data.AlipayVerifyResponse;
import com.cardvlaue.sys.data.ApplyInfoResponse;
import com.cardvlaue.sys.data.CheckAuthorizeResponse;
import com.cardvlaue.sys.data.CurrentShopBean;
import com.cardvlaue.sys.data.HomeImageDO;
import com.cardvlaue.sys.data.ImeiDO;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.MessageResponse;
import com.cardvlaue.sys.data.MidsResponse;
import com.cardvlaue.sys.data.MobilePhoneVerifyResponse;
import com.cardvlaue.sys.data.SearchQueryDO;
import com.cardvlaue.sys.data.ShopListsBean;
import com.cardvlaue.sys.data.SplashResponse;
import com.cardvlaue.sys.data.UserCreditResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.VerifyCodeResponse;
import io.reactivex.Flowable;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit Interface
 */
interface RequestService {

    /**
     * 提交通讯录数据
     *
     * @param imei 设备唯一号
     * @param body 参数
     * @return 响应
     */
    @POST("behavior/addAddressBook")
    Flowable<ImeiDO> uploadAddAddressBook(@Header("X-Penguin-Driver-Identifier") String imei,
        @Body JSONObject body);

    /**
     * 提交意见反馈
     *
     * @param feedback 意见
     * @return 提交结果
     */
    @POST("feedback")
    Flowable<String> postFeedback(@Body JSONObject feedback);

    /**
     * 居住地址的一级联动
     */
    @GET("provincelist")
    Flowable<List<SearchQueryDO>> queryAddressLists(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken);

    /**
     * 居住地址的二级联动
     */
    @GET("proid/{proid}")
    Flowable<List<SearchQueryDO>> queryAddressProidLists(
        @Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken, @Path("proid") String where);

    /**
     * 居住地址的三级联动
     */
    @GET("countylist/{countyid}")
    Flowable<List<SearchQueryDO>> queryAddressCountyLists(
        @Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken, @Path("countyid") String where);

    /**
     * 查询拟融资期限
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @return 期限
     */
    @GET("planFundTerm")
    Flowable<String> queryFinanceTariff(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken);

    /**
     * 聚信立验证
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param applicationId applicationId
     * @param body 参数
     * @return 响应
     */
    @POST("applications/{applicationId}/jxlVerify")
    Flowable<MobilePhoneVerifyResponse> jxlVerify(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("applicationId") String applicationId, @Body JSONObject body);

    /**
     * 聚信立提交
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param applicationId applicationId
     * @param body 参数
     * @return 响应
     */
    @POST("applications/{applicationId}/jxlSubmit")
    Flowable<MobilePhoneVerifyResponse> jxlSubmit(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("applicationId") String applicationId, @Body JSONObject body);

    /**
     * 支付宝登录
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param body 参数
     * @return 响应
     */
    @POST("alipay/login")
    Flowable<AlipayVerifyResponse> alipayLoginVerify(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Body JSONObject body);

    /**
     * 获取支付宝验证状态
     */
    @GET("alipayStatus/{applicationId}")
    Flowable<AlipayVerifyResponse> alipayStatus(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("applicationId") String applicationId);

    /**
     * 支付宝验证码登录
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param body 参数
     * @return 响应
     */
    @POST("alipay/loginWithCode")
    Flowable<AlipayVerifyResponse> getAlipayLoginWithCodeVerify(
        @Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Body JSONObject body);

    /**
     * 支付宝重新发送验证码
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param body 参数
     * @return 响应
     */
    @POST("alipay/resendCode")
    Flowable<AlipayVerifyResponse> getAlipayResendCodeVerify(
        @Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Body JSONObject body);

    /**
     * 查询消息
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param where 1 = 系统消息 0 = 用户消息
     * @param skip 从多少条开始
     * @param limit 每次返回的条数(默认10条)
     * @return 响应
     */
    @GET("messages")
    Flowable<List<MessageResponse>> queryMessage(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Query("where") JSONObject where,
        @Query("skip") int skip,
        @Query("limit") int limit);

    /**
     * 查询首页图片
     *
     * @param pageName 首页名称
     * @return 响应
     */
    @GET(UrlConstants.HOME_IMAGE)
    Flowable<HomeImageDO> queryHomeImages(@Query("pageName") String pageName);

    /**
     * 创建商编
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param body 参数
     * @return 响应
     */
    @POST("credits/{creditId}/mids")
    Flowable<MidsResponse> createMids(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("creditId") String creditId, @Body ArrayMap<String, String> body);

    /**
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param mId 获取问题的商编
     * @return 响应
     */
    @GET("credits/{creditId}/mids/{mId}/verifyQuestion")
    Flowable<MidsResponse> questionMids(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("creditId") String creditId, @Path("mId") String mId);

    /**
     * 验证商编
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param verifyId 待验证的商编
     * @param body 参数
     * @return 响应
     */
    @POST("credits/{creditId}/midVerification/{verifyId}")
    Flowable<MidsResponse> verifyMids(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("creditId") String creditId, @Path("verifyId") String verifyId,
        @Body JSONObject body);

    /**
     * 删除商编
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param mId 待删除的商编
     * @return 响应
     */
    @DELETE("credits/{creditId}/mids/{mId}")
    Flowable<MidsResponse> deleteMids(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("creditId") String creditId, @Path("mId") String mId);

    /**
     * 查询商编列表
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @return 响应
     */
    @GET("credits/{creditId}/mids")
    Flowable<MidsResponse> queryMids(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("creditId") String creditId);

    /**
     * 查询当前商铺
     *
     * @param phone 手机号
     * @return 响应
     */
    @GET("merchantInfo/{phone}")
    Flowable<CurrentShopBean> queryCurrentShop(@Path("phone") String phone);

    /**
     * 切换店铺
     *
     * @param phone 手机号
     * @param id 店铺编号
     * @return 响应
     */
    @PUT("merchantInfo/{phone}")
    Flowable<CurrentShopBean> setCurrentShop(@Path("phone") String phone, @Body JSONObject id);

    /**
     * 查询商铺列表
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @return 响应
     */
    @GET("merchants/shops")
    Flowable<List<ShopListsBean>> queryShopLists(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken);

    /**
     * 获取授信
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @return 授信信息
     */
    @GET("credits/{creditId}")
    Flowable<UserCreditResponse> userCredit(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken, @Path("creditId") String creditId);

    /**
     * 创建授信
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param body 参数
     * @return 响应
     */
    @POST("credits")
    Flowable<LoginResponse> createCredit(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken, @Body JSONObject body);

    /**
     * 查询行业清单
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param where 查询条件
     * @return 响应
     */
    @GET("industrylists")
    Flowable<List<SearchQueryDO>> queryIndustryLists(
        @Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken, @Query("where") String where);

    /**
     * 省市区县的模糊搜索
     *
     * @param where 查询条件
     */
    @GET("provinceSearch")
    Flowable<List<SearchQueryDO>> queryProvinceData(
        @Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken, @Query("where") String where);

    /**
     * 查询地址
     *
     * @param address 地址
     * @return 响应
     */
    @GET(UrlConstants.ADDRESS_SEARCH)
    Flowable<AddressSearchResponse> checkShopAddress(@Query("address") String address);

    /**
     * 查询申请
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param applicationId applicationId
     * @return 响应
     */
    @GET("applications/{applicationId}")
    Flowable<ApplyInfoResponse> queryApplyInfo(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("applicationId") String applicationId);

    /**
     * 更新申请
     *
     * @param id merchantId
     * @param token accessToken
     * @param applicationId applicationId
     * @param body 参数
     * @return 响应
     */
    @PUT("applications/{applicationId}")
    Flowable<LoginResponse> updateApplyInfo(@Header("X-CRM-Merchant-Id") String id,
        @Header("X-CRM-Access-Token") String token,
        @Path("applicationId") String applicationId, @Body JSONObject body);

    /**
     * 更新用户
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param pObjectId merchantId
     * @param body 参数
     * @return 响应
     */
    @PUT("merchants/{objectId}")
    Flowable<LoginResponse> createOrUpdateUserInfo(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("objectId") String pObjectId, @Body UserInfoNewResponse body);

    /**
     * 获取用户信息数据
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param objectID merchantId
     * @return 响应
     */
    @GET("merchants/{objectID}")
    Flowable<UserInfoNewResponse> queryUserInfo(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken, @Path("objectID") String objectID);

    /**
     * 使用手机号码一键注册或忘记密码
     *
     * @param body 参数
     * @return 响应
     */
    @POST("merchantsByMobilePhone")
    Flowable<LoginResponse> createOrFindPwd(@Header("X-CRM-Udid") String uUid,
        @Body JSONObject body);

    /**
     * 发送手机验证码
     *
     * @param body 参数
     * @return 响应
     */
    @POST("mobilePhoneVerifyCode")
    Flowable<VerifyCodeResponse> sendSmsCode(@Body JSONObject body);

    /**
     * 用户登出
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param mId merchantId
     * @return 响应
     */
    @POST("merchants/{mId}/logout")
    Flowable<LoginResponse> userLogout(@Header("X-CRM-Merchant-Id") String objectId,
        @Header("X-CRM-Access-Token") String accessToken,
        @Path("mId") String mId);

    /**
     * 用户登录
     *
     * @param mobilePhone 手机号
     * @param password 密码
     * @param pushId 极光推送 ID
     * @return 响应
     */
    @GET("login")
    Flowable<LoginResponse> userLogin(@Header("X-CRM-Udid") String uUid,
        @Query("mobilePhone") String mobilePhone, @Query("password") String password,
        @Query("pushId") String pushId);

    /**
     * 创建授权
     *
     * @param request 参数
     * @return 响应
     */
    @POST("authorizations")
    Flowable<LoginResponse> createAuthorize(@Body JSONObject request);

    /**
     * 检查手机号是否已注册授权
     *
     * @param mobilePhone 手机号
     * @return 响应
     */
    @GET("checkMobilePhoneRegisterAuth/{mobilePhone}")
    Flowable<CheckAuthorizeResponse> checkPhoneAuthorize(@Path("mobilePhone") String mobilePhone);

    /**
     * 检查新版本
     *
     * @param type 类型
     * @param platform 平台
     * @return 响应
     */
    @GET(UrlConstants.VERSION_CHECK)
    Flowable<SplashResponse> checkNewVersion(@Query("type") String type,
        @Query("platform") String platform);

}
