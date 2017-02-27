package com.cardvlaue.sys.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import cn.jpush.android.api.JPushInterface;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.CodeObtainDialog;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.SafeUtil;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;
import timber.log.Timber;

public final class LoginPresenter implements LoginContract.Presenter {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static final String BUS_CODE_SENT = "LoginPresenter_BUS_CODE_SENT";
    @NonNull
    private final TasksDataSource mTasksRepository;
    private Context mContext;
    @NonNull
    private LoginContract.View mLoginView;

    private CompositeDisposable mDisposables;

    /**
     * 提交中
     */
    private boolean isCommit;

    @Inject
    LoginPresenter(Context context, @NonNull TasksRepository tasksRepository,
        @NonNull LoginContract.View loginView) {
        mContext = context;
        mTasksRepository = tasksRepository;
        mLoginView = loginView;
    }

    @Inject
    void setupListeners() {
        mDisposables = new CompositeDisposable();
        mLoginView.setPresenter(this);
    }

    @Override
    public void clickCommit(String mobilePhone, String pwd, FragmentManager manager, boolean type) {
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }
        if (TextUtils.isEmpty(mobilePhone)) {
            ToastUtil.showFailure(mContext, "请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showFailure(mContext, "请输入密码");
            return;
        }
        if (!CheckUtil.isMobilePhone(mobilePhone)) {
            ToastUtil.showFailure(mContext, "错误的手机号");
            return;
        }
        if (pwd.length() < 6 || pwd.length() > 20) {
            ToastUtil.showFailure(mContext, "密码长度不合法");
            return;
        }
        if (!mLoginView.checkAgreementBox()) {
            ToastUtil.showFailure(mContext, "未同意服务协议");
            return;
        }

        if (type) {
            CodeObtainDialog.newInstance(mobilePhone, BUS_CODE_SENT)
                .show(manager, "CodeObtainDialog");
        } else {
            if (isCommit) {
                return;
            }

            isCommit = true;
            mLoginView.showLoadingDialog();
            checkAgreementOk(mobilePhone, pwd);
        }
    }

    /**
     * 检查是否接受服务协议
     *
     * @param mobilePhone 手机号
     * @param password 密码
     */
    private void checkAgreementOk(String mobilePhone, String password) {
        String pushIdStr = JPushInterface.getRegistrationID(mContext);
        Timber.e("JPushInterface:%s", pushIdStr);
        if (!mLoginView.checkAgreementShown()) {
            requestLogin(mobilePhone, password, pushIdStr);
        } else {
            JSONObject rBody = new JSONObject();
            rBody.put("mobilePhone", mobilePhone);
            rBody.put("type", 5);
            rBody
                .put("time", DateFormat.format(DATE_FORMAT, System.currentTimeMillis()).toString());
            String ipStr = mTasksRepository.getIpAddress();
            if (!TextUtils.isEmpty(ipStr)) {
                rBody.put("ip", ipStr);
            }
            rBody.put("deviceNumber", DeviceUtil.getDeviceInfo(mContext));
            String gpsStr = mTasksRepository.getGpsAddress();
            if (!TextUtils.isEmpty(gpsStr)) {
                rBody.put("gps", gpsStr);
            }
            rBody.put("agent", DeviceUtil.getUA(mContext));
            mDisposables.add(mTasksRepository.createAuthorize(rBody)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    Timber.e("createAuthorize:%s", JSON.toJSONString(loginResponse));
                    requestLogin(mobilePhone, password, pushIdStr);
                }, throwable -> {
                    isCommit = false;
                    ToastUtil.showFailure(mContext, "接受服务协议失败");
                }));
        }
    }

    /**
     * 登录请求
     *
     * @param mobilePhone 手机号
     * @param password 密码
     * @param pushId 极光推送 ID
     */
    private void requestLogin(String mobilePhone, String password, String pushId) {
        long timestamp = System.currentTimeMillis() / 1000;
        String cipherPwd =
            SafeUtil.createMD5(SafeUtil.createMD5(password) + "cvbaoli" + timestamp) + "|"
                + timestamp;
        String uUid;
        if (TextUtils.isEmpty(mTasksRepository.getUdid())) {
            uUid = "";
        } else {
            uUid = mTasksRepository.getUdid();
        }
        mDisposables.add(mTasksRepository.getLogin(uUid, mobilePhone, cipherPwd, pushId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginResponse -> {
                Timber.e("getLogin:%s", JSON.toJSONString(loginResponse));
                switch (loginResponse.responseSuccess(mContext)) {
                    case -1:
                        isCommit = false;
                        mLoginView.dismissLoadingDialog();
                        ToastUtil.showFailure(mContext, loginResponse.getError());
                        break;
                    case 0:
                        isCommit = false;
                        mTasksRepository
                            .saveLogin(mobilePhone, loginResponse.objectId,
                                loginResponse.accessToken);
                        mLoginView.dismissLoadingDialog();
                        mLoginView.loginSuccess();
                        break;
                }
            }, throwable -> {
                Timber.e("getLoginEEE:%s", throwable.getMessage());
                isCommit = false;
                mLoginView.dismissLoadingDialog();
                ToastUtil.showFailure(mContext, "登录异常");
            }));
    }

    @Override
    public void verifyMobilePhoneAuthorize(String mobilePhone, boolean type) {
        mLoginView.changeAgreementShown(View.GONE);

        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        if (CheckUtil.isMobilePhone(mobilePhone)) {
            mDisposables.add(mTasksRepository.checkAuthorize(mobilePhone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(checkAuthorizeResponse -> {
                    Timber.e("checkAuthorize:%s", JSON.toJSONString(checkAuthorizeResponse));
                    if (0 == checkAuthorizeResponse.responseSuccess(mContext)) {
                        if (!type && checkAuthorizeResponse.notAuthorize()) {
                            mLoginView.changeAgreementShown(View.VISIBLE);
                        }
                    }
                }, throwable -> Timber
                    .e("verifyMobilePhoneAuthorizeEEE:%s", throwable.getMessage())));
        }
    }

    @Override
    public void subscribe() {
        mLoginView.initMobilePhone(mTasksRepository.getMobilePhone());
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }

}
