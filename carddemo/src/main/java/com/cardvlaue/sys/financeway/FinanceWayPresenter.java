package com.cardvlaue.sys.financeway;

import android.content.Context;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.ApplyInfoResponse;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.MidsItemResponse;
import com.cardvlaue.sys.data.MidsResponse;
import com.cardvlaue.sys.data.UserCreditResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.ToastUtil;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

final class FinanceWayPresenter implements FinanceWayContract.Presenter {

    private Context mContext;

    private FinanceWayContract.View mWayView;

    private TasksRepository mTasksRepository;

    @Inject
    FinanceWayPresenter(Context context, TasksRepository tasksRepository,
        FinanceWayContract.View wayView) {
        mContext = context;
        mTasksRepository = tasksRepository;
        mWayView = wayView;
    }

    @Override
    public void setCurrentStep() {
        if (!TextUtils.isEmpty(mTasksRepository.getSetStep())
            && Integer.parseInt(mTasksRepository.getSetStep()) > 30) {
            mWayView.gotoNext();
            mWayView.dismissLoadingDialog();
            return;
        }
        LoginResponse loginR = mTasksRepository.getLogin();
        UserInfoNewResponse userInfoR = mTasksRepository.getUserInfo();

        JSONObject body = new JSONObject();
        body.put("setStep", "30");

        mWayView.showLoadingDialog();
        Timber.e("=============30============"+userInfoR.objectId+"==applicationId=="+userInfoR.applicationId);
        mTasksRepository
            .updateApplyInfo(userInfoR.objectId, loginR.accessToken, userInfoR.applicationId,
                body, new TasksDataSource.LoadResponseNewCallback<LoginResponse, String>() {
                    @Override
                    public void onResponseSuccess(LoginResponse stepS) {
                        mWayView.dismissLoadingDialog();
                        Timber.e("======30====" + JSON.toJSONString(stepS));
                        switch (stepS.responseSuccess(mContext)) {
                            case -1:
                                ToastUtil.showFailure(mContext, stepS.getError());
                                break;
                            case 0:
                                mWayView.gotoNext();
                                break;
                        }
                    }

                    @Override
                    public void onResponseFailure(String f) {
                        mWayView.dismissLoadingDialog();
                        ToastUtil.showFailure(mContext, f);
                    }
                });
    }

    /**
     * 查询商编状态
     */
    private void queryMids() {
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        mWayView.showLoadingDialog();
        LoginResponse login = mTasksRepository.getLogin();
        UserInfoNewResponse userInfo = mTasksRepository.getUserInfo();
        mTasksRepository.getApplyInfo(userInfo.objectId, login.accessToken, userInfo.applicationId,
            new TasksDataSource.LoadResponseNewCallback<ApplyInfoResponse, String>() {
                @Override
                public void onResponseSuccess(ApplyInfoResponse sApplyInfoResponse) {
                    switch (sApplyInfoResponse.responseSuccess(mContext)) {
                        case -1:
                            mWayView.dismissLoadingDialog();
                            ToastUtil.showFailure(mContext, sApplyInfoResponse.getError());
                            break;
                        case 0:
                            String creditStr = sApplyInfoResponse.creditId;
                            String alipayStr = sApplyInfoResponse.alipayCheck;
                            if ("1".equals(alipayStr)) {
                                mWayView.changeAlipayStatus("1");
                            } else {
                                mWayView.changeAlipayStatus("0");
                            }

                            if (TextUtils.isEmpty(creditStr)) {
                                mWayView.changeCreditStatus(false);
                                mWayView.dismissLoadingDialog();
                            } else {
                                mTasksRepository.saveApplyInfo(sApplyInfoResponse);
                                mWayView.changeCreditStatus(true);
                                String idStr = login.objectId;
                                String tokenStr = login.accessToken;
                                Timber.e(idStr + "|||" + tokenStr + "|||" + creditStr + "|||"
                                    + userInfo.applicationId);

                                mTasksRepository.getCreditInfo(idStr, tokenStr, creditStr,
                                    new TasksDataSource.LoadResponseNewCallback<UserCreditResponse, String>() {
                                        @Override
                                        public void onResponseSuccess(
                                            UserCreditResponse sCreditInfo) {
                                            switch (sCreditInfo.responseSuccess(mContext)) {
                                                case -1:
                                                    mWayView.dismissLoadingDialog();
                                                    ToastUtil.showFailure(mContext,
                                                        sCreditInfo.getError());
                                                    break;
                                                case 0:
                                                    double creditLine = sCreditInfo.totalCreditLine;
                                                    if (creditLine > 0) {
                                                        mWayView.changeAmountStatus(true);
                                                    } else {
                                                        mWayView.changeAmountStatus(false);
                                                    }

                                                    mTasksRepository
                                                        .queryMids(login.objectId,
                                                            login.accessToken, creditStr,
                                                            new TasksDataSource.LoadResponseNewCallback<MidsResponse, String>() {
                                                                @Override
                                                                public void onResponseSuccess(
                                                                    MidsResponse sMidsResponse) {
                                                                    mWayView.dismissLoadingDialog();
                                                                    switch (sMidsResponse
                                                                        .responseSuccess(
                                                                            mContext)) {
                                                                        case -1:
                                                                            ToastUtil
                                                                                .showFailure(
                                                                                    mContext,
                                                                                    sMidsResponse
                                                                                        .getError());
                                                                            break;
                                                                        case 0:
                                                                            List<MidsItemResponse> posData = sMidsResponse
                                                                                .getResults();
                                                                            for (MidsItemResponse data : posData) {
                                                                                if ("S"
                                                                                    .equalsIgnoreCase(
                                                                                        data.getStatus())) {
                                                                                    mWayView
                                                                                        .changePosStatus(
                                                                                            true);
                                                                                    break;
                                                                                } else {
                                                                                    mWayView
                                                                                        .changePosStatus(
                                                                                            false);
                                                                                }
                                                                            }
                                                                            mWayView.checkPosAdd(
                                                                                posData.isEmpty());
                                                                            break;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onResponseFailure(
                                                                    String f) {
                                                                    mWayView.dismissLoadingDialog();
                                                                    ToastUtil
                                                                        .showFailure(mContext, f);
                                                                }
                                                            });
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onResponseFailure(String f) {
                                            mWayView.dismissLoadingDialog();
                                            ToastUtil.showFailure(mContext, f);
                                        }
                                    });
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    mWayView.showLoadingDialog();
                    ToastUtil.showFailure(mContext, f);
                }
            });
    }

    @Inject
    void setupListeners() {
        mWayView.setPresenter(this);
    }

    @Override
    public void start() {
        queryMids();
    }

}
