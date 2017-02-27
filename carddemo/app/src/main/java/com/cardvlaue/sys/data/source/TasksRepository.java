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
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Concrete implementation to load tasks from the data sources into a cache. <p> For simplicity,
 * this implements a dumb synchronisation between locally persisted data and data obtained from the
 * server, by using the remote data source only if the local database doesn't exist or is empty.
 * <p/>
 * By marking the constructor with {@code @Inject} and the class with {@code @Singleton}, Dagger
 * injects the dependencies required to create an instance of the TasksRespository (if it fails, it
 * emits a compiler error). It uses {@link TasksRepositoryModule} to do so, and the constructed
 * instance is available in {@link TasksRepositoryComponent}.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and therefore,
 * to ensure the developer doesn't instantiate the class manually and bypasses Dagger, it's good
 * practice minimise the visibility of the class/constructor as much as possible.
 */
@Singleton
public class TasksRepository implements TasksDataSource {

    private final TasksDataSource mTasksRemoteDataSource;

    private final TasksDataSource mTasksLocalDataSource;

    /**
     * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
     * required to create an instance of the TasksRepository. Because {@link TasksDataSource} is an
     * interface, we must provide to Dagger a way to build those arguments, this is done in {@link
     * TasksRepositoryModule}. <p> When two arguments or more have the same type, we must provide to
     * Dagger a way to differentiate them. This is done using a qualifier. <p> Dagger strictly
     * enforces that arguments not marked with {@code @Nullable} are not injected with {@code
     *
     * @Nullable} values.
     */
    @Inject
    TasksRepository(@Remote TasksDataSource tasksRemoteDataSource,
        @Local TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = tasksRemoteDataSource;
        mTasksLocalDataSource = tasksLocalDataSource;
    }

    @Override
    public void saveUdid(@NonNull String udid) {
        mTasksLocalDataSource.saveUdid(udid);
    }

    @Override
    public String getUdid() {
        return mTasksLocalDataSource.getUdid();
    }


    @Override
    public void saveCreditId(@NonNull String creditId) {
        mTasksLocalDataSource.saveCreditId(creditId);
    }

    @Override
    public String getCreditId() {
        return mTasksLocalDataSource.getCreditId();
    }


    @Override
    public void saveSetStep(@NonNull String setStepId) {
        mTasksLocalDataSource.saveSetStep(setStepId);
    }

    @Override
    public String getSetStep() {
        return mTasksLocalDataSource.getSetStep();
    }

    @Override
    public void saveCredCreditId(@NonNull String credcreditId) {
        mTasksLocalDataSource.saveCredCreditId(credcreditId);
    }

    @Override
    public String getCredCreditId() {
        return mTasksLocalDataSource.getCredCreditId();
    }

    @Override
    public void saveMerchantId(@NonNull String merchantId) {
        mTasksLocalDataSource.saveMerchantId(merchantId);
    }

    @Override
    public String getMerchantId() {
        return mTasksLocalDataSource.getMerchantId();
    }

    @Override
    public String getObjectId() {
        return mTasksLocalDataSource.getObjectId();
    }

    @Override
    public void saveApplicationId(@NonNull String applicationId) {
        mTasksLocalDataSource.saveApplicationId(applicationId);
    }

    @Override
    public String getApplicationId() {
        return mTasksLocalDataSource.getApplicationId();
    }

    @Override
    public String getHomeImageData() {
        return mTasksLocalDataSource.getHomeImageData();
    }

    @Override
    public void saveHomeImageData(String msg) {
        mTasksLocalDataSource.saveHomeImageData(msg);
    }

    @Override
    public String getVersionCheckInfo() {
        return mTasksLocalDataSource.getVersionCheckInfo();
    }

    @Override
    public void saveVersionCheckInfo(String msg) {
        mTasksLocalDataSource.saveVersionCheckInfo(msg);
    }

    @Override
    public Flowable<String> postFeedback(JSONObject feedback) {
        return mTasksRemoteDataSource.postFeedback(feedback);
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryAddressCountyLists(@NonNull String objectId,
        @NonNull String accessToken, String where) {
        return mTasksRemoteDataSource.queryAddressCountyLists(objectId, accessToken, where);
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryAddressProidLists(@NonNull String objectId,
        @NonNull String accessToken, String where) {
        return mTasksRemoteDataSource.queryAddressProidLists(objectId, accessToken, where);
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryAddress(@NonNull String objectId,
        @NonNull String accessToken) {
        return mTasksRemoteDataSource.queryAddress(objectId, accessToken);
    }

    @Override
    public Flowable<String> queryFinanceTariff(@NonNull String objectId,
        @NonNull String accessToken) {
        return mTasksRemoteDataSource.queryFinanceTariff(objectId, accessToken);
    }

    @Override
    public Flowable<AlipayVerifyResponse> getAlipayLoginVerify(String objectId,
        String accessToken,
        JSONObject body) {
        return mTasksRemoteDataSource.getAlipayLoginVerify(objectId, accessToken, body);
    }

    @Override
    public Flowable<AlipayVerifyResponse> getalipayStatus(String objectId, String accessToken,
        String appId) {
        return mTasksRemoteDataSource.getalipayStatus(objectId, accessToken, appId);
    }

    @Override
    public Flowable<AlipayVerifyResponse> getAlipayLoginWithCodeVerify(String objectId,
        String accessToken, JSONObject body) {
        return mTasksRemoteDataSource.getAlipayLoginWithCodeVerify(objectId, accessToken, body);
    }

    @Override
    public Flowable<AlipayVerifyResponse> getAlipayResendCodeVerify(String objectId,
        String accessToken, JSONObject body) {
        return mTasksRemoteDataSource.getAlipayResendCodeVerify(objectId, accessToken, body);
    }

    @Override
    public void getJxlVerify(String objectId, String accessToken, String applicationId,
        JSONObject body, LoadResponseNewCallback<MobilePhoneVerifyResponse, String> callback) {
        mTasksRemoteDataSource.getJxlVerify(objectId, accessToken, applicationId, body, callback);
    }

    @Override
    public void getJxlSubmit(String objectId, String accessToken, String applicationId,
        JSONObject body, LoadResponseNewCallback<MobilePhoneVerifyResponse, String> callback) {
        mTasksRemoteDataSource.getJxlSubmit(objectId, accessToken, applicationId, body, callback);
    }

    @Override
    public void getCreditInfo(String objectId, String accessToken, String creditId,
        LoadResponseNewCallback<UserCreditResponse, String> callback) {
        mTasksRemoteDataSource.getCreditInfo(objectId, accessToken, creditId, callback);
    }

    @Override
    public UserCreditResponse getCreditInfo() {
        return mTasksLocalDataSource.getCreditInfo();
    }

    @Override
    public void createCredit(String id, String token, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        mTasksRemoteDataSource.createCredit(id, token, body, callback);
    }

    @Override
    public void saveCreditInfo(UserCreditResponse credit) {
        mTasksLocalDataSource.saveCreditInfo(credit);
    }

    @Override
    public Flowable<List<MessageResponse>> queryMessage(String objectId, String accessToken,
        JSONObject where) {
        return mTasksRemoteDataSource.queryMessage(objectId, accessToken, where);
    }

    @Override
    public Flowable<HomeImageDO> queryHomeImages() {
        return mTasksRemoteDataSource.queryHomeImages();
    }

    @Override
    public void deleteMids(String objectId, String accessToken, String creditId, String mId,
        LoadResponseNewCallback<MidsResponse, String> callback) {
        mTasksRemoteDataSource.deleteMids(objectId, accessToken, creditId, mId, callback);
    }

    @Override
    public void createMids(String objectId, String accessToken, String creditId,
        ArrayMap<String, String> body, LoadResponseNewCallback<MidsResponse, String> callback) {
        mTasksRemoteDataSource.createMids(objectId, accessToken, creditId, body, callback);
    }

    @Override
    public void verifyMids(String objectId, String accessToken, String creditId, String verifyId,
        JSONObject body, LoadResponseNewCallback<MidsResponse, String> callback) {
        mTasksRemoteDataSource
            .verifyMids(objectId, accessToken, creditId, verifyId, body, callback);
    }

    @Override
    public void questionMids(String objectId, String accessToken, String creditId, String mId,
        LoadResponseNewCallback<MidsResponse, String> callback) {
        mTasksRemoteDataSource.questionMids(objectId, accessToken, creditId, mId, callback);
    }

    @Override
    public void queryMids(String objectId, String accessToken, String creditId,
        LoadResponseNewCallback<MidsResponse, String> callback) {
        mTasksRemoteDataSource.queryMids(objectId, accessToken, creditId, callback);
    }

    @Override
    public ApplyInfoResponse getApplyInfo() {
        return mTasksLocalDataSource.getApplyInfo();
    }

    @Override
    public void saveApplyInfo(ApplyInfoResponse apply) {
        mTasksLocalDataSource.saveApplyInfo(apply);
    }

    @Override
    public void getApplyInfo(String objectId, String accessToken, String applicationId,
        LoadResponseNewCallback<ApplyInfoResponse, String> callback) {
        mTasksRemoteDataSource.getApplyInfo(objectId, accessToken, applicationId, callback);
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryIndustry(String objectId, String accessToken,
        String where) {
        return mTasksRemoteDataSource.queryIndustry(objectId, accessToken, where);
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryProvince(String objectId, String accessToken,
        String where) {
        return mTasksRemoteDataSource.queryProvince(objectId, accessToken, where);
    }

    @Override
    public Flowable<AddressSearchResponse> queryShopAddress(String address) {
        return mTasksRemoteDataSource.queryShopAddress(address);
    }

    @Override
    public String getIpAddress() {
        return mTasksLocalDataSource.getIpAddress();
    }

    @Override
    public Observable<String> getIpAddress(String url) {
        return mTasksRemoteDataSource.getIpAddress(url);
    }

    @Override
    public void saveIpAddress(String ip) {
        mTasksLocalDataSource.saveIpAddress(ip);
    }

    @Override
    public void saveGpsAddress(String gps) {
        mTasksLocalDataSource.saveGpsAddress(gps);
    }

    @Override
    public Flowable<SplashResponse> checkNewVersion(String channel) {
        return mTasksRemoteDataSource.checkNewVersion(channel);
    }

    @Override
    public String getGpsAddress() {
        return mTasksLocalDataSource.getGpsAddress();
    }

    @Override
    public void createOrFindPwd(String uUid, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        mTasksRemoteDataSource.createOrFindPwd(uUid, body, callback);
    }

    @Override
    public void sendSmsCode(String mobilePhone, String code,
        LoadResponseNewCallback<VerifyCodeResponse, String> callback) {
        mTasksRemoteDataSource.sendSmsCode(mobilePhone, code, callback);
    }

    @Override
    public void updateApplyInfo(String id, String token, String applicationId, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        mTasksRemoteDataSource.updateApplyInfo(id, token, applicationId, body, callback);
    }

    @Override
    public Flowable<List<ShopListsBean>> queryShopLists(String objectId, String accessToken) {
        return mTasksRemoteDataSource.queryShopLists(objectId, accessToken);
    }

    @Override
    public void createOrUpdateUserInfo(String objectId, String accessToken,
        UserInfoNewResponse body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        mTasksRemoteDataSource.createOrUpdateUserInfo(objectId, accessToken, body, callback);
    }

    @Override
    public Flowable<UserInfoNewResponse> getUserInfo(String objectId, String accessToken) {
        return mTasksRemoteDataSource.getUserInfo(objectId, accessToken);
    }

    @Override
    public UserInfoNewResponse getUserInfo() {
        return mTasksLocalDataSource.getUserInfo();
    }

    @Override
    public void saveUserInfo(UserInfoNewResponse user) {
        mTasksLocalDataSource.saveUserInfo(user);
    }

    @Override
    public Flowable<CurrentShopBean> setCurrentShop(String phone, JSONObject id) {
        return mTasksRemoteDataSource.setCurrentShop(phone, id);
    }

    @Override
    public Flowable<CurrentShopBean> queryCurrentShop(String phone) {
        return mTasksRemoteDataSource.queryCurrentShop(phone);
    }

    @Override
    public Flowable<ImeiDO> uploadAddAddressBook(String imei, JSONObject body) {
        return mTasksRemoteDataSource.uploadAddAddressBook(imei, body);
    }

    @Override
    public void clearCache() {
        mTasksLocalDataSource.clearCache();
    }

    @Override
    public String getMobilePhone() {
        return mTasksLocalDataSource.getMobilePhone();
    }

    @Override
    public void saveMobilePhone(String phone) {
        mTasksLocalDataSource.saveMobilePhone(phone);
    }

    @Override
    public Flowable<LoginResponse> userLogout(String id, String token) {
        return mTasksRemoteDataSource.userLogout(id, token);
    }

    @Override
    public void saveLogin(String id) {
        mTasksLocalDataSource.saveLogin(id);
    }

    @Override
    public void saveLogin(String id, String token) {
        mTasksLocalDataSource.saveLogin(id, token);
    }

    @Override
    public void saveLogin(String phone, String id, String token) {
        mTasksLocalDataSource.saveLogin(phone, id, token);
    }

    @Override
    public LoginResponse getLogin() {
        return mTasksLocalDataSource.getLogin();
    }

    @Override
    public Flowable<LoginResponse> getLogin(String... args) {
        return mTasksRemoteDataSource.getLogin(args);
    }

    @Override
    public Flowable<LoginResponse> createAuthorize(JSONObject body) {
        return mTasksRemoteDataSource.createAuthorize(body);
    }

    @Override
    public Flowable<CheckAuthorizeResponse> checkAuthorize(String mobilePhone) {
        return mTasksRemoteDataSource.checkAuthorize(mobilePhone);
    }

}
