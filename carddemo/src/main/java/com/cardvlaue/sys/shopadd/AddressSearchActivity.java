package com.cardvlaue.sys.shopadd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.AddressSearchItemResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class AddressSearchActivity extends BaseActivity {

    public static final String BUS_ADDRESS_SEARCH = "AddressSearchActivity_BUS_ADDRESS_SEARCH";
    /**
     * 列表
     */
    @BindView(R.id.rv_search_lists)
    RecyclerView mListsView;
    /**
     * 搜索框
     */
    @BindView(R.id.et_search_input)
    EditText mSearchView;
    private TasksDataSource mTasksRepository;
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private AddressSearchAdapter mSearchAdapter;
    private List<AddressSearchItemResponse> mFirstData = new ArrayList<>(), mUseData = new ArrayList<>();
    private boolean firstOpen = true;
    private Toast mToast;
    private boolean isQuerying;

    /**
     * 点击搜索
     */
    @OnClick(R.id.tv_address_search_button)
    void clickSearch() {
        String searchStr = mSearchView.getText().toString();
        if (TextUtils.isEmpty(searchStr)) {
            return;
        }
        queryAddress(searchStr);
    }

    @OnTextChanged(R.id.et_search_input)
    void changeInputText(CharSequence charSequence) {
        queryAddress(charSequence.toString());
    }

    @OnClick(R.id.iv_search_back)
    void clickBack() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTasksRepository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        setContentView(R.layout.activity_address_search);
        ButterKnife.bind(this);

        mSearchAdapter = new AddressSearchAdapter();
        mSearchAdapter.setOnItemClickLister(position -> {
            if (position >= mUseData.size()) {
                position = mUseData.size() - 1;
            }
            BusAddressSearch searchBus = new BusAddressSearch(BUS_ADDRESS_SEARCH);
            searchBus.lngAndlat = mUseData.get(position).lngAndlat;
            searchBus.city = mUseData.get(position).city;
            searchBus.district = mUseData.get(position).district;
            searchBus.name = mUseData.get(position).name;
            RxBus2.Companion.get().send(searchBus);
            finish();
        });

        mListsView.setLayoutManager(new LinearLayoutManager(this));
        mListsView.setAdapter(mSearchAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryAddress(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mToast != null) {
            mToast.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposables.clear();
    }

    private void queryAddress(String addressStr) {
        if (isQuerying) {
            return;
        }

        if (TextUtils.isEmpty(addressStr)) {
            if (!mFirstData.isEmpty()) {
                update(mFirstData);
                return;
            } else {
                addressStr = "上海";
            }
        }

        isQuerying = true;
        mDisposables.add(
            mTasksRepository.queryShopAddress(addressStr).observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Timber.e("queryShopAddress:%s", JSON.toJSONString(s));
                    isQuerying = false;
                    if (s.requestSuccess()) {
                        if (firstOpen) {
                            firstOpen = false;
                            mFirstData = s.resultData;
                        }
                        mUseData = s.resultData;
                        update(mUseData);
                    } else if (s.requestError()) {
                        showFailure(s.error);
                    } else {
                        showFailure("地址查询异常");
                    }
                }, throwable -> {
                    isQuerying = false;
                    showFailure("地址获取失败");
                }));
    }

    private void showFailure(final String msg) {
        runOnUiThread(() -> mToast = ToastUtil.showFailure(this, msg, true));
    }

    /**
     * 更新列表数据
     */
    private void update(List<AddressSearchItemResponse> data) {
        runOnUiThread(() -> mSearchAdapter.updateData(data));
    }

}
