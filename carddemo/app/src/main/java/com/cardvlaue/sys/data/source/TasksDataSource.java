/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cardvlaue.sys.data.source;

import android.support.annotation.NonNull;
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
import io.reactivex.Observable;
import java.util.List;

/**
 * Main entry point for accessing tasks data.
 */
public interface TasksDataSource {

    void saveUdid(String udid);

    String getUdid();

    void saveMerchantId(String merchantId);

    String getMerchantId();

    void saveCreditId(String creditId);

    String getCreditId();

    void saveSetStep(String setStep);

    String getSetStep();

    void saveCredCreditId(String credcreditId);

    String getCredCreditId();

    String getObjectId();

    void saveApplicationId(String applicationId);

    String getApplicationId();

    /**
     * 获取缓存本地数据
     */
    String getHomeImageData();

    /**
     * 保存首页图片数据
     */
    void saveHomeImageData(String msg);

    /**
     * 是否检测到新版本
     */
    String getVersionCheckInfo();

    /**
     * 保存是否有新版本
     */
    void saveVersionCheckInfo(String msg);

    /**
     * 提交意见反馈
     *
     * @param feedback 意见
     * @return 提交结果
     */
    Flowable<String> postFeedback(JSONObject feedback);

    /**
     * 居住地址的三级联动
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param where 查询条件
     * @return 地址信息
     */
    Flowable<List<SearchQueryDO>> queryAddressCountyLists(@NonNull String objectId,
        @NonNull String accessToken,
        String where);

    /**
     * 居住地址的二级联动
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param where 查询条件
     * @return 地址信息
     */
    Flowable<List<SearchQueryDO>> queryAddressProidLists(@NonNull String objectId,
        @NonNull String accessToken,
        String where);

    /**
     * 居住地址的一级联动
     */
    Flowable<List<SearchQueryDO>> queryAddress(@NonNull String objectId,
        @NonNull String accessToken);

    /**
     * 查询拟融资期限
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @return 期限
     */
    Flowable<String> queryFinanceTariff(@NonNull String objectId, @NonNull String accessToken);

    /**
     * 支付宝登录
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param body 登录参数
     * @return 结果
     */
    Flowable<AlipayVerifyResponse> getAlipayLoginVerify(String objectId, String accessToken,
        JSONObject body);

    /**
     * 获取支付宝验证状态
     *
     * @param appId applicationId
     * @return 验证状态
     */
    Flowable<AlipayVerifyResponse> getalipayStatus(String objectId, String accessToken,
        String appId);

    /**
     * 支付宝验证码登录
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param body 验证码
     */
    Flowable<AlipayVerifyResponse> getAlipayLoginWithCodeVerify(String objectId,
        String accessToken,
        JSONObject body);

    /**
     * 支付宝重新发送验证码
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param body 验证码
     */
    Flowable<AlipayVerifyResponse> getAlipayResendCodeVerify(String objectId, String accessToken,
        JSONObject body);

    /**
     * 验证聚信立
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param applicationId applicationId
     * @param body 参数
     * @param callback 回调
     */
    void getJxlVerify(String objectId, String accessToken, String applicationId, JSONObject body,
        LoadResponseNewCallback<MobilePhoneVerifyResponse, String> callback);

    /**
     * 提交聚信立验证
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param applicationId applicationId
     * @param body 参数
     * @param callback 回调
     */
    void getJxlSubmit(String objectId, String accessToken, String applicationId, JSONObject body,
        LoadResponseNewCallback<MobilePhoneVerifyResponse, String> callback);

    /**
     * 获取服务器授信信息
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param callback 回调
     */
    void getCreditInfo(String objectId, String accessToken, String creditId,
        LoadResponseNewCallback<UserCreditResponse, String> callback);

    /**
     * 读取本地授信信息
     *
     * @return 授信信息
     */
    UserCreditResponse getCreditInfo();

    /**
     * 创建授信
     *
     * @param id merchantId
     * @param token accessToken
     * @param body 参数
     * @param callback 回调
     */
    void createCredit(String id, String token, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback);

    /**
     * 本地缓存授信信息
     *
     * @param credit 授信信息
     */
    void saveCreditInfo(UserCreditResponse credit);

    /**
     * 查询消息
     *
     * @param objectId merchantId
     * @param accessToken 用户令牌
     * @param where 1 = 系统消息 0 = 用户消息
     * @return 消息
     */
    Flowable<List<MessageResponse>> queryMessage(String objectId, String accessToken,
        JSONObject where);

    /**
     * 查询首页图片
     * <p/>
     * http://www.cvbaoli.com/testpenguin/new/m/clientConfig/query?pageName=home
     */
    Flowable<HomeImageDO> queryHomeImages();

    /**
     * 删除商编
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param mId 删除的商编
     * @param callback 回调
     */
    void deleteMids(String objectId, String accessToken, String creditId, String mId,
        LoadResponseNewCallback<MidsResponse, String> callback);

    /**
     * 创建商编
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param body 参数
     * @param callback 回调
     */
    void createMids(String objectId, String accessToken, String creditId,
        ArrayMap<String, String> body, LoadResponseNewCallback<MidsResponse, String> callback);

    /**
     * 验证商编
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param verifyId 待验证商编编号
     * @param body 参数
     * @param callback 回调
     */
    void verifyMids(String objectId, String accessToken, String creditId, String verifyId,
        JSONObject body, LoadResponseNewCallback<MidsResponse, String> callback);

    /**
     * 查询商编验证问题
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param mId 商编编号
     * @param callback 回调
     */
    void questionMids(String objectId, String accessToken, String creditId, String mId,
        LoadResponseNewCallback<MidsResponse, String> callback);

    /**
     * 查询商编列表
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param callback 回调
     */
    void queryMids(String objectId, String accessToken, String creditId,
        LoadResponseNewCallback<MidsResponse, String> callback);

    /**
     * 获取本地申请信息
     *
     * @return 申请信息
     */
    ApplyInfoResponse getApplyInfo();

    /**
     * 保存申请信息
     *
     * @param apply 申请信息
     */
    void saveApplyInfo(ApplyInfoResponse apply);

    /**
     * 查询申请信息
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param applicationId applicationId
     * @param callback 回调
     */
    void getApplyInfo(String objectId, String accessToken, String applicationId,
        LoadResponseNewCallback<ApplyInfoResponse, String> callback);

    /**
     * 查询行业清单
     *
     * @param where 查询条件
     */
    Flowable<List<SearchQueryDO>> queryIndustry(String objectId, String accessToken,
        String where);

    /**
     * 省市区县的模糊搜索
     *
     * @param where 查询条件
     */
    Flowable<List<SearchQueryDO>> queryProvince(String objectId, String accessToken,
        String where);

    /**
     * 查询地址
     *
     * @param address 地址
     * @return 搜索结果
     */
    Flowable<AddressSearchResponse> queryShopAddress(String address);

    /**
     * 获取 IP 地址
     *
     * @return 本地 IP
     */
    String getIpAddress();

    /**
     * 获取 IP 地址
     *
     * @param url 链接
     * @return 远程 IP
     */
    Observable<String> getIpAddress(String url);

    /**
     * 保存 IP
     *
     * @param ip IP
     */
    void saveIpAddress(String ip);

    /**
     * 获取 GPS
     *
     * @return GPS
     */
    String getGpsAddress();

    /**
     * 保存 GPS
     *
     * @param gps GPS
     */
    void saveGpsAddress(String gps);

    /**
     * 检查新版本
     *
     * @param channel 渠道编号
     * @return 版本信息
     */
    Flowable<SplashResponse> checkNewVersion(String channel);

    /**
     * 注册或忘记密码
     *
     * @param body 参数
     * @param callback 回调
     */
    void createOrFindPwd(String uUid, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback);

    /**
     * 发送短信验证码
     *
     * @param mobilePhone 手机号
     * @param code 图片验证码
     * @param callback 回调
     */
    void sendSmsCode(String mobilePhone, String code,
        LoadResponseNewCallback<VerifyCodeResponse, String> callback);

    /**
     * 更新申请
     *
     * @param id merchantId
     * @param token accessToken
     * @param applicationId applicationId
     * @param body 参数
     * @param callback 回调
     */
    void updateApplyInfo(String id, String token, String applicationId, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback);

    /**
     * 查询店铺列表
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @return 店铺列表
     */
    Flowable<List<ShopListsBean>> queryShopLists(String objectId, String accessToken);

    /**
     * 创建或更新用户信息数据
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param body 参数
     * @param callback 回调
     */
    void createOrUpdateUserInfo(String objectId, String accessToken, UserInfoNewResponse body,
        LoadResponseNewCallback<LoginResponse, String> callback);

    /**
     * 获取服务器用户信息数据
     */
    Flowable<UserInfoNewResponse> getUserInfo(String objectId, String accessToken);

    /**
     * 获取本地用户信息数据
     *
     * @return 用户信息
     */
    UserInfoNewResponse getUserInfo();

    /**
     * 保存用户信息
     *
     * @param user 本地用户信息数据
     */
    void saveUserInfo(UserInfoNewResponse user);

    /**
     * 切换到指定的店铺
     *
     * @param phone 手机号
     * @param id merchantId
     * @return 切换是否成功
     */
    Flowable<CurrentShopBean> setCurrentShop(String phone, JSONObject id);

    /**
     * 查询当前 merchantId
     *
     * @param phone 手机号
     * @return merchantId
     */
    Flowable<CurrentShopBean> queryCurrentShop(String phone);

    /**
     * 上传通讯录
     */
    Flowable<ImeiDO> uploadAddAddressBook(String imei, JSONObject body);

    /**
     * 清除用户缓存数据
     */
    void clearCache();

    /**
     * 获取用户手机号
     *
     * @return 手机号
     */
    String getMobilePhone();

    /**
     * 保存手机号
     *
     * @param phone 手机号
     */
    void saveMobilePhone(String phone);

    /**
     * 用户注销
     *
     * @param id merchantId
     * @param token accessToken
     * @return 是否注销成功
     */
    Flowable<LoginResponse> userLogout(String id, String token);

    /**
     * 保存 merchantId
     *
     * @param id merchantId
     */
    void saveLogin(String id);

    /**
     * 保存 merchantId 和 accessToken
     *
     * @param id merchantId
     * @param token accessToken
     */
    void saveLogin(String id, String token);

    /**
     * 保存类似登录成功之后的数据
     *
     * @param phone 手机号
     * @param id merchantId
     * @param token accessToken
     */
    void saveLogin(String phone, String id, String token);

    /**
     * 获取本地登录数据
     *
     * @return 本地登录数据
     */
    LoginResponse getLogin();

    /**
     * 用户登录
     *
     * @param args mobilePhone -> password -> pushId 顺序不能错
     * @return 是否登录成功
     */
    Flowable<LoginResponse> getLogin(String... args);

    /**
     * 创建服务协议授权
     *
     * @param body 授权参数
     * @return 是否授权成功
     */
    Flowable<LoginResponse> createAuthorize(JSONObject body);

    /**
     * 检查账号是否已确认服务协议
     *
     * @param mobilePhone 手机号
     * @return 是否已授权
     */
    Flowable<CheckAuthorizeResponse> checkAuthorize(String mobilePhone);

    interface LoadResponseNewCallback<Success, Failure> {

        void onResponseSuccess(Success s);

        void onResponseFailure(Failure f);

    }

}
