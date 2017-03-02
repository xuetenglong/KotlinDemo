package com.cardvlaue.sys.userdatails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;
import timber.log.Timber;

final class UserDetailsPresenter implements UserDetailsContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private UserDetailsContract.View mDetailsView;

    @NonNull
    private Context mContext;

    private CompositeDisposable mDisposables;

    private boolean isIng;

    @Inject
    UserDetailsPresenter(@NonNull Context context, @NonNull TasksRepository tasksRepository,
        @NonNull UserDetailsContract.View detailsView) {
        mContext = context;
        mTasksRepository = tasksRepository;
        mDetailsView = detailsView;
    }

    @Override
    public void userLogout() {
        if (isIng) {
            return;
        }

        isIng = true;
        mDetailsView.showLoadingDialog();

        mTasksRepository.clearCache();

        LoginResponse login = mTasksRepository.getLogin();
        mDisposables.add(mTasksRepository.userLogout(login.objectId, login.accessToken)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginResponse -> {
                Timber.e("userLogout:%s", JSON.toJSONString(loginResponse));
                isIng = false;
                mDetailsView.dismissLoadingDialog();
                mDetailsView.clickOut();
            }, throwable -> {
                Timber.e("userLogoutEEE:%s", throwable.getMessage());
                isIng = false;
                mDetailsView.dismissLoadingDialog();
                mDetailsView.clickOut();
            }));
    }

    @Inject
    void setupListeners() {
        mDisposables = new CompositeDisposable();
        mDetailsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        UserInfoNewResponse user = mTasksRepository.getUserInfo();
        String phoneStr = user.mobilePhone;
        if (TextUtils.isEmpty(phoneStr)) {
            phoneStr = mTasksRepository.getMobilePhone();
        }

        mDetailsView.initData(user.ownerName, phoneStr, user.ownerSSN);
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}
