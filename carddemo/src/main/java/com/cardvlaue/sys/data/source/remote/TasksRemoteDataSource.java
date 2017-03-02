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

package com.cardvlaue.sys.data.source.remote;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
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
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import timber.log.Timber;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
@Singleton
public class TasksRemoteDataSource implements TasksDataSource {

    private RequestService mService;

    private RequestService mTimeOutService;

    public TasksRemoteDataSource() {
        mService = RequestClient.newJsonClient();
        mTimeOutService = RequestClient.hasTimeOutClient();
    }

    @Override
    public void saveUdid(String udid) {
    }

    @Override
    public String getUdid() {
        return null;
    }

    @Override
    public void saveMerchantId(String merchantId) {
    }

    @Override
    public String getCreditId() {
        return null;
    }

    @Override
    public void saveSetStep(String setStep) {
    }

    @Override
    public String getSetStep() {
        return null;
    }

    @Override
    public void saveCreditId(String merchantId) {
    }

    @Override
    public String getCredCreditId() {
        return null;
    }

    @Override
    public void saveCredCreditId(String credcreditId) {
    }

    @Override
    public String getMerchantId() {
        return null;
    }

    @Override
    public String getObjectId() {
        return null;
    }

    @Override
    public void saveApplicationId(String applicationId) {
    }

    @Override
    public String getApplicationId() {
        return null;
    }

    @Override
    public String getHomeImageData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveHomeImageData(String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersionCheckInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveVersionCheckInfo(String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<String> postFeedback(JSONObject feedback) {
        return mService.postFeedback(feedback).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryAddressCountyLists(@NonNull String objectId,
        @NonNull String accessToken, String where) {
        return mService.queryAddressCountyLists(objectId, accessToken, where)
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryAddressProidLists(@NonNull String objectId,
        @NonNull String accessToken, String where) {
        return mService.queryAddressProidLists(objectId, accessToken, where)
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryAddress(@NonNull String objectId,
        @NonNull String accessToken) {
        return mService.queryAddressLists(objectId, accessToken).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<String> queryFinanceTariff(@NonNull String objectId,
        @NonNull String accessToken) {
        return mService.queryFinanceTariff(objectId, accessToken).subscribeOn(Schedulers.io());
    }

    @Override
    public void getJxlVerify(String objectId, String accessToken, String applicationId,
        JSONObject body, LoadResponseNewCallback<MobilePhoneVerifyResponse, String> callback) {
        mTimeOutService.jxlVerify(objectId, accessToken, applicationId, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mobilePhoneVerifyResponse -> {
                Timber.e("getJxlVerify:%s", JSON.toJSONString(mobilePhoneVerifyResponse));
                callback.onResponseSuccess(mobilePhoneVerifyResponse);
            }, throwable -> {
                Timber.e("getJxlVerifyEEE:%s", throwable.getMessage());
                callback.onResponseFailure("聚信立验证失败");
            });
    }

    @Override
    public void getJxlSubmit(String objectId, String accessToken, String applicationId,
        JSONObject body,
        LoadResponseNewCallback<MobilePhoneVerifyResponse, String> callback) {
        mTimeOutService.jxlSubmit(objectId, accessToken, applicationId, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mobilePhoneVerifyResponse -> {
                Timber.e("getJxlSubmit:%s", JSON.toJSONString(mobilePhoneVerifyResponse));
                callback.onResponseSuccess(mobilePhoneVerifyResponse);
            }, throwable -> {
                Timber.e("getJxlSubmitEEE:%s", throwable.getMessage());
                callback.onResponseFailure("聚信立提交失败");
            });
    }

    @Override
    public Flowable<AlipayVerifyResponse> getAlipayLoginVerify(String objectId,
        String accessToken,
        JSONObject body) {
        return mTimeOutService.alipayLoginVerify(objectId, accessToken, body)
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<AlipayVerifyResponse> getalipayStatus(String objectId, String accessToken,
        String appId) {
        return mTimeOutService.alipayStatus(objectId, accessToken, appId)
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<AlipayVerifyResponse> getAlipayLoginWithCodeVerify(String objectId,
        String accessToken, JSONObject body) {
        return mTimeOutService.getAlipayLoginWithCodeVerify(objectId, accessToken, body)
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<AlipayVerifyResponse> getAlipayResendCodeVerify(String objectId,
        String accessToken, JSONObject body) {
        return mTimeOutService.getAlipayResendCodeVerify(objectId, accessToken, body)
            .subscribeOn(Schedulers.io());
    }

    @Override
    public void getCreditInfo(String objectId, String accessToken, String creditId,
        LoadResponseNewCallback<UserCreditResponse, String> callback) {
        mService.userCredit(objectId, accessToken, creditId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(userCreditResponse -> {
                Timber.e("getCreditInfo:%s", JSON.toJSONString(userCreditResponse));
                callback.onResponseSuccess(userCreditResponse);
            }, throwable -> {
                Timber.e("getCreditInfoEEE:" + throwable.getMessage());
                callback.onResponseFailure("获取授信失败");
            });
    }

    @Override
    public UserCreditResponse getCreditInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveCreditInfo(UserCreditResponse credit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createCredit(String id, String token, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        mService.createCredit(id, token, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginResponse -> {
                Timber.e("createCredit:%s", JSON.toJSONString(loginResponse));
                callback.onResponseSuccess(loginResponse);
            }, throwable -> {
                Timber.e("createCreditEEE:%s", throwable.getMessage());
                callback.onResponseFailure("授信创建失败");
            });
    }

    @Override
    public Flowable<List<MessageResponse>> queryMessage(String objectId, String accessToken,
        JSONObject where) {
        return mService.queryMessage(objectId, accessToken, where, 0, 10)
            .subscribeOn(Schedulers.io());
    }

    public Flowable<HomeImageDO> queryHomeImages() {
        return mService.queryHomeImages("home").subscribeOn(Schedulers.io());
    }

    @Override
    public void deleteMids(String objectId, String accessToken, String creditId, String mId,
        LoadResponseNewCallback<MidsResponse, String> callback) {
        mService.deleteMids(objectId, accessToken, creditId, mId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(callback::onResponseSuccess,
                throwable -> callback.onResponseFailure("删除商编失败"));
    }

    @Override
    public void createMids(String objectId, String accessToken, String creditId,
        ArrayMap<String, String> body, LoadResponseNewCallback<MidsResponse, String> callback) {
        mService.createMids(objectId, accessToken, creditId, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(midsResponse -> {
                Timber.e("createMids:%s", JSON.toJSONString(midsResponse));
                callback.onResponseSuccess(midsResponse);
            }, throwable -> callback.onResponseFailure("商编创建失败"));
    }

    @Override
    public void verifyMids(String objectId, String accessToken, String creditId, String verifyId,
        JSONObject body, LoadResponseNewCallback<MidsResponse, String> callback) {
        mService.verifyMids(objectId, accessToken, creditId, verifyId, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(midsResponse -> {
                Timber.e("verifyMids:%s", JSON.toJSONString(midsResponse));
                callback.onResponseSuccess(midsResponse);
            }, throwable -> callback.onResponseFailure("商编验证问题获取失败"));
    }

    @Override
    public void questionMids(String objectId, String accessToken, String creditId, String mId,
        LoadResponseNewCallback<MidsResponse, String> callback) {
        mService.questionMids(objectId, accessToken, creditId, mId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(midsResponse -> {
                Timber.e("questionMids:%s", JSON.toJSONString(midsResponse));
                callback.onResponseSuccess(midsResponse);
            }, throwable -> callback.onResponseFailure("商编验证问题获取失败"));
    }

    @Override
    public void queryMids(String objectId, String accessToken, String creditId,
        LoadResponseNewCallback<MidsResponse, String> callback) {
        mService.queryMids(objectId, accessToken, creditId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(midsResponse -> {
                Timber.e("queryMids:%s", JSON.toJSONString(midsResponse));
                callback.onResponseSuccess(midsResponse);
            }, throwable -> {
                Timber.e("queryMidsERROR:%s", throwable.getMessage());
                callback.onResponseFailure("商编查询失败");
            });
    }

    @Override
    public ApplyInfoResponse getApplyInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveApplyInfo(ApplyInfoResponse apply) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getApplyInfo(String objectId, String accessToken, String applicationId,
        LoadResponseNewCallback<ApplyInfoResponse, String> callback) {
        mService.queryApplyInfo(objectId, accessToken, applicationId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(applyInfoResponse -> {
                Timber.e("queryApplyInfo:%s", JSON.toJSONString(applyInfoResponse));
                callback.onResponseSuccess(applyInfoResponse);
            }, throwable -> callback.onResponseFailure("申请信息获取失败"));
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryIndustry(String objectId, String accessToken,
        String where) {
        return mService.queryIndustryLists(objectId, accessToken, where)
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<List<SearchQueryDO>> queryProvince(String objectId, String accessToken,
        String where) {
        return mService.queryProvinceData(objectId, accessToken, where)
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<AddressSearchResponse> queryShopAddress(String address) {
        return mService.checkShopAddress(address).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<String> getIpAddress(String url) {
        return Observable.just(url).map(s -> new OkHttpClient()
            .newCall(new Request.Builder().url(s).build()).execute().body().string())
            .subscribeOn(Schedulers.io());
    }

    @Override
    public String getIpAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveIpAddress(String ip) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getGpsAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveGpsAddress(String address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<SplashResponse> checkNewVersion(String channel) {
        return mService.checkNewVersion("android", channel).subscribeOn(Schedulers.io());
    }

    @Override
    public void createOrFindPwd(String uUid, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        mService.createOrFindPwd(uUid, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginResponse -> {
                Timber.e("createOrFindPwd:%s", JSON.toJSONString(loginResponse));
                callback.onResponseSuccess(loginResponse);
            }, throwable -> {
                Timber.e("createOrFindPwdEEE:%s", throwable.getMessage());
                callback.onResponseFailure("注册或修改密码失败");
            });
    }

    @Override
    public void sendSmsCode(String mobilePhone, String code,
        LoadResponseNewCallback<VerifyCodeResponse, String> callback) {
        JSONObject object = new JSONObject();
        object.put("mobilePhone", mobilePhone);
        object.put("imageVerifyCode", code);
        Timber.e("sendSmsCodeJSON:%s", object.toJSONString());

        mService.sendSmsCode(object)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(verifyCodeResponse -> {
                Timber.e("sendSmsCode:%s", JSON.toJSONString(verifyCodeResponse));
                callback.onResponseSuccess(verifyCodeResponse);
            }, throwable -> {
                Timber.e("sendSmsCodeEEE:%s", throwable.getMessage());
                callback.onResponseFailure("验证码发送失败");
            });
    }

    @Override
    public void updateApplyInfo(String id, String token, String applicationId, JSONObject body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        mService.updateApplyInfo(id, token, applicationId, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginResponse -> {
                Timber.e("updateApplyInfo:%s", JSON.toJSONString(loginResponse));
                callback.onResponseSuccess(loginResponse);
            }, throwable -> {
                Timber.e("updateApplyInfoEEE:%s", throwable.getMessage());
                callback.onResponseFailure("申请更新失败");
            });
    }

    @Override
    public Flowable<List<ShopListsBean>> queryShopLists(String objectId, String accessToken) {
        return mService.queryShopLists(objectId, accessToken).subscribeOn(Schedulers.io());
    }

    @Override
    public void createOrUpdateUserInfo(String objectId, String accessToken,
        UserInfoNewResponse body,
        LoadResponseNewCallback<LoginResponse, String> callback) {
        mService.createOrUpdateUserInfo(objectId, accessToken, objectId, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginResponse -> {
                Timber.e("createOrUpdateUserInfo:%s", JSON.toJSONString(loginResponse));
                callback.onResponseSuccess(loginResponse);
            }, throwable -> {
                Timber.e("createOrUpdateUserInfoEEE:%s", throwable.getMessage());
                callback.onResponseFailure("个人信息操作失败");
            });
    }

    @Override
    public Flowable<UserInfoNewResponse> getUserInfo(String objectId, String accessToken) {
        return mService.queryUserInfo(objectId, accessToken, objectId).subscribeOn(Schedulers.io());
    }

    @Override
    public UserInfoNewResponse getUserInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveUserInfo(UserInfoNewResponse user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<CurrentShopBean> setCurrentShop(String phone, JSONObject id) {
        return mService.setCurrentShop(phone, id).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<CurrentShopBean> queryCurrentShop(String phone) {
        return mService.queryCurrentShop(phone).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<ImeiDO> uploadAddAddressBook(String imei, JSONObject body) {
        return mService.uploadAddAddressBook(imei, body).subscribeOn(Schedulers.io());
    }

    @Override
    public void clearCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMobilePhone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveMobilePhone(String phone) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<LoginResponse> userLogout(String id, String token) {
        return mService.userLogout(id, token, id).subscribeOn(Schedulers.io());
    }

    @Override
    public void saveLogin(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveLogin(String id, String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveLogin(String phone, String id, String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LoginResponse getLogin() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Flowable<LoginResponse> getLogin(String... args) {
        return mService.userLogin(args[0], args[1], args[2], args[3]).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<LoginResponse> createAuthorize(JSONObject body) {
        return mService.createAuthorize(body).subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<CheckAuthorizeResponse> checkAuthorize(String mobilePhone) {
        return mService.checkPhoneAuthorize(mobilePhone).subscribeOn(Schedulers.io());
    }

}
