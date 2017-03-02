package com.cardvlaue.sys.home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.HomeImageDO;
import com.cardvlaue.sys.data.HomeImageItemDO;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.MessageResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.financeintention.FinanceIntentionActivity;
import com.cardvlaue.sys.financeintention.FinanceIntentionFragment;
import com.cardvlaue.sys.login.LoginActivity;
import com.cardvlaue.sys.message.MessageActivity;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

final class HomePresenter implements HomeContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private HomeContract.View mHomeView;

    @NonNull
    private Context mContext;

    private CompositeDisposable mDisposables;

    /**
     * 两系统一用户信息
     */
    private String oneSysMsg, twoSysMsg, userMsg;

    /**
     * 轮播图
     */
    private List<HomeImageItemDO> mImageData;
    private boolean isSysLoading;
    private boolean isUserLoading;
    private boolean isLoading;

    @Inject
    HomePresenter(@NonNull Context context, @NonNull TasksRepository tasksRepository,
        @NonNull HomeContract.View homeView) {
        mContext = context;
        mTasksRepository = tasksRepository;
        mHomeView = homeView;
    }

    @Inject
    void setupListeners() {
        mDisposables = new CompositeDisposable();
        mHomeView.setPresenter(this);
    }

    @Override
    public void loadHomeData() {
        queryHomeImages();
        queryMessage();
    }

    /**
     * 查询公告
     */
    private void queryMessage() {
        LoginResponse login = mTasksRepository.getLogin();
        String idStr = login.objectId;
        String tokenStr = login.accessToken;
        if (!TextUtils.isEmpty(idStr) && !TextUtils.isEmpty(tokenStr)) {
            getSystemMessage(idStr, tokenStr);
            getUserMessage(idStr, tokenStr);
        }
    }

    /**
     * 系统消息
     */
    private void getSystemMessage(String id, String token) {
        if (isSysLoading) {
            return;
        }

        isSysLoading = true;
        JSONObject body = new JSONObject();
        body.put("type", "1");
        mDisposables.add(mTasksRepository.queryMessage(id, token, body)
            .observeOn(AndroidSchedulers.mainThread())
            .filter(messageResponses -> {
                Timber.e("getSystemMessage:%s", JSON.toJSONString(messageResponses));
                isSysLoading = false;
                return !messageResponses.isEmpty();
            })
            .subscribe(messageResponses -> {
                oneSysMsg = messageResponses.get(0).title;
                twoSysMsg = messageResponses.get(messageResponses.size() - 1).title;
                mHomeView.showSysMsg(oneSysMsg, twoSysMsg);
            }, throwable -> {
                Timber.e("getSystemMessageERR:%s", throwable.getMessage());
                isSysLoading = false;
            }));
    }

    /**
     * 用户消息
     */
    private void getUserMessage(String id, String token) {
        if (isUserLoading) {
            return;
        }

        isUserLoading = true;
        JSONObject body = new JSONObject();
        body.put("type", "0");
        mDisposables.add(mTasksRepository.queryMessage(id, token, body)
            .observeOn(AndroidSchedulers.mainThread())
            .filter(messageResponses -> {
                Timber.e("getUserMessage:%s", JSON.toJSONString(messageResponses));
                isUserLoading = false;
                return !messageResponses.isEmpty();
            })
            .subscribe(messageResponses -> {
                for (MessageResponse data : messageResponses) {
                    if (TextUtils.isEmpty(data.readTime)) {
                        userMsg = data.title;
                        mHomeView.showUserMsg(userMsg);
                        break;
                    }
                }
            }, throwable -> {
                Timber.e("getUserMessageERR:%s", throwable.getMessage());
                isUserLoading = false;
            }));
    }

    /**
     * 加载首页图片
     */
    private void queryHomeImages() {
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络不可用");
            return;
        }
        if (isLoading) {
            return;
        }
        Timber.e("加载首页图片");
        isLoading = true;
        Disposable disposable = mTasksRepository.queryHomeImages()
            .observeOn(AndroidSchedulers.mainThread())
            // 是否请求成功
            .filter(homeImgResponse -> {
                Timber.e("queryHomeImages:%s", JSON.toJSONString(homeImgResponse));
                isLoading = false;
                return homeImgResponse.success();
            })
            // 取出数据
            .map(HomeImageDO::getResultData)
            // 判断是否为空
            .filter(homeImgItemResponses -> !homeImgItemResponses.isEmpty())
            // 显示
            .subscribe(homeImgItemResponses -> {
                mImageData = homeImgItemResponses;
                mHomeView.showHomeImage(mImageData);
                mHomeView.closeRefresh();
                mTasksRepository.saveHomeImageData(JSON.toJSONString(mImageData));
            }, throwable -> {
                Timber.e("queryHomeImagesError:%s", throwable.getMessage());
                isLoading = false;
                mHomeView.closeRefresh();
            });
        mDisposables.add(disposable);
    }

    @Override
    public void clickApply() {
        LoginResponse loginResponse = mTasksRepository.getLogin();
        if (TextUtils.isEmpty(loginResponse.accessToken)) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
        } else {
            mTasksRepository.saveApplicationId(null);
            mTasksRepository.saveSetStep(null);
            //mTasksRepository.saveLogin(null);//objectId
            mTasksRepository.saveMerchantId(null);
            mTasksRepository.saveApplicationId(null);
            mTasksRepository.saveCreditId(null);
            mContext.startActivity(new Intent(mContext, FinanceIntentionActivity.class)
                .putExtra(FinanceIntentionFragment.EXTRA_IS_LOAD_DATA, false));
        }
    }

    @Override
    public void clickTempNotice() {
        LoginResponse loginResponse = mTasksRepository.getLogin();
        if (TextUtils.isEmpty(loginResponse.accessToken)) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
        } else {
            mContext
                .startActivity(new Intent(mContext, MessageActivity.class).putExtra("type", "1"));
        }
    }

    @Override
    public void subscribe() {
        String imageStr = mTasksRepository.getHomeImageData();
        if (mImageData != null) {
            mHomeView.showHomeImage(mImageData);
            Timber.e("内存缓存");
        } else if (!TextUtils.isEmpty(imageStr)) {
            try {
                mImageData = JSON.parseArray(imageStr, HomeImageItemDO.class);
                if (mImageData != null && mImageData.size() > 0) {
                    mHomeView.showHomeImage(mImageData);
                    for (HomeImageItemDO d : mImageData) {
                        Timber.e("磁盘缓存:::%s", JSON.toJSONString(d));
                    }
                }
            } catch (Exception e) {
                Timber.e(e.getMessage());
            }
        } else {
            Timber.e("网络缓存");
            queryHomeImages();
        }

        if (TextUtils.isEmpty(imageStr)) {
            queryHomeImages();
        } else {
            mHomeView.showHomeImage(mImageData);
        }
        if (!TextUtils.isEmpty(oneSysMsg) || !TextUtils.isEmpty(twoSysMsg) || !TextUtils
            .isEmpty(userMsg)) {
            mHomeView.showSysMsg(oneSysMsg, twoSysMsg);
            mHomeView.showUserMsg(userMsg);
        } else {
            queryMessage();
        }
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}
