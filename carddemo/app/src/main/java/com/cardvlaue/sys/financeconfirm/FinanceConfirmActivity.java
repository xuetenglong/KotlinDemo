package com.cardvlaue.sys.financeconfirm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.applyinfo.ApplyInfoAdapter;
import com.cardvlaue.sys.applyinfo.ApplyRest;
import com.cardvlaue.sys.applyinfo.Confirmlist;
import com.cardvlaue.sys.cardverify.CardPrivateActivity;
import com.cardvlaue.sys.cardverify.CardPublicActivity;
import com.cardvlaue.sys.cardverify.UpdateApplyBO;
import com.cardvlaue.sys.data.ErrorResponse;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.face.FaceActivity;
import com.cardvlaue.sys.util.ToastUtil;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>融资方案确认  type   2<p/>
 */
public class FinanceConfirmActivity extends BaseActivity {

    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private RecyclerView mRecyclerView;
    private Button mBtnSubmit;//确定按钮
    private ApplyInfoAdapter mApplyInfoAdapter;
    private ApplyRest applyRest;
    private String phone;
    private String applicationId;
    private String objectId;
    private String token;
    private TasksRepository repository;
    private IFinancingRest rest;//更新申请
    private ContentLoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_confirm);
        initView();
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        applyRest = HttpConfig.getClient().create(ApplyRest.class);

        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        token = loginResponse.accessToken;

        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        objectId = userInfoNewResponse.objectId;
        applicationId = userInfoNewResponse.applicationId;

        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            applicationId = repository.getApplicationId();
        }
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "2");
        applyRest.queryConfirmList(token, objectId, applicationId, jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                ErrorResponse error = null;
                try {
                    error = JSON.parseObject(s, ErrorResponse.class);
                    switch (error.responseSuccess(FinanceConfirmActivity.this)) {
                        case -1:
                            Toast.makeText(FinanceConfirmActivity.this, error.getError(),
                                Toast.LENGTH_LONG)
                                .show();
                            break;
                    }
                } catch (Exception e) {
                    List<Confirmlist> confirmlist = JSON.parseArray(s, Confirmlist.class);
                    mApplyInfoAdapter.update(confirmlist.get(0).getItems());
                }
            }, throwable -> Timber.e("CALL:" + throwable.getMessage()));
    }

    public void initView() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.financing_confirm));
        mBackView.setOnClickListener(v -> finish());
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        //创建一个线性布局管理器,然后设置布局的方向
        LinearLayoutManager layoutManager = new LinearLayoutManager(FinanceConfirmActivity.this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        mApplyInfoAdapter = new ApplyInfoAdapter(FinanceConfirmActivity.this, "2");
        //  mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mApplyInfoAdapter);
        mBtnSubmit.setOnClickListener(view ->
            UpdateUserInfo());

    }

    /**
     * 更新申请
     */
    public void UpdateUserInfo() {
        mLoadingDialog = ContentLoadingDialog.newInstance("加载中...");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show(getSupportFragmentManager(), "tag");
        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
        updateApplyBO.setIsWithdrawConfirm("1");//isKalaRecognize

        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        String objectId = userInfoNewResponse.objectId;
        String applicationId = userInfoNewResponse.applicationId;

        LoginResponse loginResponse = repository.getLogin();
        String token = loginResponse.accessToken;

        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            applicationId = repository.getApplicationId();
        }

        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        rest.updateApply(objectId, token, applicationId, updateApplyBO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                    mLoadingDialog.dismiss();
                    Timber.e(loginBO.getUpdatedAt() + "==更新申请IsWithdrawConfirm====" + JSON
                        .toJSONString(loginBO));
                    if (!TextUtils.isEmpty(loginBO.getError())) {
                        ToastUtil.showFailure(FinanceConfirmActivity.this, loginBO.getError());
                    } else {
                        queryApply();
                       /* startActivity(new Intent(FinanceConfirmActivity.this, FaceActivity.class));
                        FinanceConfirmActivity.this.finish();*/
                    }
                }, throwable -> {
                    mLoadingDialog.dismiss();
                    Timber.e(throwable.getMessage() + "==throwable==" + JSON.toJSONString(throwable));
                }
            );
    }

    /**
     * 获取最新的申请
     */
    public void queryApply() {
        rest.queryApply(applicationId, objectId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                Timber.e("获取最新的申请银行卡对私e" + JSON.toJSONString(queryApplyBO));
                if (!TextUtils.isEmpty(queryApplyBO.getError())) {
                    ToastUtil.showFailure(FinanceConfirmActivity.this, queryApplyBO.getError());
                } else {
                    if (queryApplyBO.isWithdrawConfirm.toString().trim().equals("1")) {
                        if (queryApplyBO.secondaryBankAccountType.equals("对私")) {
                            //对私
                            Intent intentCardPrivate = new Intent(FinanceConfirmActivity.this,
                                CardPrivateActivity.class);
                            startActivity(intentCardPrivate);
                            FinanceConfirmActivity.this.finish();
                        } else {
                            //对公
                            Intent intentCardPublic = new Intent(FinanceConfirmActivity.this,
                                CardPublicActivity.class);
                            startActivity(intentCardPublic);
                            FinanceConfirmActivity.this.finish();
                        }
                    }
                }
            }, throwable -> Timber.e("申请查询异常，请稍后再试银行卡对私e" + JSON.toJSONString(throwable)));
    }

}
