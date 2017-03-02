package com.cardvlaue.sys.financeintention;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.ApplyInfoResponse;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.shopadd.ShopAddRequest;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;
import timber.log.Timber;

final class FinanceIntentionPresenter implements FinanceIntentionContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;
    private Context mContext;
    @NonNull
    private FinanceIntentionContract.View mIntentionView;

    private CompositeDisposable mDisposables;

    private boolean isCommit;
    private boolean isLoading;

    @Inject
    FinanceIntentionPresenter(Context context, @NonNull TasksRepository tasksRepository,
        @NonNull FinanceIntentionContract.View intentionView) {
        mContext = context;
        mTasksRepository = tasksRepository;
        mIntentionView = intentionView;
    }

    /**
     * 加载店铺列表
     */
    private void loadShopListsData() {
        if (isLoading) {
            return;
        }
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }
        LoginResponse loginResponse = mTasksRepository.getLogin();
        String idStr = loginResponse.objectId;
        Timber.e("objectId==================:%s", idStr);
        if (!TextUtils.isEmpty(idStr)) {
            isLoading = true;
            mDisposables.add(mTasksRepository.queryShopLists(idStr, loginResponse.accessToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shopListsBeen -> {
                    Timber.e("queryShopLists:%s", JSON.toJSONString(shopListsBeen));
                    isLoading = false;
                    if (shopListsBeen.isEmpty()) {
                        mIntentionView.changeShopStatus(true);
                    } else {
                        mIntentionView.changeShopStatus(false);
                    }
                }, throwable -> {
                    Timber.e("queryShopListsEEE:%s", throwable.getMessage());
                    isLoading = false;
                }));
        }
    }

    @Override
    public void updateUserInfo(String shopStr, String useStr, String loanAmount,
        String planFundTerm,
        String gift) {
        if (isCommit) {
            return;
        }

        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        LoginResponse loginResponse = mTasksRepository.getLogin();
        // 是否登录
        String idStr = loginResponse.objectId;
        if (TextUtils.isEmpty(idStr) || TextUtils.isEmpty(shopStr)) {
            ToastUtil.showFailure(mContext, "请选择融资店铺");
            return;
        }
        if (TextUtils.isEmpty(useStr) || "请选择".equals(useStr)) {
            ToastUtil.showFailure(mContext, "请选择融资用途");
            return;
        }
        if (TextUtils.isEmpty(loanAmount)) {
            ToastUtil.showFailure(mContext, "融资金额不能为空");
            return;
        }
        UserInfoNewResponse userInfoNewResponse = mTasksRepository.getUserInfo();
        // 经纬度不能为空
        if (TextUtils.isEmpty(userInfoNewResponse.bizAddrLonlat)) {
            mIntentionView.showPromoteAmountDialog("您所选融资店铺的经营地址不合法，请修改后再进行下一步操作！", idStr);
            return;
        }
        // 是否有租赁合同不能为空
        if (TextUtils.isEmpty(userInfoNewResponse.hasLeaseContract)) {
            mIntentionView.showPromoteAmountDialog("您所选融资店铺的是否有租赁合同不能为空，请修改后再进行下一步操作！", idStr);
            return;
        }

        isCommit = true;
        mIntentionView.showLoadingDialog();

        ShopAddRequest shopInfo = new ShopAddRequest();
        shopInfo.isCreate = "0";
        shopInfo.loanPurpose = useStr;
        shopInfo.loanAmount = loanAmount;
        shopInfo.planFundTerm = planFundTerm;
        shopInfo.couponIds = gift;
        Timber.e("updateUserInfo:%s", JSON.toJSONString(shopInfo));

        String tokenStr = loginResponse.accessToken;
        mTasksRepository.createOrUpdateUserInfo(idStr, tokenStr, shopInfo,
            new TasksDataSource.LoadResponseNewCallback<LoginResponse, String>() {
                @Override
                public void onResponseSuccess(LoginResponse updateS) {
                    switch (updateS.responseSuccess(mContext)) {
                        case -1:
                            isCommit = false;
                            mIntentionView.dismissLoadingDialog();
                            ToastUtil.showFailure(mContext, updateS.getError());
                            break;
                        case 0:
                            Timber.e("创建或更新用户信息数据=====updateUserInfo:%s", JSON.toJSONString(updateS));
                            UserInfoNewResponse userInfo = mTasksRepository.getUserInfo();
                            String userIdStr = userInfo.objectId;
                            String appIdStr = userInfo.applicationId;
                            if (!TextUtils.isEmpty(userIdStr) && !TextUtils.isEmpty(appIdStr)) {
                                mTasksRepository.getApplyInfo(userIdStr, tokenStr, appIdStr,
                                    new TasksDataSource.LoadResponseNewCallback<ApplyInfoResponse, String>() {
                                        @Override
                                        public void onResponseSuccess(ApplyInfoResponse s) {
                                            switch (s.responseSuccess(mContext)) {
                                                case -1:
                                                    isCommit = false;
                                                    mIntentionView.dismissLoadingDialog();
                                                    ToastUtil.showFailure(mContext, s.getError());
                                                    break;
                                                case 0:
                                                    if (TextUtils.isEmpty(s.creditId)) {
                                                        createCredit(userIdStr, tokenStr, appIdStr);
                                                    } else {
                                                        String stepStr = mTasksRepository
                                                            .getSetStep();
                                                        if (TextUtils.isEmpty(stepStr)
                                                            || Integer.parseInt(stepStr) < 10) {
                                                            setCurrentStep(userIdStr, tokenStr,
                                                                appIdStr);
                                                        } else {
                                                            isCommit = false;
                                                            mIntentionView.dismissLoadingDialog();
                                                            mIntentionView.gotoNext();
                                                        }
                                                    }
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onResponseFailure(String f) {
                                            isCommit = false;
                                            mIntentionView.dismissLoadingDialog();
                                            ToastUtil.showFailure(mContext, f);
                                        }
                                    });
                            } else {
                                isCommit = false;
                                mIntentionView.dismissLoadingDialog();
                                ToastUtil.showFailure(mContext, "请重新选择店铺");
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isCommit = false;
                    mIntentionView.dismissLoadingDialog();
                    ToastUtil.showFailure(mContext, "提交失败");
                }
            });
    }

    @Inject
    void setupListeners() {
        mDisposables = new CompositeDisposable();
        mIntentionView.setPresenter(this);
    }

    @Override
    public void loadUserData() {
        UserInfoNewResponse userInfo = mTasksRepository.getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.objectId)) {
            mIntentionView.initFaceData(userInfo);
        }
    }

    /**
     * 保存当前步骤
     */
    private void setCurrentStep(String id, String token, String applicationId) {
        JSONObject body = new JSONObject();
        body.put("setStep", "10");

        mTasksRepository.updateApplyInfo(id, token, applicationId,
            body, new TasksDataSource.LoadResponseNewCallback<LoginResponse, String>() {
                @Override
                public void onResponseSuccess(LoginResponse stepS) {
                    isCommit = false;
                    mIntentionView.dismissLoadingDialog();
                    if (stepS.responseSuccess(mContext) == -1) {
                        ToastUtil.showFailure(mContext, stepS.getError());
                    } else if (stepS.responseSuccess(mContext) == 0) {
                        mIntentionView.gotoNext();
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isCommit = false;
                    mIntentionView.dismissLoadingDialog();
                    ToastUtil.showFailure(mContext, "保存步骤失败");
                }
            });
    }

    /**
     * 创建授信
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param appId applicationId
     */
    private void createCredit(String objectId, String accessToken, String appId) {
        JSONObject body = new JSONObject();
        body.put("applicationId", appId);
        Timber.e("createCredit-applicationId:%s", JSON.toJSONString(body));

        mTasksRepository.createCredit(objectId, accessToken, body,
            new TasksDataSource.LoadResponseNewCallback<LoginResponse, String>() {
                @Override
                public void onResponseSuccess(LoginResponse s) {
                    switch (s.responseSuccess(mContext)) {
                        case -1:
                            isCommit = false;
                            mIntentionView.dismissLoadingDialog();
                            ToastUtil.showFailure(mContext, s.getError());
                            break;
                        case 0:
                            String stepStr = mTasksRepository.getSetStep();
                            if (TextUtils.isEmpty(stepStr) || Integer.parseInt(stepStr) < 10) {
                                setCurrentStep(objectId, accessToken, appId);
                            } else {
                                isCommit = false;
                                mIntentionView.dismissLoadingDialog();
                                mIntentionView.gotoNext();
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isCommit = false;
                    mIntentionView.dismissLoadingDialog();
                    ToastUtil.showFailure(mContext, f);
                }
            });
    }

    @Override
    public void subscribe() {
        loadShopListsData();
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}
