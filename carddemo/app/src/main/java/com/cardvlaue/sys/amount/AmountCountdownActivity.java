package com.cardvlaue.sys.amount;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.ApplyInfoResponse;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserCreditResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * <p>额度倒计时<p/>
 */
public class AmountCountdownActivity extends BaseActivity {

    public static final String BUS_AMOUNT_CODE = "BUS_AMOUNT_CODE";
    private Toolbar mToolbarView;
    private TextView mTitleTextView;
    private long mHour = 00;
    private long mMin = 02;
    private long mSecond = 00;// 天 ,小时,分钟,秒
    private boolean isRun = true;
    private boolean issuccess = true;
    private Handler timeHandler;
    // 倒计时
    private TextView minutesTv, secondsTv;
    private String objectId,token, phone, creditId;//creditId, appId
    private TasksRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_countdown);

        initView();
        startRun();
    }

    public void initView() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mTitleTextView.setText(getString(R.string.amount_calculate));
        minutesTv = (TextView) findViewById(R.id.minutes_tv);
        secondsTv = (TextView) findViewById(R.id.seconds_tv);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();





        LoginResponse loginResponse = repository.getLogin();
        // objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        objectId=userInfoNewResponse.objectId;

        Timber.e("首次创建授信返回的授信id" + repository.getCredCreditId() + "objectId:" + objectId+"=userInfoNewResponse==="+userInfoNewResponse.objectId);
        Timber.e("获取多申请里面返回的" + repository.getCreditId());

        if (!TextUtils.isEmpty(repository.getCreditId())) {//获取多申请里面返回的
            creditId = repository.getCreditId();
            objectId = repository.getMerchantId();
        } else {
            getUserInfo();
        }


        Timber.e("获取多申请里面返回的之后的objectId：" + repository.getCreditId() + "objectId:" + objectId);
        timeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    computeTime();
                    if (mMin < 10) {
                        minutesTv.setText("0" + mMin + ":");
                    } else {
                        minutesTv.setText(mMin + ":");
                    }
                    if (mSecond < 10) {
                        secondsTv.setText("0" + mSecond);
                    } else {
                        secondsTv.setText(mSecond + "");
                    }
                    if (mHour == 0 && mMin == 0 && mSecond == 0) {
                        timeHandler.removeMessages(0x1005);
                        if (issuccess) {
                            // startActivity(new Intent(AmountCountdownActivity.this, CountAmountActivity.class));
                            ToastUtil.showFailure(AmountCountdownActivity.this, "额度未生成或发生未知错误");
                            issuccess = false;
                        }

                        AmountCountdownActivity.this.finish();
                    }
                } else if (msg.what == 0x1005) {//调用授信的接口
                    Timber.e("调用授信的接口msg.what == 0x1005");
                    if (!TextUtils.isEmpty(creditId)) {//creditId
                        Timber.e("objectId::调用授信的接口msg.what == 0x1006" + repository.getMerchantId() + "==666==" + objectId);
                        queryCredit(creditId);//objectId,
                    }
                }
            }
        };
        Timber.e("creditId授信id" + creditId);
        if (!TextUtils.isEmpty(creditId)) {
            timeHandler.sendEmptyMessageDelayed(0x1005, 3000);
        }
    }

    /**
     * 开启倒计时
     */
    private void startRun() {
        new Thread(() -> {
            while (isRun) {
                try {
                    Thread.sleep(1000); // sleep 1000ms
                    Message message = Message.obtain();
                    message.what = 1;
                    timeHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 倒计时计算
     */
    private void computeTime() {
        mSecond--;
        if (mSecond < 0) {
            mMin--;
            mSecond = 59;
            if (mMin < 0) {
                mMin = 59;
                mHour--;
                if (mHour < 0) {
                    // 倒计时结束
                    mHour = 23;
                }
            }
        }
    }

    /**
     * 获取授信
     */
    public void queryCredit(String creditId) {//String objectId,
        timeHandler.sendEmptyMessageDelayed(0x1005, 3000);
        Timber.e("授信参数111" + creditId + "====" + objectId + "=====" + token + "====");

        repository.getCreditInfo(objectId, token, creditId,
            new TasksDataSource.LoadResponseNewCallback<UserCreditResponse, String>() {
                @Override
                public void onResponseSuccess(UserCreditResponse s) {
                    Timber.e("授信返回的结果：" + JSON.toJSONString(s));
                    double creditLine = s.totalCreditLine;
                    if ("N".equals(s.getCreditStatus()) || "E".equals(s.getCreditStatus())
                        || ("PM".equals(s.getCreditStatus()) && String.valueOf(s) != null && !String
                        .valueOf(creditLine).equals("0"))) {
                        timeHandler.removeMessages(0x1005);
                        //获取授信成功，用过要把授信的数据保存到数据库中
                        //repository.saveCreditInfo(userCreditResponse);
                        if (issuccess) {

                            //startActivity(new Intent(AmountCountdownActivity.this, CountAmountActivity.class));

                            BusIndustrySelect select = new BusIndustrySelect(
                                AmountCountdownActivity.BUS_AMOUNT_CODE);
                            select.setTypeId("AmountCountdown");
                            RxBus.getDefaultBus().send(select);
                            issuccess = false;
                            AmountCountdownActivity.this.finish();
                        }
                    } else if (s.getCode() == 271) {
                        ToastUtil.showFailure(AmountCountdownActivity.this, s.getError());
                        timeHandler.removeMessages(0x1005);
                        AmountCountdownActivity.this.finish();
                    }/*else{
                        ToastUtil.showFail(AmountCountdownActivity.this,userCreditResponse.getError());
                    }*/
                }

                @Override
                public void onResponseFailure(String f) {
                    Timber.e("授信计算额度的时候：" + f + "====");
                    Toast.makeText(AmountCountdownActivity.this, "授信查询异常，请稍后再试", Toast.LENGTH_LONG)
                        .show();
                }
            });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeMessages(0x1005);
    }

    //获取用户
    public void getUserInfo() {
        repository.getUserInfo(objectId, token)
            .compose(bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                switch (s.responseSuccess(AmountCountdownActivity.this)) {
                    case -1:
                        ToastUtil.showFailure(AmountCountdownActivity.this, s.getError());
                        break;
                    case 0:
                        Timber.e("获取用户信息" + JSON.toJSONString(s));
                        obtainApplyInfo(s.objectId, token, s.applicationId);
                        break;
                }
            }, Throwable::printStackTrace);
    }

    private void obtainApplyInfo(@NonNull final String objectId, @NonNull final String accessToken,
        @NonNull String applicationId) {
        Timber.e("obtainApplyInfo:%s||%s", objectId, applicationId);
        repository.getApplyInfo(objectId, accessToken, applicationId,
            new TasksDataSource.LoadResponseNewCallback<ApplyInfoResponse, String>() {
                @Override
                public void onResponseSuccess(ApplyInfoResponse response) {
                    Timber.e("获取申请:" + JSON.toJSONString(response));
                    creditId = response.creditId;
                    timeHandler.sendEmptyMessageDelayed(0x1005, 3000);
                }

                @Override
                public void onResponseFailure(String f) {
                }
            });
    }

}