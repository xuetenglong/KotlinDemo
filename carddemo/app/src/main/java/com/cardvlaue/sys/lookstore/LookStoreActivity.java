package com.cardvlaue.sys.lookstore;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.ShopListsBean;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.shopadd.ShopAddActivity;
import com.cardvlaue.sys.shopadd.ShopAddFragment;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LookStoreActivity extends BaseActivity {

    RecyclerView mListsView;
    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private LookStoreSelectListsAdapter mAdapter;

    private List<ShopListsBean> mData;

    private ContentLoadingDialog mLoadingDialog;

    private IFinancingRest rest;

    private TasksRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_store);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mToolbarView.setBackgroundResource(R.color.app_blue);
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_back, 0, 0, 0);
        mBackView.setOnClickListener(v -> finish());
        mTitleTextView.setTextColor(Color.parseColor("#ffffff"));
        mTitleTextView.setText("查看店铺");
        mListsView = (RecyclerView) findViewById(R.id.rv_shop_select_lists);
        mAdapter = new LookStoreSelectListsAdapter();
        // 更新店铺
        mAdapter
            .setOnEditClickListener(
                position -> startActivity(new Intent(this, ShopAddActivity.class)
                    .putExtra(ShopAddFragment.ARGUMENT_TYPE, 1)
                    .putExtra(ShopAddFragment.ARGUMENT_SHOP_ID, mData.get(position).merchantId)));
        // 切换店铺
        //mAdapter.setOnItemClickListener(position -> mPresenter.setCurrentShop(mData.get(position).merchantId, mData.get(position).corporateName));
        LinearLayoutManager layoutManager = new LinearLayoutManager(LookStoreActivity.this);
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        mListsView.setLayoutManager(layoutManager);
        mListsView.setAdapter(mAdapter);

        LoginResponse loginResponse = repository.getLogin();
        String idStr = loginResponse.objectId;
        if (!TextUtils.isEmpty(idStr)) {
            mLoadingDialog = ContentLoadingDialog.newInstance("加载中...");
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show(getSupportFragmentManager(), "tag");
            rest.queryShopLists(idStr, loginResponse.accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shopListsBeen -> {
                    mLoadingDialog.dismissAllowingStateLoss();
                    Timber.e("queryShopLists:%s", JSON.toJSONString(shopListsBeen));
                    mData = shopListsBeen;
                    mAdapter.updateData(shopListsBeen);
                    Timber.e("===look==" + mData.size());
                    if (mData.size() > 0) {
                        findViewById(R.id.ll_no).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.ll_no).setVisibility(View.VISIBLE);
                    }
                }, throwable -> {
                    mLoadingDialog.dismissAllowingStateLoss();
                    Timber.e("queryShopListsEEE:%s", throwable.getMessage());
                });
        } else {
            findViewById(R.id.ll_no).setVisibility(View.VISIBLE);
        }

        // 更新店铺
        mAdapter.setOnEditClickListener(
            position -> startActivity(new Intent(LookStoreActivity.this, ShopAddActivity.class)
                .putExtra(ShopAddFragment.ARGUMENT_TYPE, 1)
                .putExtra(ShopAddFragment.STORE_TYPE, 2)
                .putExtra(ShopAddFragment.ARGUMENT_SHOP_ID, mData.get(position).merchantId)));

    /*    mAdapter.setOnEditClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(LookStoreActivity.this, AppSubmitActivity.class);
                startActivity(intent);
            }
        });*/

    }
}
