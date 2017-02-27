package com.cardvlaue.sys.shopadd;

import android.content.Context;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.FinanceIntentBus;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.financeintention.FinanceIntentionFragment;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import javax.inject.Inject;
import timber.log.Timber;

final class ShopAddPresenter implements ShopAddContract.Presenter {

    private final TasksRepository mTasksRepository;
    private ShopAddActivity mContext;
    private ShopAddContract.View mAddView;

    private boolean isCommit;

    @Inject
    ShopAddPresenter(Context context, TasksRepository tasksRepository,
        ShopAddContract.View addView) {
        mContext = (ShopAddActivity) context;
        mTasksRepository = tasksRepository;
        mAddView = addView;
    }

    @Inject
    void setupListeners() {
        mAddView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void commitShopInfo(String id, ShopAddRequest info) {
        if (isCommit) {
            return;
        }
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        isCommit = true;
        mAddView.showLoadingDialog();

        LoginResponse login = mTasksRepository.getLogin();
        if (TextUtils.isEmpty(id)) {
            id = login.objectId;
        }
        Timber.e("commitShopInfo:%s||%s", id, JSON.toJSONString(info));
        String finalId = id;
        mTasksRepository.createOrUpdateUserInfo(finalId, login.accessToken, info,
            new TasksDataSource.LoadResponseNewCallback<LoginResponse, String>() {
                @Override
                public void onResponseSuccess(LoginResponse s) {
                    switch (s.responseSuccess(mContext)) {
                        case -1:
                            isCommit = false;
                            mAddView.dismissLoadingDialog();
                            ToastUtil.showFailure(mContext, s.getError());
                            break;
                        case 0:
                            String createIdStr = s.objectId;
                            if (!TextUtils.isEmpty(createIdStr)) {
                                ToastUtil.showSuccess(mContext, "店铺创建成功");
                                String createTokenStr = s.accessToken;
                                Timber.e("=======店铺创建成功======="+createIdStr+"======"+createTokenStr);
                                mTasksRepository.saveLogin(createIdStr, createTokenStr);
                                loadUserInfoCreate(createIdStr, createTokenStr);
                                createCredit(createIdStr, createTokenStr, s.applicationId);
                            } else {
                                ToastUtil.showSuccess(mContext, "店铺更新成功");
                                loadUserInfoUpdate(finalId, login.accessToken);
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isCommit = false;
                    mAddView.dismissLoadingDialog();
                    ToastUtil.showFailure(mContext, f);
                }
            });
    }

    private void loadUserInfoUpdate(String objectId, String accessToken) {
        mTasksRepository.getUserInfo(objectId, accessToken)
            .compose(mContext.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                isCommit = false;
                mAddView.dismissLoadingDialog();
                switch (s.responseSuccess(mContext)) {
                    case -1:
                        ToastUtil.showFailure(mContext, s.getError());
                        break;
                    case 0:
                        mTasksRepository.saveUserInfo(s);
                        mAddView.updateSuccess();
                        break;
                }
            }, throwable -> {
                isCommit = false;
                mAddView.dismissLoadingDialog();
                ToastUtil.showFailure(mContext, "获取店铺信息失败");
            });
    }

    /**
     * 创建成功，获取用户
     */
    private void loadUserInfoCreate(String objectId, String accessToken) {
        mTasksRepository.getUserInfo(objectId, accessToken)
            .compose(mContext.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                isCommit = false;
                mAddView.dismissLoadingDialog();
                switch (s.responseSuccess(mContext)) {
                    case -1:
                        ToastUtil.showFailure(mContext, s.getError());
                        break;
                    case 0:
                        Timber.e("=======创建店铺成功，获取用户=========="+JSON.toJSONString(s));
                        mTasksRepository.saveUserInfo(s);
                        RxBus.getDefaultBus()
                            .send(new FinanceIntentBus(FinanceIntentionFragment.BUS_SHOP_DATA,
                                s.corporateName, s.loanPurpose, s.loanAmount,
                                s.planFundTerm));
                        mAddView.createSuccess();
                        break;
                }
            }, throwable -> {
                isCommit = false;
                mAddView.dismissLoadingDialog();
                ToastUtil.showFailure(mContext, "获取店铺信息失败");
            });
    }

    @Override
    public void obtainShopInfo(String objectId) {
        if (TextUtils.isEmpty(objectId)) {
            ToastUtil.showFailure(mContext, "数据异常，请重试");
        } else {
            LoginResponse login = mTasksRepository.getLogin();
            mTasksRepository.getUserInfo(objectId, login.accessToken)
                .compose(mContext.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (s.responseSuccess(mContext) == 0) {
                        mAddView.initFaceData(s);
                    }
                }, throwable -> ToastUtil.showFailure(mContext, "获取店铺信息失败"));
        }
    }

    /**
     * 创建授信
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
                            ToastUtil.showFailure(mContext, s.getError());
                            break;
//                    case 0:
//                        ApplyInfoResponse apply = new ApplyInfoResponse();
//                        apply.creditId = s.objectId;
//                        mTasksRepository.saveApplyInfo(apply);
//                        break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                }
            });
    }

}
