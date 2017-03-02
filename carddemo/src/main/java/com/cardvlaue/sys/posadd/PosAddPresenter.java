package com.cardvlaue.sys.posadd;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
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
import javax.inject.Inject;
import timber.log.Timber;

final class PosAddPresenter implements PosAddContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private PosAddContract.View mPosView;

    private Context mContext;

    private boolean isCommit;

    private MidsResponse midsResponse;

    private Handler timeHandler;

    private String VerifyId, Question;

    private ArrayList<String> Answers;
    private boolean isQuestVerifying;

    @Inject
    PosAddPresenter(Context context, @NonNull TasksRepository tasksRepository,
        @NonNull PosAddContract.View posView) {
        mContext = context;
        mTasksRepository = tasksRepository;
        mPosView = posView;
        timeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 4) {
                    Timber.e("============pos===============1025" + VerifyId + "===" + Question
                        + "===="
                        + Answers);
                    if (!VerifyId.equals("0")) {
                        mPosView.showVerifyDialog(VerifyId, Question, Answers);
                    } else {
                        mPosView.closeMe();
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
    public void createPos(String mid) {
        if (isCommit) {
            return;
        }

        if (!CheckUtil.isOnline(mContext)) {
            ToastUtil.showFailure(mContext, "网络未连接");
            return;
        }

        if (TextUtils.isEmpty(mid)) {
            ToastUtil.showFailure(mContext, "请输入商编");
            return;
        }

        if (mid.length() < 14 || mid.length() > 15) {
            ToastUtil.showFailure(mContext, "无效的商户编号，请重新填写");
            return;
        }

        isCommit = true;
        mPosView.showPosAddingDialog();
        LoginResponse login = mTasksRepository.getLogin();
        ApplyInfoResponse applyInfo = mTasksRepository.getApplyInfo();
        String idStr = login.objectId;
        String tokenStr = login.accessToken;
        String creditStr = applyInfo.creditId;

        ArrayMap<String, String> body = new ArrayMap<>();
        body.put("mid", mid);

        Timber.e("LoginId:%s,Token:%s,CreditId:%s,Params:%s", idStr, tokenStr, creditStr,
            JSON.toJSONString(body));
        mTasksRepository.createMids(idStr, tokenStr, creditStr, body,
            new TasksDataSource.LoadResponseNewCallback<MidsResponse, String>() {
                @Override
                public void onResponseSuccess(MidsResponse s) {
                    mPosView.dismissPosAddingDialog();
                    switch (s.responseSuccess(mContext)) {
                        case -1:
                            isCommit = false;
                            ToastUtil.showFailure(mContext, s.getError());
                            break;
                        case 0:
                            if ("0000".equals(s.getResCode())) {
                                //正在获取流水
                                mPosView.showPosGetingDialog();
                                loadStatus(idStr, tokenStr, creditStr, mid);
                            } else {
                                isCommit = false;
                                mPosView.dismissPosAddingDialog();
                                ToastUtil.showFailure(mContext, s.getResMsg());
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isCommit = false;
                    mPosView.dismissPosAddingDialog();
                    ToastUtil.showFailure(mContext, f);
                }
            });
    }

    /**
     * 查询商编状态
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param mId 商编
     */
    private void loadStatus(String objectId, String accessToken, String creditId, String mId) {
        Timber.e("查询商编状态");
        mTasksRepository.queryMids(objectId, accessToken, creditId,
            new TasksDataSource.LoadResponseNewCallback<MidsResponse, String>() {
                @Override
                public void onResponseSuccess(MidsResponse s) {
                    switch (s.responseSuccess(mContext)) {
                        case -1:
                            isCommit = false;
                            mPosView.dismissPosGetingDialog();
                            ToastUtil.showFailure(mContext, s.getError());
                            break;
                        case 0:
                            Timber.e("查询商编状态--响应成功" + mId);
                            for (MidsItemResponse dataItem : s.getResults()) {
                                if (mId.equalsIgnoreCase(dataItem.getMid())) {
                                    String status = dataItem.getStatus();
                                    Timber.i("查询状态:::%s|||%s", status, mId);
                                    if ("Q".equalsIgnoreCase(status) || "P"
                                        .equalsIgnoreCase(status)) {
                                        Timber.e("loadStatus 重新查询状态");
                                        loadStatus(objectId, accessToken, creditId, mId);
                                    } else if ("U".equalsIgnoreCase(status)) {
                                        Timber.e("Success 查询状态");
                                        mPosView.dismissPosGetingDialog();
                                        mPosView.showPosQuetionDialogDialog();
                                        questMid(objectId, accessToken, creditId, mId);
                                    } else {
                                        Timber.e("closeMe 重新查询状态");
                                        mPosView.closeMe();
                                    }
                                }
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isCommit = false;
                    mPosView.dismissPosGetingDialog();
                }
            });
    }

    /**
     * 查询验证问题
     *
     * @param objectId merchantId
     * @param accessToken accessToken
     * @param creditId 授信编号
     * @param mId 商编
     */
    private void questMid(String objectId, String accessToken, String creditId, String mId) {
        Timber.e("mId:%s", mId);
        mTasksRepository.questionMids(objectId, accessToken, creditId, mId,
            new TasksDataSource.LoadResponseNewCallback<MidsResponse, String>() {
                @Override
                public void onResponseSuccess(MidsResponse s) {
                    isCommit = false;
                    mPosView.dismissPosQuetionDialogDialog();
                    switch (s.responseSuccess(mContext)) {
                        case -1:
                            ToastUtil.showFailure(mContext, s.getError());
                            break;
                        case 0:
                            midsResponse = s;
                            switch (s.getResCode()) {
                                case "0000":
                                    if (!s.getVerifyId().equals("0")) {
                                        mPosView
                                            .showVerifyDialog(midsResponse.getVerifyId(),
                                                midsResponse.getQuestion(),
                                                (ArrayList<String>) midsResponse.getAnswers());
                                    } else {
                                        mPosView.closeMe();
                                    }
                                    break;
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    isCommit = false;
                    mPosView.dismissPosQuetionDialogDialog();
                    ToastUtil.showFailure(mContext, f);
                }
            });
    }

    @Override
    public void verifyMid(String answer) {
        if (isQuestVerifying) {
            return;
        }

        isQuestVerifying = true;
        mPosView.showVerifyingDialog();

        LoginResponse login = mTasksRepository.getLogin();
        ApplyInfoResponse applyInfo = mTasksRepository.getApplyInfo();
        Timber.e("verifyMid:%s||%s", answer, applyInfo.creditId);

        JSONObject body = new JSONObject();
        body.put("selectedAmt", answer);

        mTasksRepository.verifyMids(login.objectId, login.accessToken, applyInfo.creditId,
            midsResponse.getVerifyId()
            , body, new TasksDataSource.LoadResponseNewCallback<MidsResponse, String>() {
                @Override
                public void onResponseSuccess(MidsResponse s) {
                    isQuestVerifying = false;
                    mPosView.dismissVerifyingDialog();
                    switch (s.responseSuccess(mContext)) {
                        case -1:
                            ToastUtil.showFailure(mContext, s.getError());
                            mPosView.closeMe();
                            break;
                        case 0:
                            if ("0000".equals(s.getResCode())) {
                                if (s.isVerifyResult()) {
                                    mPosView.reAddPos();
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
                                        mPosView.closeMe();
                                    }
                                }
                            } else {
                                String resMsg = s.getResMsg();
                                if (!TextUtils.isEmpty(resMsg)) {
                                    ToastUtil.showFailure(mContext, resMsg);
                                }
                                mPosView.closeMe();
                            }
                            break;
                    }
                }

                @Override
                public void onResponseFailure(String f) {
                    mPosView.dismissVerifyingDialog();
                    ToastUtil.showFailure(mContext, f);
                    mPosView.closeMe();
                }
            });
    }

}
