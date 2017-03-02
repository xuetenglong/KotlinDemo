package com.cardvlaue.sys.posmanagement;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.ApplyInfoResponse;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.MidsItemResponse;
import com.cardvlaue.sys.data.MidsResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

final class PosManagementPresenter implements PosManagementContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private PosManagementContract.View mPosView;

    @NonNull
    private Context mContext;

    private String VerifyId, Question;

    private ArrayList<String> Answers;

    private Handler timeHandler;
    private boolean isQuestVerifying;
    private boolean isQuestLoad;
    private MidsResponse midsResponse;
    private boolean isQuerying;

    @Inject
    PosManagementPresenter(@NonNull Context context, @NonNull TasksRepository tasksRepository,
        @NonNull PosManagementContract.View posView) {
        mContext = context;
        mTasksRepository = tasksRepository;
        mPosView = posView;
        timeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 4) {
                    Timber.e("============PosManagementPresenter===============1024" + VerifyId
                        + "==="
                        + Question + "====" + Answers);
                    if (!VerifyId.equals("0")) {
                        mPosView.showVerifyDialog(VerifyId, Question, Answers);
                    } else {
                        mPosView.dismissLoadingDialog();
                    }
                }
            }
        };
    }

    @Inject
    void setupListeners() {
        mPosView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void verifyMids(String answer) {
        if (isQuestVerifying) {
            ToastUtil.showFailure(mContext, "问题验证中");
            return;
        }
        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        isQuestVerifying = true;
        mPosView.showLoadingDialog("验证中...");

        JSONObject body = new JSONObject();
        body.put("selectedAmt", answer);

        LoginResponse login = mTasksRepository.getLogin();
        ApplyInfoResponse applyInfo = mTasksRepository.getApplyInfo();
        Timber.e("verifyMid:%s||%s", answer, applyInfo.creditId);

        mTasksRepository.verifyMids(login.objectId, login.accessToken, applyInfo.creditId,
            midsResponse.getVerifyId(),
            body, new TasksDataSource.LoadResponseNewCallback<MidsResponse, String>() {
                @Override
                public void onResponseSuccess(MidsResponse s) {
                    isQuestVerifying = false;
                    mPosView.dismissLoadingDialog();
                    switch (s.responseSuccess(mContext)) {
                        case -1:
                            ToastUtil.showFailure(mContext, s.getError());
                            break;
                        case 0:
                            if ("0000".equals(s.getResCode())) {
                                if (s.isVerifyResult()) {
                                    loadPosLists();
                                    ToastUtil.showSuccess(mContext, "验证成功");
                                } else {
                                    int verifyTime = s.getLeftVerifyTimes();
                                    if (verifyTime > 0) {
                                        ToastUtil
                                            .showFailure(mContext,
                                                "验证失败！连续2次错误将暂停受理您的申请，您还剩" + verifyTime + "次机会!");
                                        VerifyId = midsResponse.getVerifyId();
                                        Question = midsResponse.getQuestion();
                                        Answers = (ArrayList<String>) midsResponse.getAnswers();
                                        timeHandler.sendEmptyMessageDelayed(4, 3000);
                                        //mPosView.showVerifyDialog(midsResponse.getVerifyId(), midsResponse.getQuestion(), (ArrayList<String>) midsResponse.getAnswers());
                                    } else {
                                        loadPosLists();
                                        ToastUtil.showSuccess(mContext, "验证失败");
                                    }
                                }
                            } else {
                                Timber.e("getResMsg:%s", s.getResMsg());
                                ToastUtil.showFailure(mContext, s.getResMsg());
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isQuestVerifying = false;
                    mPosView.dismissLoadingDialog();
                    ToastUtil.showFailure(mContext, f);
                }
            });
    }

    @Override
    public void questMids(String verifyId) {
        if (isQuestLoad) {
            ToastUtil.showFailure(mContext, "验证问题获取中");
            return;
        }

        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        isQuestLoad = true;
        mPosView.showLoadingDialog("获取中...");

        LoginResponse login = mTasksRepository.getLogin();
        ApplyInfoResponse applyInfo = mTasksRepository.getApplyInfo();
        mTasksRepository
            .questionMids(login.objectId, login.accessToken, applyInfo.creditId, verifyId,
                new TasksDataSource.LoadResponseNewCallback<MidsResponse, String>() {
                    @Override
                    public void onResponseSuccess(MidsResponse s) {
                        isQuestLoad = false;
                        mPosView.dismissLoadingDialog();
                        switch (s.responseSuccess(mContext)) {
                            case -1:
                                ToastUtil.showFailure(mContext, s.getError());
                                break;
                            case 0:
                                midsResponse = s;
                                switch (s.getResCode()) {
                                    case "0000":
                                        midsResponse = s;
                                        if (!midsResponse.getVerifyId().equals("0")) {
                                            mPosView
                                                .showVerifyDialog(midsResponse.getVerifyId(),
                                                    midsResponse.getQuestion(),
                                                    (ArrayList<String>) midsResponse.getAnswers());
                                        } else {
                                            mPosView.dismissLoadingDialog();
                                        }

                                        break;
                                    default:
                                        ToastUtil.showFailure(mContext, s.getResMsg());
                                        if (!s.getVerifyId().equals("0")) {
                                            mPosView
                                                .showVerifyDialog(midsResponse.getVerifyId(),
                                                    midsResponse.getQuestion(),
                                                    (ArrayList<String>) midsResponse.getAnswers());
                                        } else {
                                            ToastUtil.showFailure(mContext, s.getResMsg());
                                        }
                                        break;
                                }
                                break;
                        }
                    }

                    @Override
                    public void onResponseFailure(String f) {
                        isQuestLoad = false;
                        mPosView.dismissLoadingDialog();
                        ToastUtil.showFailure(mContext, f);
                    }
                });
    }

    @Override
    public void loadPosLists() {
        if (isQuerying) {
            return;
        }

        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        isQuerying = true;
        // Q P
        LoginResponse login = mTasksRepository.getLogin();
        ApplyInfoResponse applyInfo = mTasksRepository.getApplyInfo();

        Timber.e("查询商编");
        mTasksRepository.queryMids(login.objectId, login.accessToken, applyInfo.creditId,
            new TasksDataSource.LoadResponseNewCallback<MidsResponse, String>() {
                @Override
                public void onResponseSuccess(MidsResponse s) {
                    isQuerying = false;
                    mPosView.dismissLoadingDialog();
                    switch (s.responseSuccess(mContext)) {
                        case -1:
                            ToastUtil.showFailure(mContext, s.getError());
                            break;
                        case 0:
                            List<MidsItemResponse> queryData = s.getResults();
                            if (queryData != null && !queryData.isEmpty()) {
                                mPosView.updateLists(queryData);

                                for (MidsItemResponse data : queryData) {
                                    if ("Q".equalsIgnoreCase(data.getStatus()) || "P"
                                        .equalsIgnoreCase(data.getStatus())) {
                                        loadPosLists();
                                    }
                                }
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isQuerying = false;
                    mPosView.dismissLoadingDialog();
                    ToastUtil.showFailure(mContext, f);
                }
            });
    }

}
