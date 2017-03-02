package com.cardvlaue.sys.mobileverify;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.MobilePhoneVerifyResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.util.CheckUtil;
import javax.inject.Inject;
import timber.log.Timber;

final class MobileVerifyPresenter implements MobileVerifyContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private MobileVerifyContract.View mVerifyView;

    private String token, website;

    private int processCode;

    @Inject
    MobileVerifyPresenter(@NonNull TasksRepository tasksRepository,
        @NonNull MobileVerifyContract.View verifyView) {
        mTasksRepository = tasksRepository;
        mVerifyView = verifyView;
    }

    @Inject
    void setupListeners() {
        mVerifyView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void clickCommit(@NonNull Context context, @NonNull String mobilePhone,
        @NonNull String pwd, String code) {
        if (!CheckUtil.isOnline(context)) {
            mVerifyView.showFailureMsg("无可用网络");
            return;
        }

        if (TextUtils.isEmpty(mobilePhone)) {
            mVerifyView.showFailureMsg("手机号码不能为空");
            return;
        } else if (mobilePhone.length() != 11) {
            mVerifyView.showFailureMsg("手机号码为11位");
            return;
        }

        if (!CheckUtil.isMobilePhone(mobilePhone)) {
            mVerifyView.showFailureMsg("不支持的手机号");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            mVerifyView.showFailureMsg("服务密码不能为空");
            return;
        }

        LoginResponse login = mTasksRepository.getLogin();
        UserInfoNewResponse userInfoNewResponse = mTasksRepository.getUserInfo();
        if (login != null && userInfoNewResponse != null) {
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(website)) {
                JSONObject body = new JSONObject();
                body.put("mobilePhone", mobilePhone);
                body.put("password", pwd);
                body.put("token", token);
                body.put("website", website);
                body.put("captcha", code);
                if (processCode == 10022) {
                    body.put("queryPwd", code);
                }

                Timber.e("clickCommit:" + body.toJSONString());
                mTasksRepository
                    .getJxlSubmit(login.objectId, login.accessToken,
                        userInfoNewResponse.applicationId,
                        body,
                        new TasksDataSource.LoadResponseNewCallback<MobilePhoneVerifyResponse, String>() {
                            @Override
                            public void onResponseSuccess(MobilePhoneVerifyResponse s) {
                                switch (s.responseSuccess(context)) {
                                    case 0:
                                        if (s.getProcessCode() == 10008) {
                                            mVerifyView.showSuccessMsg(s.getContent());
                                            // getUserInfo();
                                        } else if (s.getProcessCode() == 10002) {
                                            mVerifyView.showFailureMsg(s.getContent());
                                            mVerifyView.showVerifyCode();
                                        } else if (s.getProcessCode() == 10022) {
                                            processCode = 10022;
                                            mVerifyView.showFailureMsg(s.getContent());
                                            mVerifyView.showqueryPwd();
                                        } else {
                                            mVerifyView.showFailureMsg(s.getContent());
                                        }
                                        break;
                                    case -1:
                                        mVerifyView.showFailureMsg(s.getError());
                                        break;
                                }
                            }

                            @Override
                            public void onResponseFailure(String f) {
                            }
                        });
            } else {
                JSONObject body = new JSONObject();
                body.put("ownerName", userInfoNewResponse.ownerName);
                body.put("ownerSSN", userInfoNewResponse.ownerSSN);
                Timber.e("手机验证，从用户对象里面取值ownerName"+userInfoNewResponse.ownerName+"ownerSSN"+userInfoNewResponse.ownerSSN);

              /*  body.put("ownerName", "段杰");
                body.put("ownerSSN", "43042619931013954X");*/
                body.put("mobilePhone", mobilePhone);
                body.put("password", pwd);

                Timber.e("clickCommit:" + body.toJSONString());

                mTasksRepository
                    .getJxlVerify(login.objectId, login.accessToken,
                        userInfoNewResponse.applicationId,
                        body,
                        new TasksDataSource.LoadResponseNewCallback<MobilePhoneVerifyResponse, String>() {
                            @Override
                            public void onResponseSuccess(MobilePhoneVerifyResponse s) {
                                switch (s.responseSuccess(context)) {
                                    case 0:
                                        if (s.getProcessCode() == 10008) {
                                            mVerifyView.showSuccessMsg(s.getContent());
                                            // getUserInfo();
                                        } else if (s.getProcessCode() == 10002) {
                                            mVerifyView.showFailureMsg(s.getContent());
                                            mVerifyView.showVerifyCode();

                                            token = s.getToken();
                                            website = s.getWebsite();
                                        } else if (s.getProcessCode() == 10022) {
                                            processCode = 10022;
                                            mVerifyView.showFailureMsg(s.getContent());
                                            mVerifyView.showqueryPwd();
                                        } else {
                                            mVerifyView.showFailureMsg(s.getContent());
                                        }
                                        break;
                                    case -1:
                                        mVerifyView.showFailureMsg(s.getError());
                                        break;
                                }
                            }

                            @Override
                            public void onResponseFailure(String f) {
                                mVerifyView.showFailureMsg(f);
                            }
                        });
            }
        }
    }
}
