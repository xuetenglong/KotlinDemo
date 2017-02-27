package com.cardvlaue.sys.shopselect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.ShopListsBean;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.shopadd.ShopAddActivity;
import com.cardvlaue.sys.shopadd.ShopAddFragment;
import com.cardvlaue.sys.util.RxBus2;
import com.trello.rxlifecycle.components.support.RxFragment;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class ShopSelectFragment extends RxFragment implements ShopSelectContract.View {

    @BindView(R.id.tv_white_back)
    TextView mTitleView;
    @BindView(R.id.rv_shop_select_lists)
    RecyclerView mListsView;
    private ShopSelectContract.Presenter mPresenter;
    private ShopSelectListsAdapter mAdapter;

    private List<ShopListsBean> mData = new ArrayList<>();

    private ContentLoadingDialog mLoadingDialog;
    private Disposable mDisposable;

    public static ShopSelectFragment newInstance() {
        return new ShopSelectFragment();
    }

    /**
     * 点击提交
     */
    @OnClick(R.id.btn_shop_select_commit)
    void clickCommit() {
        startActivity(new Intent(getContext(), ShopAddActivity.class)
            .putExtra(ShopAddFragment.ARGUMENT_TYPE, 0));
    }

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        Timber.e("店铺列表：%s", JSON.toJSONString(mData));
        if (!mData.isEmpty()) {
            for (ShopListsBean data : mData) {
                if ("1".equals(data.isCurrent) && "0".equals(data.isDocumentLocked)) {
                    mPresenter.checkShopChange(data.merchantId, data.corporateName);
                    return;
                }
            }
        }

        Timber.e("无可用店铺");
        closeMe();
    }

    @Override
    public void closeMe() {
        getActivity().finish();
    }

    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isVisible()) {
            mLoadingDialog.show(getFragmentManager(), "showLoadingDialog");
        }
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isResumed()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void setPresenter(ShopSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoadingDialog = ContentLoadingDialog.newInstance("选择中...");
        mLoadingDialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_select, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleView.setText(getString(R.string.shop_select_name));

        mAdapter = new ShopSelectListsAdapter();
        // 更新店铺
        mAdapter.setOnEditClickListener(
            position -> startActivity(new Intent(getContext(), ShopAddActivity.class)
                .putExtra(ShopAddFragment.ARGUMENT_TYPE, 1)
                .putExtra(ShopAddFragment.ARGUMENT_SHOP_ID, mData.get(position).merchantId)));
        // 切换店铺
        mAdapter.setOnItemClickListener(position -> mPresenter
            .setCurrentShop(mData.get(position).merchantId, mData.get(position).corporateName));
        mListsView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListsView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mDisposable = RxBus2.Companion.get().toObservable().subscribe(o -> {
            if (ShopAddFragment.BUS_SHOP_CREATE_SUCCESS.equals(o)) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void updateLists(List<ShopListsBean> data) {
        mData.clear();
        mData.addAll(data);
        if (mData.isEmpty()) {
            mListsView.setVisibility(View.GONE);
        } else {
            mAdapter.updateData(mData);
            mListsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mPresenter.unsubscribe();
    }
}
