package com.cardvlaue.sys.financeway;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.alipayverify.AlipayVerifyActivity;
import com.cardvlaue.sys.amount.AmountCountdownActivity;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.posadd.PosAddActivity;
import com.cardvlaue.sys.posmanagement.PosManagementActivity;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import com.haozhang.lib.SlantedTextView;
import com.trello.rxlifecycle.components.support.RxFragment;
import rx.android.schedulers.AndroidSchedulers;

public class FinanceWayFragment extends RxFragment implements FinanceWayContract.View {

    private static final String POS_OK = "已完成";

    private static final String POS_NO = "未验证";
    /**
     * 标题文字
     */
    @BindView(R.id.tv_white_back)
    TextView mTitleView;
    /**
     * POS 状态
     */
    @BindView(R.id.stv_finance_way_pos)
    SlantedTextView mPosStatusView;
    /**
     * 支付宝 状态
     */
    @BindView(R.id.stv_finance_way_alipay)
    SlantedTextView mAlipayStatusView;
    private FinanceWayContract.Presenter mPresenter;
    private boolean isPosEmpty;

    /*
     * 融资意向的经营地址判断
    private PromoteAmountDialog mPromoteAmountDialog;*/
    private ContentLoadingDialog mLoadingDialog;

    /**
     * 是否已授信
     */
    private boolean isCredited;

    /**
     * 是否有额度
     */
    private boolean isAmount;

    public static FinanceWayFragment newInstance() {
        return new FinanceWayFragment();
    }

    @Override
    public void changePosStatus(boolean status) {
        if (status) {
            mPosStatusView.setText(POS_OK);
            mPosStatusView.setSlantedBackgroundColor(Color.parseColor("#FEA545"));
        } else {
            mPosStatusView.setText(POS_NO);
            mPosStatusView.setSlantedBackgroundColor(Color.parseColor("#d8d8d8"));
        }
    }

    @Override
    public void checkPosAdd(boolean status) {
        isPosEmpty = status;
    }

    @OnClick(R.id.btn_way_commit)
    void clickCommit() {
        if (!isCredited) {
            ToastUtil.showFailure(getContext(), "该店铺未授信");
        } else {
            if (isAmount) {
                mPresenter.setCurrentStep();
            } else {
                String posStatusStr = mPosStatusView.getText();
                String payStatusStr = mAlipayStatusView.getText();
                if (POS_NO.equals(posStatusStr) && POS_NO.equals(payStatusStr)) {
                    ToastUtil.showFailure(getContext(), "请完成POS流水或者支付宝验证。");
                } else {
                    mPresenter.setCurrentStep();
                }
            }
        }
    }

    /**
     * 点击商编
     */
    @OnClick(R.id.ll_finance_way_pos)
    void clickPos() {
        if (!isCredited) {
            ToastUtil.showFailure(getContext(), "该店铺未授信");
        } else {
            if (isPosEmpty) {
                startActivity(new Intent(getContext(), PosAddActivity.class));
            } else {
                startActivity(new Intent(getContext(), PosManagementActivity.class));
            }
        }
    }

    @Override
    public void changeCreditStatus(boolean status) {
        isCredited = status;
    }

    @Override
    public void changeAlipayStatus(String status) {
        if (status.equals("1")) {
            mAlipayStatusView.setText(POS_OK);
            mAlipayStatusView.setSlantedBackgroundColor(Color.parseColor("#359DF5"));
        } else {
            mAlipayStatusView.setText(POS_NO);
            mAlipayStatusView.setSlantedBackgroundColor(Color.parseColor("#d8d8d8"));
        }
    }

    @Override
    public void changeAmountStatus(boolean status) {
        isAmount = status;
    }

    /**
     * 点击支付宝
     */
    @OnClick(R.id.ll_finance_way_alipay)
    void clickAlipay() {
        if (!"已完成".equals(mAlipayStatusView.getText())) {
            startActivity(new Intent(getContext(), AlipayVerifyActivity.class));
        }
    }

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        try {
            ((CVApplication) getActivity().getApplicationContext()).getQueue().back(getActivity());
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gotoNext() {
        startActivity(new Intent(getContext(), AmountCountdownActivity.class));
    }

    @Override
    public void showLoadingDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isVisible()) {
            mLoadingDialog.show(getFragmentManager(), "showLoadingDialog");
        }
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void setPresenter(FinanceWayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*mPromoteAmountDialog = PromoteAmountDialog.newInstance("提示信息", "您所选融资店铺的经营地址不合法，请修改后再进行下一步操作！", "好的,马上修改");
        mPromoteAmountDialog.setOnClickOkListener(() -> startActivity(new Intent(getActivity(), ShopAddActivity.class)));*/

        mLoadingDialog = ContentLoadingDialog.newInstance("加载中...");
        mLoadingDialog.setCancelable(false);

        RxBus.getDefaultBus().toObserverable()
            .compose(bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (AmountCountdownActivity.BUS_AMOUNT_CODE.equals(busIndustrySelect.getBus())) {
                    try {
                        ((CVApplication) getActivity().getApplication()).getQueue()
                            .next(getActivity(), 30);
                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance_calculate_way, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitleView.setText("计算融资额度");
        mPosStatusView.setText(POS_NO);
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.start();
    }
}
