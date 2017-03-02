package com.cardvlaue.sys.creditreport;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.uploadphoto.TipConstant;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.ToastUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class CreditReportsPresenterImpl implements ICreditReportsPresenter {

    private ICreditReportView creditReportsView;
    private ICreditReportsRest creditReposrtRest;
    private boolean isRefresh;
    private boolean isCommit;  //正在提交验证码
    private String phone;
    private Activity context;
    private String objectId;
    private String token;
    private String applicationId;
    private TasksRepository repository;

    public CreditReportsPresenterImpl(Activity context, ICreditReportView creditReportsView) {
        this.creditReportsView = creditReportsView;
        this.context = context;
        creditReposrtRest = HttpConfig.getClient().create(ICreditReportsRest.class);
        repository = ((CVApplication) context.getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();
    }

    @Override
    public void getVerificationCode() {
        if (!CheckUtil.isOnline(context)) {
            creditReportsView.toast(TipConstant.LOGIN_NOT_NETWORK);
            return;
        }
        // 正在请求
        if (isRefresh) {
            creditReportsView.toast(TipConstant.CODE_IMG_SENDING);
            return;
        }

        isRefresh = true;
        LoginResponse loginResponse = repository.getLogin();
        token = loginResponse.accessToken;

        JSONObject jsBody = new JSONObject();
        jsBody.put("type", "1");

        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        objectId = userInfoNewResponse.objectId;
        applicationId = userInfoNewResponse.applicationId;
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }

        creditReposrtRest.verifyCode(token, objectId, jsBody)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(verifyCodeBO -> {
                isRefresh = false;
                Timber.e(JSON.toJSONString(verifyCodeBO));
                switch (verifyCodeBO.responseSuccess(context)) {
                    case -1:
                        creditReportsView.toast(verifyCodeBO.getError());
                        break;
                    case 0:
                        if (!TextUtils.isEmpty(verifyCodeBO.getImgUrl())) {
                            creditReportsView
                                .imgCodeLoadSuccess(verifyCodeBO.getImgUrl(),
                                    verifyCodeBO.getSessionId());
                        }
                        break;
                }
            }, throwable -> {
                isRefresh = false;
                creditReportsView.toast("图片验证码加载失败");
            });
    }

    @Override
    public void creditReport(JSONObject body) {
        if (!CheckUtil.isOnline(context)) {
            creditReportsView.toast(TipConstant.LOGIN_NOT_NETWORK);
            return;
        }
        if (isCommit) {
            creditReportsView.toast("正在验证中...");
            return;
        }

        isCommit = true;
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            applicationId = repository.getApplicationId();
        }
        creditReposrtRest.creditReport(token, objectId, applicationId, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mobilePhoneVerifyCodeBO -> {
                isCommit = false;
                Timber.e(JSON.toJSONString(mobilePhoneVerifyCodeBO));
                if (mobilePhoneVerifyCodeBO.responseSuccess(context) == 0 && !TextUtils
                    .isEmpty(mobilePhoneVerifyCodeBO.getCreatedAt())) {
                    ToastUtil.showSuccess(context,"验证成功");
                    creditReportsView.creditReportSuccess();
                } else if (!TextUtils.isEmpty(mobilePhoneVerifyCodeBO.getImgUrl())) {
                    creditReportsView.imgCodeLoadSuccess(mobilePhoneVerifyCodeBO.getImgUrl(), null);
                    // creditReportsView.toast(mobilePhoneVerifyCodeBO.getError());
                    ToastUtil.showFailure(context,mobilePhoneVerifyCodeBO.getError());
                    creditReportsView.creditFailure();
                } else if (mobilePhoneVerifyCodeBO.responseSuccess(context) == -1) {
                    // creditReportsView.toast(mobilePhoneVerifyCodeBO.getError());
                    ToastUtil.showFailure(context,mobilePhoneVerifyCodeBO.getError());
                    creditReportsView.creditFailure();
                }
            }, throwable -> {
                isCommit = false;
                creditReportsView.creditFailure();
                Timber.e("creditReportEEE:%s", throwable.getLocalizedMessage());
            });
    }
}