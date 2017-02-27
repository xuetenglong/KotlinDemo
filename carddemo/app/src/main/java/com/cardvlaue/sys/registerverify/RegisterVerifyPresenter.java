package com.cardvlaue.sys.registerverify;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import cn.jpush.android.api.JPushInterface;
import cn.tongdun.android.shell.FMAgent;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.CodeObtainDialog;
import com.cardvlaue.sys.login.LoginPresenter;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.SafeUtil;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;

final class RegisterVerifyPresenter implements RegisterVerifyContract.Presenter {

    /**
     * 验证码已发送，开启倒计时
     */
    static final String BUS_CODE_SENT = "RegisterVerifyPresenter_BUS_CODE_SENT";
    private final TasksDataSource mTasksRepository;
    private final RegisterVerifyContract.View mVerifyView;
    private Context mContext;
    private boolean isCommit;

    private CompositeDisposable mDisposables;

    @Inject
    RegisterVerifyPresenter(Context context, TasksRepository tasksRepository,
        RegisterVerifyContract.View verifyView) {
        mContext = context;
        mTasksRepository = tasksRepository;
        mVerifyView = verifyView;
    }

    @Inject
    void setupListeners() {
        mDisposables = new CompositeDisposable();
        mVerifyView.setPresenter(this);
    }

    @Override
    public void clickReSendCode(String mobilePhone, FragmentManager fm) {
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        if (CheckUtil.isMobilePhone(mobilePhone)) {
            CodeObtainDialog.newInstance(mobilePhone, BUS_CODE_SENT).show(fm, "clickReSendCode");
        } else {
            ToastUtil.showFailure(mContext, "数据异常");
        }
    }

    @Override
    public void clickCommit(String mobilePhone, String pwd, String verifyCode,
        final String inviteCode) {
        if (isCommit) {
            return;
        }

        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        if (!CheckUtil.isMobilePhone(mobilePhone)) {
            ToastUtil.showFailure(mContext, "错误的手机号");
            return;
        }

        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtil.showFailure(mContext, "请输入短信验证码");
            return;
        }

        isCommit = true;
        mVerifyView.showDialog();

        JSONObject aBody = new JSONObject();
        aBody.put("mobilePhone", mobilePhone);
        aBody.put("type", 5);
        aBody.put("time",
            DateFormat.format(LoginPresenter.DATE_FORMAT, System.currentTimeMillis()).toString());
        String ipStr = mTasksRepository.getIpAddress();
        if (!TextUtils.isEmpty(ipStr)) {
            aBody.put("ip", ipStr);
        }
        aBody.put("deviceNumber", DeviceUtil.getDeviceInfo(mContext));
        String gpsStr = mTasksRepository.getGpsAddress();
        if (!TextUtils.isEmpty(gpsStr)) {
            aBody.put("gps", gpsStr);
        }
        aBody.put("agent", DeviceUtil.getUA(mContext));
        mDisposables.add(mTasksRepository.createAuthorize(aBody)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginResponse -> {
                switch (loginResponse.responseSuccess(mContext)) {
                    case -1:
                        isCommit = false;
                        mVerifyView.dismissDialog();
                        ToastUtil.showFailure(mContext, loginResponse.getError());
                        break;
                    case 0:
                        JSONObject rBody = new JSONObject();
                        rBody.put("mobilePhone", mobilePhone);
                        rBody.put("password", SafeUtil.createMD5(pwd));
                        rBody.put("pushId", JPushInterface.getRegistrationID(mContext));
                        rBody.put("type", "1");
                        rBody.put("mobilePhoneVerifyCode", verifyCode);
                        if (CheckUtil.isMobilePhone(inviteCode)) {
                            rBody.put("inviteCode", inviteCode);
                        }
                        String ipAddress = mTasksRepository.getIpAddress();
                        if (!TextUtils.isEmpty(ipAddress)) {
                            rBody.put("ipAddress", ipAddress);
                        }
                        rBody.put("blackBox", FMAgent.onEvent(mContext));
                        String uUid;
                        if (TextUtils.isEmpty(mTasksRepository.getUdid())) {
                            uUid = "";
                        } else {
                            uUid = mTasksRepository.getUdid();
                        }
                        mTasksRepository.createOrFindPwd(uUid, rBody,
                            new TasksDataSource.LoadResponseNewCallback<LoginResponse, String>() {
                                @Override
                                public void onResponseSuccess(LoginResponse s) {
                                    isCommit = false;
                                    mVerifyView.dismissDialog();
                                    switch (s.responseSuccess(mContext)) {
                                        case -1:
                                            ToastUtil.showFailure(mContext, s.getError());
                                            break;
                                        case 0:
                                            mTasksRepository
                                                .saveLogin(mobilePhone, s.objectId, s.accessToken);
                                            mVerifyView.registerSuccess();
                                            break;
                                    }
                                }

                                @Override
                                public void onResponseFailure(String f) {
                                    isCommit = false;
                                    mVerifyView.dismissDialog();
                                    ToastUtil.showFailure(mContext, f);
                                }
                            });
                        break;
                }
            }, throwable -> {
                isCommit = false;
                mVerifyView.dismissDialog();
                ToastUtil.showFailure(mContext, "接受服务协议失败");
            }));
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}
