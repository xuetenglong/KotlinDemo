package com.cardvlaue.sys.alipayverify;

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
class AlipayVerifyPresenter implements AlipayVerifyContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private final AlipayVerifyContract.View mVerifyView;

    @NonNull
    private final Context mContext;

    private CompositeDisposable mDisposables;

    private String sessionId;

    //private int mCode;

    @Inject
    AlipayVerifyPresenter(@NonNull TasksRepository tasksRepository,
        @NonNull AlipayVerifyContract.View verifyView, @NonNull Context context) {
        mTasksRepository = tasksRepository;
        mVerifyView = verifyView;
        mContext = context;
    }

    @Inject
    void setupListeners() {
        mDisposables = new CompositeDisposable();
        mVerifyView.setPresenter(this);
    }

    @Override
    public void clickCommit(@NonNull String phone, @NonNull String pwd, String code) {
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "无可用网络");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showFailure(mContext, "登录名不能为空");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showFailure(mContext, "密码不能为空");
            return;
        }

        LoginResponse login = mTasksRepository.getLogin();
        UserInfoNewResponse userInfoNewResponse = mTasksRepository.getUserInfo();
        if (login != null && userInfoNewResponse != null) {
            mVerifyView.showLoadingDialog();
            if (!TextUtils.isEmpty(sessionId)) {//这个需要验证码的时候
                JSONObject body = new JSONObject();
                body.put("sessionId", sessionId);
                body.put("payCode", code);
                mDisposables.add(
                    mTasksRepository
                        .getAlipayLoginWithCodeVerify(login.objectId, login.accessToken, body)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s -> {
                            Timber.e("getAlipayLoginWithCodeVerify:%s", JSON.toJSONString(s));
                            mVerifyView.colseDialog();
                            switch (s.responseSuccess(mContext)) {
                                case 0:
                                    Timber.e("需要验证码的时候支付宝验证码登录的返回" + JSON.toJSONString(s));
                                    if (!TextUtils.isEmpty(s.getUpdatedAt())) {
                                        mVerifyView.colseActivity();
                                    }
                                    if (!TextUtils.isEmpty(s.getSessionId())) {
                                        sessionId = s.getSessionId();
                                        mVerifyView.codevisible();
                                    }
                                    break;
                                case -1:
                                    if (s.getCode() == 305 || s.getCode() == 306
                                        || s.getCode() == 307
                                        || s.getCode() == 307) {
                                        sessionId = "";
                                    }
                                    ToastUtil.showFailure(mContext, s.getError());
                                    break;
                            }
                        }, throwable -> {
                            Timber.e("getAlipayLoginWithCodeVerifyERR:%s",
                                throwable.getMessage() + JSON.toJSONString(throwable));
                            mVerifyView.colseDialog();
                            ToastUtil.showFailure(mContext, "支付宝验证码登录失败");
                        }));
            } else {
                JSONObject body = new JSONObject();
                body.put("username", phone);
                body.put("password", pwd);
                Timber.e("objectId:%s", login.objectId);
                mDisposables
                    .add(mTasksRepository
                        .getAlipayLoginVerify(login.objectId, login.accessToken, body)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s -> {
                            Timber.e("getAlipayLoginVerify:%s", JSON.toJSONString(s));
                            mVerifyView.colseDialog();
                            switch (s.responseSuccess(mContext)) {
                                case 0:
                                    Timber.e("支付宝登录的返回" + JSON.toJSONString(s));
                                    if (!TextUtils.isEmpty(s.getUpdatedAt())) {
                                        mVerifyView.colseActivity();
                                    }
                                    if (!TextUtils.isEmpty(s.getSessionId())) {
                                        sessionId = s.getSessionId();
                                        mVerifyView.codevisible();
                                    }
                                    break;
                                case -1:
                                    ToastUtil.showFailure(mContext, s.getError());
                                    break;
                            }
                        }, throwable -> {
                            Timber.e("getAlipayLoginVerifyEEE:%s",
                                throwable.getMessage() + JSON.toJSONString(throwable));
                            mVerifyView.colseDialog();
                            ToastUtil.showFailure(mContext, "支付宝登录失败");
                        }));
            }
        }
    }

    @Override
    public void resendCode() {
        LoginResponse login = mTasksRepository.getLogin();
        UserInfoNewResponse userInfoNewResponse = mTasksRepository.getUserInfo();
        if (login != null && userInfoNewResponse != null) {
            mVerifyView.showLoadingDialog();
            if (!TextUtils.isEmpty(sessionId)) {
                JSONObject body = new JSONObject();
                body.put("sessionId", sessionId);
                mDisposables
                    .add(mTasksRepository
                        .getAlipayResendCodeVerify(login.objectId, login.accessToken, body)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(alipayVerifyResponse -> {
                            Timber.e("getAlipayResendCodeVerify:%s",
                                JSON.toJSONString(alipayVerifyResponse));
                            mVerifyView.colseDialog();
                            switch (alipayVerifyResponse.responseSuccess(mContext)) {
                                case 0:
                                    Timber.e("支付宝重新发送验证码的返回" + JSON
                                        .toJSONString(alipayVerifyResponse));
                                    /*if (s.getCode() == 309) {
                                        mVerifyView.resendCode();
                                    }*/
                                    break;
                                case -1:
                                    ToastUtil
                                        .showFailure(mContext, alipayVerifyResponse.getError());
                                    Timber.e("支付宝重新发送验证码的返回-1" + JSON
                                        .toJSONString(alipayVerifyResponse));
                                    break;
                            }
                        }, throwable -> {
                            Timber.e("getAlipayResendCodeVerifyERR:%s", throwable.getMessage());
                            mVerifyView.colseDialog();
                            ToastUtil.showFailure(mContext, "支付宝重新发送验证码失败");
                        }));
            }
        }
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}
