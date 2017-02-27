package com.cardvlaue.sys.baolinotice;

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
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.applyinfo.ApplyInfoAdapter;
import com.cardvlaue.sys.applyinfo.ApplyRest;
import com.cardvlaue.sys.applyinfo.Confirmlist;
import com.cardvlaue.sys.bill.BillActivity;
import com.cardvlaue.sys.data.ErrorResponse;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>保理通知书  type 3<p/>
 */
public class BaoliNoticeActivity extends BaseActivity {

    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private RecyclerView mRecyclerView;
    private Button mBtnSubmit;//确定按钮
    private Button mBtnBack;//返回修改按钮
    private LinearLayout mLyBottom;//控制是否显示
    private ApplyInfoAdapter mApplyInfoAdapter;
    private ApplyRest applyRest;
    private String phone;
    private String applicationId;
    private String objectId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baoli_notice);
        initView();
        applyRest = HttpConfig.getClient().create(ApplyRest.class);
        TasksRepository repository = ((CVApplication) getApplication())
            .getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        applicationId = repository.getUserInfo().applicationId;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "3");
        Timber.e("merid" + repository.getApplicationId());
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            applicationId = repository.getApplicationId();
        }

        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        applyRest.queryConfirmList(token, objectId, applicationId, jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                Timber.e(s + "保理通知书");
                ErrorResponse error = null;
                try {
                    error = JSON.parseObject(s, ErrorResponse.class);
                    Timber.e("保理通知书:" + error.responseSuccess(this));
                    switch (error.responseSuccess(BaoliNoticeActivity.this)) {
                        case -1:
                            Toast.makeText(BaoliNoticeActivity.this, error.getError(),
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
        mTitleTextView.setText(getString(R.string.notice_factoring));
        mBackView.setOnClickListener(view -> finish());
        mBackView.setOnClickListener(v -> finish());
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mLyBottom = (LinearLayout) findViewById(R.id.ly_bottom);
        //创建一个线性布局管理器,然后设置布局的方向
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaoliNoticeActivity.this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        mApplyInfoAdapter = new ApplyInfoAdapter(BaoliNoticeActivity.this, "3");
        //  mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mApplyInfoAdapter);
        mBtnSubmit.setOnClickListener(view -> {
            startActivity(new Intent(BaoliNoticeActivity.this, BillActivity.class));
        });
        mBtnSubmit.setOnClickListener(
            v -> startActivity(new Intent(BaoliNoticeActivity.this, BillActivity.class)));
    }

}
