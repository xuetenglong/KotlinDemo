package com.cardvlaue.sys.userinfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import javax.inject.Inject;

final class UserInfoPresenter implements UserInfoContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;
    private UserInfoActivity mContext;
    @NonNull
    private UserInfoContract.View mInfoView;
    private boolean isCommit;

    @Inject
    UserInfoPresenter(Context context, @NonNull TasksRepository tasksRepository,
        @NonNull UserInfoContract.View infoView) {
        mContext = (UserInfoActivity) context;
        mTasksRepository = tasksRepository;
        mInfoView = infoView;
    }

    @Inject
    void setupListeners() {
        mInfoView.setPresenter(this);
    }

    @Override
    public boolean checkPhoneIsMe(String phone) {
        return phone.equals(mTasksRepository.getMobilePhone());
    }

    @Override
    public void updateInfo(UserInfoNewResponse userInfo) {
        if (isCommit) {
            ToastUtil.showFailure(mContext, "提交中...");
            return;
        }
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        isCommit = true;
        mInfoView.showLoadingDialog();
        LoginResponse loginResponse = mTasksRepository.getLogin();
        String idStr = loginResponse.objectId;
        String tokenStr = loginResponse.accessToken;
        mTasksRepository.createOrUpdateUserInfo(idStr, tokenStr, userInfo,
            new TasksDataSource.LoadResponseNewCallback<LoginResponse, String>() {
                @Override
                public void onResponseSuccess(LoginResponse updateS) {
                    switch (updateS.responseSuccess(mContext)) {
                        case -1:
                            isCommit = false;
                            mInfoView.dismissLoadingDialog();
                            ToastUtil.showFailure(mContext, updateS.getError());
                            break;
                        case 0:
                            mTasksRepository.getUserInfo(idStr, tokenStr)
                                .compose(mContext.bindToLifecycle())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(userS -> {
                                    switch (userS.responseSuccess(mContext)) {
                                        case -1:
                                            isCommit = false;
                                            mInfoView.dismissLoadingDialog();
                                            ToastUtil.showFailure(mContext, userS.getError());
                                            break;
                                        case 0:
                                            mTasksRepository.saveUserInfo(userS);

                                            String stepStr = mTasksRepository.getSetStep();
                                            if (TextUtils.isEmpty(stepStr)
                                                || Integer.parseInt(stepStr) < 20) {
                                                setCurrentStep(userS.objectId, tokenStr,
                                                    userS.applicationId);
                                            } else {
                                                isCommit = false;
                                                mInfoView.dismissLoadingDialog();
                                                mInfoView.gotoNext();
                                            }
                                            break;
                                    }
                                }, throwable -> {
                                    isCommit = false;
                                    mInfoView.dismissLoadingDialog();
                                    ToastUtil.showFailure(mContext, "更新信息获取失败，请重试");
                                });
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isCommit = false;
                    mInfoView.dismissLoadingDialog();
                    ToastUtil.showFailure(mContext, "个人信息更新失败");
                }
            });
    }

    /**
     * 保存当前步骤
     */
    private void setCurrentStep(String id, String token, String applicationId) {
        JSONObject body = new JSONObject();
        body.put("setStep", "20");

        mTasksRepository.updateApplyInfo(id, token, applicationId,
            body, new TasksDataSource.LoadResponseNewCallback<LoginResponse, String>() {
                @Override
                public void onResponseSuccess(LoginResponse stepS) {
                    isCommit = false;
                    mInfoView.dismissLoadingDialog();
                    if (stepS.responseSuccess(mContext) == -1) {
                        ToastUtil.showFailure(mContext, stepS.getError());
                    } else if (stepS.responseSuccess(mContext) == 0) {
                        mInfoView.gotoNext();
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isCommit = false;
                    mInfoView.dismissLoadingDialog();
                    ToastUtil.showFailure(mContext, "保存步骤失败");
                }
            });
    }

    @Override
    public void loadUserData() {
        Single.just(mTasksRepository.getUserInfo())
            .compose(mContext.bindToLifecycle())
            .filter(userInfoNewResponse -> userInfoNewResponse != null && !TextUtils
                .isEmpty(userInfoNewResponse.objectId))
            .subscribe(userInfoNewResponse -> mInfoView.initFaceData(userInfoNewResponse));
    }

    @Override
    public void start() {
    }

}
