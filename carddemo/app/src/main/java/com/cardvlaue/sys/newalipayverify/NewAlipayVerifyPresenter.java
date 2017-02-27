package com.cardvlaue.sys.newalipayverify;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by cardvalue on 2016/6/27.
 */
class NewAlipayVerifyPresenter implements NewAlipayVerifyContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private final NewAlipayVerifyContract.View mVerifyView;

    @NonNull
    private final Context mContext;

    private CompositeDisposable mDisposables;

    private LoginResponse login;

    private String sessionId;

    private UserInfoNewResponse userInfoNewResponse;

    //private int mCode;

    @Inject
    NewAlipayVerifyPresenter(@NonNull TasksRepository tasksRepository,
        @NonNull NewAlipayVerifyContract.View verifyView, @NonNull Context context) {
        mTasksRepository = tasksRepository;
        mVerifyView = verifyView;
        mContext = context;
        login = mTasksRepository.getLogin();
        userInfoNewResponse = mTasksRepository.getUserInfo();
    }

    @Inject
    void setupListeners() {
        mDisposables = new CompositeDisposable();
        mVerifyView.setPresenter(this);
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }

    @Override
    public void clickCommit() {
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "无可用网络");
            return;
        }
        JSONObject body = new JSONObject();
        body.put("type", "0");
        Timber.e("NewAlipayVerifyPresenter:%s",
            "===========22222222222222===NewAlipayVerifyPresenter==============");
        mDisposables
            .add(mTasksRepository.getAlipayLoginVerify(login.objectId, login.accessToken, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Timber.e("NewAlipayVerifyPresenter:%s", JSON.toJSONString(s));
                    switch (s.responseSuccess(mContext)) {
                        case 0:
                            Timber.e("NewAlipayVerifyPresenter支付宝登录的返回");
                            if (!TextUtils.isEmpty(s.getUrl())) {
                                Timber.e("NewAlipayVerifyPresenter支付宝登录的返回11111111111");
                                mVerifyView.flag(s.getUrl());
                            }
                            Timber.e("NewAlipayVerifyPresenter支付宝登录的返回22222222222211");
                            mVerifyView.closeDialog();
                            Timber.e("NewAlipayVerifyPresenter支付宝登录的返回3333333333");
                            break;
                        case -1:
                            Timber.e("NewAlipayVerifyPresenter支付宝登录的返回4443333");
                            ToastUtil.showFailure(mContext, s.getError());
                            mVerifyView.closeDialog();
                            mVerifyView.failure();
                            break;
                    }
                }, throwable -> {
                    Timber.e("getAlipayLoginVerifyEEE:%s",
                        throwable.getMessage() + JSON.toJSONString(throwable));
                    mVerifyView.closeDialog();
                    mVerifyView.failure();
                    ToastUtil.showFailure(mContext, "服务器超时,请重试");
                }));
    }

    /**
     * 获取支付宝验证状态
     */
    @Override
    public void getalipayStatus() {
        Timber.e("获取支付宝验证状态:" + userInfoNewResponse.applicationId);
        mTasksRepository
            .getalipayStatus(login.objectId, login.accessToken, userInfoNewResponse.applicationId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(alipayVerifyResponse -> {
                Timber.e("NewAlipayVerifyPresenter获取支付宝验证状态:%s",
                    JSON.toJSONString(alipayVerifyResponse));
                switch (alipayVerifyResponse.responseSuccess(mContext)) {
                    case 0:
                        Timber
                            .e("NewAlipayVerifyPresenter获取支付宝验证状态" + JSON
                                .toJSONString(alipayVerifyResponse));
                        if (!TextUtils.isEmpty(alipayVerifyResponse.getStatus())) {
                            mVerifyView.Status(alipayVerifyResponse.getStatus());
                        }
                        break;
                    case -1:
                        ToastUtil.showFailure(mContext, alipayVerifyResponse.getError());
                        break;
                }
            });
    }
}
