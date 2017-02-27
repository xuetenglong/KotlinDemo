package com.cardvlaue.sys.bill;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>对账单<p/>
 */
public class BillActivity extends BaseActivity {

    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private RecyclerView mRecyclerView;
    private BillRest billRest;
    private String appId, objectId, token, phone;
    private TasksRepository repository;
    private List<CashListItem> list = new ArrayList<>();
    private BillAdapter mBillAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        initView();
        billRest = HttpConfig.getClient().create(BillRest.class);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        appId = repository.getUserInfo().applicationId;

        queryCashList();
    }


    public void queryCashList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("skip", "0");
        jsonObject.put("limit", "300");
        if (!TextUtils.isEmpty(repository.getApplicationId())) {
            appId = repository.getApplicationId();
        }
        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        billRest.queryCashList(objectId, token, appId, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                Timber.e("对账单返回的结果2" + JSON.toJSONString(s));
                mBillAdapter.update(s.getResults());
            }, throwable -> Timber.e(throwable.getMessage() + "==" + JSON.toJSONString(throwable)));
    }

    public void initView() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.tv_bill));
        mBackView.setOnClickListener(v -> finish());
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_bill);
        //创建一个线性布局管理器,然后设置布局的方向
        LinearLayoutManager layoutManager = new LinearLayoutManager(BillActivity.this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        mBillAdapter = new BillAdapter(BillActivity.this);
        //  mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mBillAdapter);
    }
}
