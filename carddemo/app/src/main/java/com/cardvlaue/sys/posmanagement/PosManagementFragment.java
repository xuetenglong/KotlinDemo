package com.cardvlaue.sys.posmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.MidsItemResponse;
import com.cardvlaue.sys.data.PosBus;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.posadd.PosAddActivity;
import com.cardvlaue.sys.posadd.PosAddVerifyDialog;
import com.cardvlaue.sys.util.RxBus;
import com.trello.rxlifecycle.components.support.RxFragment;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class PosManagementFragment extends RxFragment implements PosManagementContract.View {

    private static final String BUS_POS_VERIFY = "PosManagementFragment_BUS_POS_VERIFY";
    @BindView(R.id.tv_white_back)
    TextView mTitleView;
    @BindView(R.id.rv_pos_management_lists)
    RecyclerView mListsView;
    private PosManagementContract.Presenter mPresenter;
    private PosManagementAdapter mAdapter;

    private List<MidsItemResponse> mData = new ArrayList<>();

    private ContentLoadingDialog mLoadingDialog;

    public static PosManagementFragment newInstance() {
        return new PosManagementFragment();
    }

    @Override
    public void showVerifyDialog(String verifyId, String question, ArrayList<String> data) {
        PosAddVerifyDialog.newInstance(BUS_POS_VERIFY, verifyId, question, data, "2")
            .show(getFragmentManager(), "showVerifyDialog");
    }

    /**
     * 跳转到商编添加
     */
    @OnClick(R.id.tv_pos_add)
    void clickAddPos() {
        startActivity(new Intent(getContext(), PosAddActivity.class));
        getActivity().finish();
    }

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        getActivity().finish();
    }

    @Override
    public void setPresenter(@NonNull PosManagementContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefaultBus().toObserverable().compose(bindToLifecycle()).subscribe(o -> {
            if (o instanceof PosBus) {
                PosBus posBus = (PosBus) o;
                if (BUS_POS_VERIFY.equals(posBus.event)) {
                    Timber.e("验证商编-管理");
                    mPresenter.verifyMids(posBus.mId);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pos_management, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitleView.setText(getString(R.string.pos_management_name));

        mAdapter = new PosManagementAdapter();
        mAdapter.setOnItemClickListener(position -> {
            if ("U".equals(mData.get(position).getStatus())) {
                mPresenter.questMids(mData.get(position).getMid());
            }
        });
        mListsView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListsView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.start();

        showLoadingDialog("商编加载中...");
        mPresenter.loadPosLists();
    }

    @Override
    public void updateLists(List<MidsItemResponse> data) {
        mData.clear();
        mData.addAll(data);
        mAdapter.updateData(mData);
    }

    @Override
    public void showLoadingDialog(String msg) {
        mLoadingDialog = ContentLoadingDialog.newInstance(msg);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show(getFragmentManager(), "showLoadingDialog");
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
        }
    }

}
