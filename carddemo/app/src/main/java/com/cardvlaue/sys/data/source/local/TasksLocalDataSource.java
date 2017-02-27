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

package com.cardvlaue.sys.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
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
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.local.LocalDataContract.HomeImageDataLocalDataContract;
import com.cardvlaue.sys.data.source.local.LocalDataContract.VersionLocalDataContract;
import com.cardvlaue.sys.util.CacheUtil;
import com.cardvlaue.sys.util.PrefUtil;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import java.util.List;
import javax.inject.Singleton;

/**
 * Concrete implementation of a data source as a db.
 */
@Singleton
public class TasksLocalDataSource implements TasksDataSource {

    private static final String XML_UDID = "udid";

    private static final String XML_OBJECT_ID = "objectId";

    private static final String XML_APPLICATION_ID = "applicationId";

    private static final String XML_MERCHANT_ID = "merchantId";

    private static final String XML_CREDIT_ID = "creditId";

    private static final String XML_CRED_CREDIT_ID = "credCreditId";

    private static final String XML_SET_STEP = "setStepId";

    private Context mContext;

    public TasksLocalDataSource(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public String getObjectId() {
        return PrefUtil.getString(mContext, XML_OBJECT_ID);
    }

    @Override
    public void saveUdid(@NonNull String udid) {
        PrefUtil.putString(mContext, XML_UDID, udid);
    }

    @Override
    public String getUdid() {
        return PrefUtil.getString(mContext, XML_UDID);
    }

    @Override
    public void saveCreditId(String creditId) {
        PrefUtil.putString(mContext, XML_CREDIT_ID, creditId);
    }

    @Override
    public String getCreditId() {
        return PrefUtil.getString(mContext, XML_CREDIT_ID);
    }

    @Override
    public void saveSetStep(String setStep) {
        PrefUtil.putString(mContext, XML_SET_STEP, setStep);
    }

    @Override
    public String getSetStep() {
        return PrefUtil.getString(mContext, XML_SET_STEP);
    }

    @Override
    public void saveCredCreditId(String credCreditId) {
        PrefUtil.putString(mContext, XML_CRED_CREDIT_ID, credCreditId);
    }

    @Override
    public String getCredCreditId() {
        return PrefUtil.getString(mContext, XML_CRED_CREDIT_ID);
    }

    @Override
    public void saveMerchantId(String merchantId) {
        PrefUtil.putString(mContext, XML_MERCHANT_ID, merchantId);
    }

    @Override
    public String getMerchantId() {
        return PrefUtil.getString(mContext, XML_MERCHANT_ID);
    }

    @Override
    public void saveApplicationId(@NonNull String applicationId) {
        PrefUtil.putString(mContext, XML_APPLICATION_ID, applicationId);
    }

    @Override
    public String getApplicationId() {
        return PrefUtil.getString(mContext, XML_APPLICATION_ID);
    }

    @Override
    public String getHomeImageData() {
        String[] keys = {HomeImageDataLocalDataContract.HOME_IMAGE_DATA};
        String[] res = CacheUtil.getString(mContext, keys);
        return res[0];
    }

    @Override
    public void saveHomeImageData(String msg) {
        String[] keys = {HomeImageDataLocalDataContract.HOME_IMAGE_DATA};
        String[] values = {msg};
        CacheUtil.putString(mContext, keys, values);
    }

    @Override
    public String getVersionCheckInfo() {
        String[] keys = {VersionLocalDataContract.VERSION_CHECK_INFO};
        String[] res = CacheUtil.getString(mContext, keys);
        return res[0];
    }

    @Override
    public void saveVersionCheckInfo(String msg) {
        String[] keys = {VersionLocalDataContract.VERSION_CHECK_INFO};
        String[] values = {msg};
        CacheUtil.putString(mContext, keys, values);
    }

    @Override
    public Flowable<String> postFeedback(JSONObject feedback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryAddressCountyLists(@NonNull String objectId,
        @NonNull String accessToken, String where) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryAddressProidLists(@NonNull String objectId,
        @NonNull String accessToken, String where) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryAddress(@NonNull String objectId,
        @NonNull String accessToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<String> queryFinanceTariff(@NonNull String objectId,
        @NonNull String accessToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<AlipayVerifyResponse> getAlipayLoginVerify(String objectId,
        String accessToken,
        JSONObject body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<AlipayVerifyResponse> getalipayStatus(String objectId, String accessToken,
        String appId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<AlipayVerifyResponse> getAlipayLoginWithCodeVerify(String objectId,
        String accessToken, JSONObject body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<AlipayVerifyResponse> getAlipayResendCodeVerify(String objectId,
        String accessToken, JSONObject body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getJxlVerify(String objectId, String accessToken, String applicationId,
        JSONObject body,
        LoadResponseNewCallback<MobilePhoneVerifyResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getJxlSubmit(String objectId, String accessToken, String applicationId,
        JSONObject body,
        LoadResponseNewCallback<MobilePhoneVerifyResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getCreditInfo(String objectId, String accessToken, String creditId,
        LoadResponseNewCallback<UserCreditResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserCreditResponse getCreditInfo() {
        String[] keys = {LocalDataContract.CreditInfoLocalDataContract.JSON};
        String[] res = CacheUtil.getString(mContext, keys);
        UserCreditResponse creditInfo = JSON.parseObject(res[0], UserCreditResponse.class);
        return creditInfo != null ? creditInfo : new UserCreditResponse();
    }

    @Override
    public void saveCreditInfo(UserCreditResponse credit) {
        String[] keys = {LocalDataContract.CreditInfoLocalDataContract.JSON};
        String[] values = {JSON.toJSONString(credit)};
        CacheUtil.putString(mContext, keys, values);
    }

    @Override
    public Flowable<List<MessageResponse>> queryMessage(String objectId, String accessToken,
        JSONObject where) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createCredit(String id, String token, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<HomeImageDO> queryHomeImages() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteMids(String objectId, String accessToken, String creditId, String mId,
        LoadResponseNewCallback<MidsResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createMids(String objectId, String accessToken, String creditId,
        ArrayMap<String, String> body, LoadResponseNewCallback<MidsResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void verifyMids(String objectId, String accessToken, String creditId, String verifyId,
        JSONObject body, LoadResponseNewCallback<MidsResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void questionMids(String objectId, String accessToken, String creditId, String mId,
        LoadResponseNewCallback<MidsResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void queryMids(String objectId, String accessToken, String creditId,
        LoadResponseNewCallback<MidsResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ApplyInfoResponse getApplyInfo() {
        String[] keys = {LocalDataContract.ApplyInfoLocalDataContract.JSON};
        String[] res = CacheUtil.getString(mContext, keys);
        ApplyInfoResponse applyInfo = JSON.parseObject(res[0], ApplyInfoResponse.class);
        return applyInfo != null ? applyInfo : new ApplyInfoResponse();
    }

    @Override
    public void saveApplyInfo(ApplyInfoResponse apply) {
        String[] keys = {LocalDataContract.ApplyInfoLocalDataContract.JSON};
        String[] values = {JSON.toJSONString(apply)};
        CacheUtil.putString(mContext, keys, values);
    }

    @Override
    public void getApplyInfo(String objectId, String accessToken, String applicationId,
        LoadResponseNewCallback<ApplyInfoResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryIndustry(String objectId, String accessToken,
        String where) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryProvince(String objectId, String accessToken,
        String where) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<AddressSearchResponse> queryShopAddress(String address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getIpAddress() {
        String[] keys = {LocalDataContract.DeviceLocalDataContract.IP_ADDRESS};
        String[] res = CacheUtil.getString(mContext, keys);
        return TextUtils.isEmpty(res[0]) ? "127.0.0.1" : res[0];
    }

    @Override
    public Observable<String> getIpAddress(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveIpAddress(String ip) {
        String[] keys = {LocalDataContract.DeviceLocalDataContract.IP_ADDRESS};
        String[] values = {ip};
        CacheUtil.putString(mContext, keys, values);
    }

    @Override
    public String getGpsAddress() {
        String[] keys = {LocalDataContract.DeviceLocalDataContract.GPS_ADDRESS};
        String[] res = CacheUtil.getString(mContext, keys);
        return TextUtils.isEmpty(res[0]) ? "0.0,0.0" : res[0];
    }

    @Override
    public void saveGpsAddress(String address) {
        String[] keys = {LocalDataContract.DeviceLocalDataContract.GPS_ADDRESS};
        String[] values = {address};
        CacheUtil.putString(mContext, keys, values);
    }

    @Override
    public Flowable<SplashResponse> checkNewVersion(String channel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createOrFindPwd(String uUid, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendSmsCode(String mobilePhone, String code,
        LoadResponseNewCallback<VerifyCodeResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateApplyInfo(String id, String token, String applicationId, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<List<ShopListsBean>> queryShopLists(String objectId, String accessToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createOrUpdateUserInfo(String objectId, String accessToken,
        UserInfoNewResponse body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<UserInfoNewResponse> getUserInfo(String objectId, String accessToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserInfoNewResponse getUserInfo() {
        String[] keys = {LocalDataContract.UserInfoLocalDataContract.JSON};
        String[] res = CacheUtil.getString(mContext, keys);
        UserInfoNewResponse userInfo = JSON.parseObject(res[0], UserInfoNewResponse.class);
        return userInfo != null ? userInfo : new UserInfoNewResponse();
    }

    @Override
    public void saveUserInfo(UserInfoNewResponse user) {
        String[] keys = {LocalDataContract.UserInfoLocalDataContract.JSON};
        String[] values = {JSON.toJSONString(user)};
        CacheUtil.putString(mContext, keys, values);
    }

    @Override
    public Flowable<CurrentShopBean> setCurrentShop(String phone, JSONObject id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<CurrentShopBean> queryCurrentShop(String phone) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<ImeiDO> uploadAddAddressBook(String imei, JSONObject body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearCache() {
        CacheUtil.clearCache(mContext);
    }

    @Override
    public String getMobilePhone() {
        return CacheUtil.getDeviceString(mContext,
            new String[]{LocalDataContract.LoginLocalDataContract.MOBILE_PHONE})[0];
    }

    @Override
    public void saveMobilePhone(String phone) {
        String[] keys = {LocalDataContract.LoginLocalDataContract.MOBILE_PHONE};
        String[] values = {phone};
        CacheUtil.putDeviceString(mContext, keys, values);
    }

    @Override
    public Flowable<LoginResponse> userLogout(String id, String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveLogin(String id) {
        String[] keys = {LocalDataContract.LoginLocalDataContract.OBJECT_ID};
        String[] values = {id};
        CacheUtil.putString(mContext, keys, values);
    }

    @Override
    public void saveLogin(String id, String token) {
        String[] keys = {LocalDataContract.LoginLocalDataContract.OBJECT_ID,
            LocalDataContract.LoginLocalDataContract.ACCESS_TOKEN};
        String[] values = {id, token};
        CacheUtil.putString(mContext, keys, values);
    }

    @Override
    public void saveLogin(String phone, String id, String token) {
        saveMobilePhone(phone);
        saveLogin(id, token);
    }

    @Override
    public LoginResponse getLogin() {
        String[] keys = {LocalDataContract.LoginLocalDataContract.OBJECT_ID,
            LocalDataContract.LoginLocalDataContract.ACCESS_TOKEN};
        String[] res = CacheUtil.getString(mContext, keys);
        return new LoginResponse(res[0], res[1]);
    }

    @Override
    public Flowable<LoginResponse> getLogin(String... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<LoginResponse> createAuthorize(JSONObject body) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<CheckAuthorizeResponse> checkAuthorize(String mobilePhone) {
        throw new UnsupportedOperationException();
    }

}
