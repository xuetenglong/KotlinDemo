package com.cardvlaue.sys.uploadphoto;

import android.app.Activity;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.apply.UpdateUserInfoBO;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by cardvalue on 2016/6/6.
 */
public class FinancingUploadModelImpl implements IFinancingUploadModel {

    private Activity context;
    private IFinancingUploadPresenter presenter;
    private IFinancingRest rest;
    private String phone;
    private String objectId;
    private String token;

    public FinancingUploadModelImpl(Activity context, IFinancingUploadPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        TasksRepository repository = ((CVApplication) context.getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
        phone = repository.getMobilePhone();
    }

    /**
     * 更新用户信息
     */
    @Override
    public void updateUserInfo(UpdateUserInfoBO user) {
        TasksRepository repository = ((CVApplication) context.getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;
        Timber.e("上传图片objectId:" + objectId);
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
       /* rest.updateUserInfo(objectId, objectId, token, user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginBO->{
                    if (loginBO.responseSuccess(context) == -1) {
                        presenter.toast(loginBO.getError());
                    }
                }, throwable-> presenter.toast("资料上传异常，请稍后再试"));*/
    }


    /**
     * 获取图片
     */
    @Override
    public void newFileLists() {
        TasksRepository repository = ((CVApplication) context.getApplication())
            .getTasksRepositoryComponent().getTasksRepository();


        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        //objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        String objectId = userInfoNewResponse.objectId;

        Timber.e("获取图片objectId:" + objectId + "token" + token+"objectIds===="+objectId);
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        rest.newFileLists(objectId, token, objectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(newFileListsBO -> {
                if (newFileListsBO.responseSuccess(context) == 0) {
                    if (newFileListsBO.getResults() != null) {
                        Timber.e("获取图片成功" + JSON.toJSONString(newFileListsBO.getResults()));
                        presenter.newFileListsSuccess(newFileListsBO.getResults());
                        // presenter.colseDialog();
                    } else {
                        presenter.toast("服务器异常，查询图片清单失败");
                    }
                } else if (newFileListsBO.responseSuccess(context) == -1) {
                    presenter.toast(newFileListsBO.getError());
                }
            }, throwable -> presenter.toast("查询清单异常，请稍后再试"));
    }


    /**
     * 获取补件的图片
     */
    @Override
    public void newChecklists() {
        TasksRepository repository = ((CVApplication) context.getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "Pending");
        Timber.e("补件的：" + objectId);
        rest.newChecklists(objectId, token, objectId, jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(newFileListsBO -> {
                Timber.e("补件的newFileListsBO" + JSON.toJSONString(newFileListsBO));
                if (newFileListsBO.responseSuccess(context) == 0) {
                    if (newFileListsBO.getResults() != null) {
                        presenter.newFileListsSuccess(newFileListsBO.getResults());
                    } else {
                        presenter.toast("服务器异常，查询图片清单失败");
                    }
                } else if (newFileListsBO.responseSuccess(context) == -1) {
                    presenter.toast(newFileListsBO.getError());
                }
            }, throwable -> {
                Timber.e("throwable补件的newFileListsBO" + JSON.toJSONString(throwable) + "===="
                    + throwable
                    .getMessage());
                presenter.toast("查询清单异常，请稍后再试");
            });
    }

    /**
     * 删除图片
     */
    @Override
    public void newDeletelists(String fileId) {
        TasksRepository repository = ((CVApplication) context.getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        //objectId = loginResponse.objectId;
        token = loginResponse.accessToken;
        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        objectId = loginResponse.objectId;
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        Timber.e("补件的：" + objectId);
        rest.newDeletelists(objectId, token, objectId, fileId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(newFileListsBO -> {
                Timber.e("删除图片：" + JSON.toJSONString(newFileListsBO));
                if (newFileListsBO.responseSuccess(context) == 0) {
                    if (newFileListsBO.getResults() != null) {
                        presenter.newFileListsSuccess(newFileListsBO.getResults());
                    } else {
                        presenter.toast("服务器异常，删除图片失败");
                    }
                } else if (newFileListsBO.responseSuccess(context) == -1) {
                    presenter.toast(newFileListsBO.getError());
                }
            }, throwable -> {
                Timber
                    .e("throwable删除图片失败newFileListsBO" + JSON.toJSONString(throwable) + "===="
                        + throwable
                        .getMessage());
                presenter.toast("删除图片失败，请稍后再试");
            });
    }

}

