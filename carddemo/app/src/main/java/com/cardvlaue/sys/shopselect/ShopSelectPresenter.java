package com.cardvlaue.sys.shopselect;

import android.content.Context;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.FinanceIntentBus;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.ShopListsBean;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.financeintention.FinanceIntentionFragment;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

class ShopSelectPresenter implements ShopSelectContract.Presenter {

    private final TasksRepository mTasksRepository;

    private ShopSelectContract.View mSelectView;

    private ShopSelectActivity mContext;

    private boolean isLoading;

    private boolean isSelecting;

    @Inject
    ShopSelectPresenter(Context context, TasksRepository tasksRepository,
        ShopSelectContract.View selectView) {
        mContext = (ShopSelectActivity) context;
        mTasksRepository = tasksRepository;
        mSelectView = selectView;
    }

    @Inject
    void setupListeners() {
        mSelectView.setPresenter(this);
    }

    @Override
    public void checkShopChange(String shopId, String shopName) {
        LoginResponse login = mTasksRepository.getLogin();
        if (shopId.equals(login.objectId)) {
            UserInfoNewResponse user = mTasksRepository.getUserInfo();
            String nameStr = user.corporateName;
            if (TextUtils.isEmpty(nameStr)) {
                nameStr = user.businessName;
            }
            FinanceIntentBus intentBus = new FinanceIntentBus(
                FinanceIntentionFragment.BUS_SHOP_DATA,
                nameStr, user.loanPurpose, user.loanAmount, user.planFundTerm);
            Timber.e("当前店铺:%s", JSON.toJSONString(intentBus));
            RxBus.getDefaultBus().send(intentBus);
            mSelectView.closeMe();
        } else {
            Timber.e("切换店铺");
            setCurrentShop(shopId, shopName);
        }
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
        if (!TextUtils.isEmpty(idStr)) {
            isLoading = true;
            mSelectView.showLoadingDialog();
            mTasksRepository.queryShopLists(idStr, loginResponse.accessToken)
                .compose(mContext.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Timber.e("queryShopLists:%s", JSON.toJSONString(s));
                    isLoading = false;
                    mSelectView.dismissLoadingDialog();
                    if (!s.isEmpty()) {
                        List<ShopListsBean> listData = new ArrayList<>();
                        List<ShopListsBean> okData = new ArrayList<>();
                        List<ShopListsBean> lockData = new ArrayList<>();
                        for (ShopListsBean data : s) {
                            if ("1".equals(data.isDocumentLocked)) {
                                lockData.add(data);
                            } else {
                                okData.add(data);
                            }
                        }

                        if (!lockData.isEmpty()) {
                            lockData.get(0).isShowTip = true;
                        }

                        listData.addAll(okData);
                        listData.addAll(okData.size(), lockData);
                        s = listData;
                        Timber.e("SHOP SIZE:%s", s.size());
                    }
                    mSelectView.updateLists(s);
                }, throwable -> {
                    Timber.e("queryShopListsEEE:%s", throwable.getMessage());
                    isLoading = false;
                    mSelectView.dismissLoadingDialog();
                });
        }
    }

    @Override
    public void setCurrentShop(String shopId, String shopName) {
        if (isSelecting) {
            return;
        }

        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        isSelecting = true;
        mSelectView.showLoadingDialog();

        JSONObject bodyPara = new JSONObject();
        bodyPara.put("merchantId", shopId);
        mTasksRepository.setCurrentShop(mTasksRepository.getMobilePhone(), bodyPara)
            .compose(mContext.bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(sCurrentShopBean -> {
                Timber.e("setCurrentShop:%s", JSON.toJSONString(sCurrentShopBean));
                switch (sCurrentShopBean.responseSuccess(mContext)) {
                    case -1:
                        isSelecting = false;
                        mSelectView.dismissLoadingDialog();
                        ToastUtil.showFailure(mContext, sCurrentShopBean.getError());
                        break;
                    case 0:
                        LoginResponse loginR = mTasksRepository.getLogin();
                        mTasksRepository.getUserInfo(shopId, loginR.accessToken)
                            .compose(mContext.bindToLifecycle())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(sUserInfoNewResponse -> {
                                isSelecting = false;
                                mSelectView.dismissLoadingDialog();
                                switch (sUserInfoNewResponse.responseSuccess(mContext)) {
                                    case -1:
                                        ToastUtil.showFailure(mContext,
                                            sUserInfoNewResponse.getError());
                                        break;
                                    case 0:
                                        mTasksRepository.saveLogin(shopId);
                                        mTasksRepository.saveUserInfo(sUserInfoNewResponse);
                                        RxBus.getDefaultBus()
                                            .send(new FinanceIntentBus(
                                                FinanceIntentionFragment.BUS_SHOP_DATA,
                                                sUserInfoNewResponse.corporateName,
                                                sUserInfoNewResponse.loanPurpose,
                                                sUserInfoNewResponse.loanAmount,
                                                sUserInfoNewResponse.planFundTerm));
                                        mSelectView.closeMe();
                                        break;
                                }
                            }, Throwable::printStackTrace);
                        break;
                }
            }, throwable -> {
                Timber.e("setCurrentShopEEE:%s", throwable.getMessage());
                isSelecting = false;
                mSelectView.dismissLoadingDialog();
                ToastUtil.showFailure(mContext, "店铺切换失败");
            });
    }

    @Override
    public void subscribe() {
        loadShopListsData();
    }

    @Override
    public void unsubscribe() {
    }
}
