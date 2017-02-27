package com.cardvlaue.sys.forgotpwd;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import cn.tongdun.android.shell.FMAgent;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.CodeObtainDialog;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.SafeUtil;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;

class ForgotPwdPresenter implements ForgotPwdContract.Presenter {

    static final String BUS_CODE = "ForgotPwdPresenter_BUS_CODE";

    @NonNull
    private final Context mContext;

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private ForgotPwdContract.View mPwdView;

    private CompositeDisposable mSubscriptions;

    @Inject
    ForgotPwdPresenter(@NonNull Context context, @NonNull TasksRepository tasksRepository,
        @NonNull ForgotPwdContract.View pwdView) {
        mContext = context;
        mTasksRepository = tasksRepository;
        mPwdView = pwdView;
        mSubscriptions = new CompositeDisposable();
    }

    @Inject
    void setupListeners() {
        mPwdView.setPresenter(this);
    }

    @Override
    public void clickCode(@NonNull String mobilePhone, FragmentManager manager) {
        if (!CheckUtil.isOnline(mContext)) {
            mPwdView.showFailMsg("无可用网络");
            return;
        }

        if (CheckUtil.isMobilePhone(mobilePhone)) {
            CodeObtainDialog.newInstance(mobilePhone, BUS_CODE).show(manager, "clickCode");
        } else {
            mPwdView.showFailMsg("非法手机号");
        }
    }

    @Override
    public void clickCommit(@NonNull String mobilePhone, @NonNull String code,
        @NonNull String pwd) {
        if (!CheckUtil.isOnline(mContext)) {
            mPwdView.showFailMsg("无可用网络");
            return;
        }

        if (!CheckUtil.isMobilePhone(mobilePhone)) {
            mPwdView.showFailMsg("非法手机号");
        } else {
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(pwd) || pwd.length() < 6
                || pwd.length() > 20) {
                mPwdView.showFailMsg("非法参数");
                return;
            }

            mPwdView.showDialog();

            JSONObject rBody = new JSONObject();
            rBody.put("mobilePhone", mobilePhone);
            rBody.put("password", SafeUtil.createMD5(pwd));
            rBody.put("pushId", JPushInterface.getRegistrationID(mContext));
            rBody.put("type", "2");
            rBody.put("mobilePhoneVerifyCode", code);
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
                        mPwdView.dismissDialog();

                        switch (s.responseSuccess(mContext)) {
                            case 0:
                                mPwdView.showSuccessMsg("密码已修改");
                                mPwdView.finishActivity();
                                break;
                            case -1:
                                mPwdView.showFailMsg(s.getError());
                                break;
                        }
                    }

                    @Override
                    public void onResponseFailure(String f) {
                        mPwdView.dismissDialog();
                        mPwdView.showFailMsg(f);
                    }
                });
        }
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
