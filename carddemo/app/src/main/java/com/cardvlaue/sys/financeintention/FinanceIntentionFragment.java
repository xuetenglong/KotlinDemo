package com.cardvlaue.sys.financeintention;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.PromoteAmountDialog;
import com.cardvlaue.sys.data.FinanceIntentBus;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.my.MyFragment;
import com.cardvlaue.sys.redenvelope.MoneyActivity;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.shopadd.ShopAddActivity;
import com.cardvlaue.sys.shopadd.ShopAddFragment;
import com.cardvlaue.sys.shopselect.ShopSelectActivity;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.ToastUtil;
import com.trello.rxlifecycle.components.support.RxFragment;
import io.reactivex.disposables.Disposable;
import java.util.List;
import timber.log.Timber;

public class FinanceIntentionFragment extends RxFragment implements FinanceIntentionContract.View {

    /**
     * 是否显示用户数据 true：显示 false：不显示
     */
    public static final String EXTRA_IS_LOAD_DATA = "FinanceIntentionFragment_EXTRA_IS_LOAD_DATA";

    public static final String BUS_SHOP_DATA = "FinanceIntentionFragment_BUS_SHOP_DATA";

    /**
     * 重置界面
     */
    public static final String BUS_INIT_DATA = "FinanceIntentionFragment_BUS_INIT_DATA";
    /**
     * 期限按钮
     */
    @BindViews({R.id.rb_finance_intention_three, R.id.rb_finance_intention_six,
        R.id.rb_finance_intention_nine, R.id.rb_finance_intention_twelve})
    List<RadioButton> mExpireViews;
    /**
     * 期限选中状态
     */
    @BindViews({R.id.view_finance_intention_three, R.id.view_finance_intention_six,
        R.id.view_finance_intention_nine, R.id.view_finance_intention_twelve})
    List<View> mTickViews;
    /**
     * 融资期限提示文本
     */
    @BindView(R.id.tv_finance_intention_tariff_tip)
    TextView mTariffTipView;
    /**
     * 标题
     */
    @BindView(R.id.tv_white_back)
    TextView mTitleTextView;
    /**
     * 融资店铺
     */
    @BindView(R.id.tv_finance_intent_shop)
    TextView mShopView;
    /**
     * 融资用途
     */
    @BindView(R.id.tv_finance_intent_use)
    TextView mUseView;
    /**
     * 融资金额
     */
    @BindView(R.id.et_finance_intention_amount_money)
    EditText mAmountView;
    /**
     * 红包
     */
    @BindView(R.id.tv_finance_intent_gift)
    TextView mGiftView;
    private FinanceIntentionContract.Presenter mPresenter;
    /**
     * 默认融资金额
     */
    private long defaultMoney = 100_000;

    /**
     * 期限、红包 ID、红包金额
     */
    private String lineStr = "12", giftId, giftAmount;

    private ContentLoadingDialog mLoadingDialog;

    /**
     * 是否加载数据 true：加载 false：不加载
     */
    private boolean isLoadData = true;

    private boolean isEmptyShop;

    /**
     * 是否选择店铺 true : 已选择 false ：未选择
     */
    private boolean isShopSelected;

    public static FinanceIntentionFragment newInstance() {
        return new FinanceIntentionFragment();
    }

    /**
     * 意向金额
     *
     * @param charSequence 金额
     */
    @OnTextChanged(R.id.et_finance_intention_amount_money)
    void changeIntentMoney(CharSequence charSequence) {
        mAmountView.setSelection(mAmountView.getText().length());
        if (!TextUtils.isDigitsOnly(charSequence)) {
            ToastUtil.showFailure(getContext(), "错误的金额");
            return;
        }
        defaultMoney = CheckUtil.convertIntentMoney(charSequence.toString());
//        String amountStr = String.valueOf(defaultMoney);
//        mAmountView.setText(amountStr);
//        mAmountView.setSelection(mAmountView.getText().length());
    }

    @Override
    public void setPresenter(@NonNull FinanceIntentionContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void changeShopStatus(boolean status) {
        isEmptyShop = status;
    }

    @Override
    public void showPromoteAmountDialog(String dialogMsg, String shopId) {
        PromoteAmountDialog promoteAmountDialog = PromoteAmountDialog
            .newInstance("提示信息", dialogMsg, "好的,马上修改");
        promoteAmountDialog
            .setOnClickOkListener(
                () -> startActivity(new Intent(getContext(), ShopAddActivity.class)
                    .putExtra(ShopAddFragment.ARGUMENT_TYPE, 1)
                    .putExtra(ShopAddFragment.ARGUMENT_SHOP_ID, shopId)));
        promoteAmountDialog
            .show(getActivity().getFragmentManager(), "FinanceIntentionFragment_tag");
    }

    /**
     * 点击提交
     */
    @OnClick(R.id.btn_intent_commit)
    void clickCommit() {
        if (!isShopSelected) {
            ToastUtil.showFailure(getContext(), "请选择融资店铺");
            return;
        }

        defaultMoney = CheckUtil.convertIntentMoney(mAmountView.getText().toString());
        String amountStr = String.valueOf(defaultMoney);
        mAmountView.setText(amountStr);

        String shopStr = mShopView.getText().toString();
        String intentStr = mUseView.getText().toString();
        mPresenter.updateUserInfo(shopStr, intentStr, amountStr, lineStr, giftId);
    }

    @Override
    public void initFaceData(UserInfoNewResponse s) {
        String corporateStr = s.corporateName;
        if (TextUtils.isEmpty(corporateStr)) {
            String businessNameStr = s.businessName;
            if (TextUtils.isEmpty(businessNameStr)) {
                isShopSelected = false;
                mShopView.setText(getString(R.string.finance_select));
            } else {
                isShopSelected = true;
                mShopView.setText(businessNameStr);
            }
        } else {
            isShopSelected = true;
            mShopView.setText(corporateStr);
        }

        String useStr = s.loanPurpose;
        if (TextUtils.isEmpty(useStr)) {
            mUseView.setText(getString(R.string.finance_select));
        } else {
            mUseView.setText(useStr);
        }
        String loanAmountStr = s.loanAmount;
        if (!TextUtils.isEmpty(loanAmountStr)) {
            mAmountView.setText(loanAmountStr);
        }
        changeExpireView(s.planFundTerm);
    }

    /**
     * 修改拟融资期限
     *
     * @param tempLine 期限数值
     */
    private void changeExpireView(String tempLine) {
        if (!TextUtils.isEmpty(tempLine)) {
            lineStr = tempLine;
        }
        Timber.e("意向融资期限：%s", lineStr);
        switch (lineStr) {
            case "3":
                mExpireViews.get(0).setChecked(true);
                break;
            case "6":
                mExpireViews.get(1).setChecked(true);
                break;
            case "9":
                mExpireViews.get(2).setChecked(true);
                break;
            case "12":
                mExpireViews.get(3).setChecked(true);
                break;
        }
    }

    /**
     * 点击红包
     */
    @OnClick(R.id.ll_finance_intention_gift)
    void clickGift() {
        defaultMoney = CheckUtil.convertIntentMoney(mAmountView.getText().toString());
        String amountStr = String.valueOf(defaultMoney);
        mAmountView.setText(amountStr);

        startActivity(new Intent(getActivity(), MoneyActivity.class)
            .putExtra("loanAmount", amountStr)
            .putExtra("typeId", giftId)
            .putExtra("amount", giftAmount)
        );
    }

    /**
     * 点击减少意向融资金额
     */
    @OnClick(R.id.ib_finance_intention_add)
    void clickAddMoney() {
        if (defaultMoney < CheckUtil.MAX_MONEY) {
            defaultMoney += CheckUtil.MIN_MONEY;
            mAmountView.setText(String.valueOf(defaultMoney));
            mAmountView.setSelection(mAmountView.getText().length());
        }
    }

    /**
     * 点击增加意向融资金额
     */
    @OnClick(R.id.ib_finance_intention_delete)
    void clickDelMoney() {
        if (defaultMoney > CheckUtil.MIN_MONEY) {
            defaultMoney -= CheckUtil.MIN_MONEY;
            mAmountView.setText(String.valueOf(defaultMoney));
            mAmountView.setSelection(mAmountView.getText().length());
        }
    }

    /**
     * 点击用途
     */
    @OnClick(R.id.ll_finance_intention_use)
    void clickUse() {
        startActivity(new Intent(getContext(), FinanceUseActivity.class));
    }

    /**
     * 点击商铺
     */
    @OnClick(R.id.ll_finance_intention_shop)
    void clickShop() {
        if (!isEmptyShop) {
            startActivity(new Intent(getContext(), ShopSelectActivity.class));
        } else {
            startActivity(new Intent(getContext(), ShopAddActivity.class)
                .putExtra(ShopAddFragment.ARGUMENT_TYPE, 0));
        }
    }

    /**
     * 后退
     */
    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        try {
            //((CVApplication) getActivity().getApplication()).getQueue().back(getActivity());
            getActivity().finish();
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLoadData = getActivity().getIntent().getBooleanExtra(EXTRA_IS_LOAD_DATA, true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance_intention, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleTextView.setText(getString(R.string.finance_intention_name));

        mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
        mLoadingDialog.setCancelable(false);

        mExpireViews.get(0).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (int i = 0; i < mTickViews.size(); i++) {
                    if (i == 0) {
                        mTickViews.get(i).setVisibility(View.VISIBLE);
                    } else {
                        mTickViews.get(i).setVisibility(View.INVISIBLE);
                    }
                }
                mTariffTipView.setText("三个月 月费率2.40%-2.50%");
                lineStr = "3";
            }
        });

        mExpireViews.get(1).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (int i = 0; i < mTickViews.size(); i++) {
                    if (i == 1) {
                        mTickViews.get(i).setVisibility(View.VISIBLE);
                    } else {
                        mTickViews.get(i).setVisibility(View.INVISIBLE);
                    }
                }
                mTariffTipView.setText("六个月 月费率1.95%-2.00%");
                lineStr = "6";
            }
        });

        mExpireViews.get(2).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (int i = 0; i < mTickViews.size(); i++) {
                    if (i == 2) {
                        mTickViews.get(i).setVisibility(View.VISIBLE);
                    } else {
                        mTickViews.get(i).setVisibility(View.INVISIBLE);
                    }
                }
                mTariffTipView.setText("九个月 月费率1.65%-1.70%");
                lineStr = "9";
            }
        });

        mExpireViews.get(3).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                for (int i = 0; i < mTickViews.size(); i++) {
                    if (i == 3) {
                        mTickViews.get(i).setVisibility(View.VISIBLE);
                    } else {
                        mTickViews.get(i).setVisibility(View.INVISIBLE);
                    }
                }
                mTariffTipView.setText("十二个月 月费率1.50%-1.55%");
                lineStr = "12";
            }
        });
    }

    private Disposable mDisposable;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDisposable = RxBus2.Companion.get().toObservable().subscribe(o -> {
            // 融资用途
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (FinanceUseActivity.BUS_FINANCE_USE.equals(busIndustrySelect.getBus())) {
                    String useStr = busIndustrySelect.getTitle();
                    if (TextUtils.isEmpty(useStr)) {
                        Timber.e("融资用途获取失败");
                    } else {
                        mUseView.setText(useStr);
                    }
                }
            }
        });

        RxBus.getDefaultBus().toObserverable().compose(bindToLifecycle()).subscribe(o -> {
            if (o instanceof FinanceIntentBus) {
                FinanceIntentBus bus = (FinanceIntentBus) o;
                Timber.e("收到店铺信息:%s", JSON.toJSONString(bus));
                if (BUS_SHOP_DATA.equals(bus.event)) {
                    String shopStr = bus.shop;
                    if (TextUtils.isEmpty(shopStr)) {
                        isShopSelected = false;
                        mShopView.setText(getString(R.string.finance_select));
                    } else {
                        isShopSelected = true;
                        mShopView.setText(shopStr);
                    }

                    String useStr = bus.use;
                    if (TextUtils.isEmpty(useStr)) {
                        mUseView.setText(getString(R.string.finance_select));
                    } else {
                        mUseView.setText(useStr);
                    }
                    String loanAmountStr = bus.amount;
                    if (!TextUtils.isEmpty(loanAmountStr)) {
                        mAmountView.setText(loanAmountStr);
                    }
                    changeExpireView(bus.line);
                }
            }

            if (BUS_INIT_DATA.equals(o)) {
                initFaceData(new UserInfoNewResponse());
            }

            // 红包
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (MyFragment.BUS_MONEY_CODE.equals(busIndustrySelect.getBus())) {
                    Timber.e("BUS_MONEY_CODE");
                    giftAmount = busIndustrySelect.getAmount();
                    giftId = busIndustrySelect.getTypeId();
                    if (!TextUtils.isEmpty(giftAmount) && !TextUtils.isEmpty(giftId)) {
                        Timber.e("giftAmount:%s||giftId:%s", giftAmount, giftId);
                        mGiftView.setText(TextUtils.isEmpty(giftAmount) ? "0元" : giftAmount + "元");
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();

        if (isLoadData) {
            isLoadData = false;
            mPresenter.loadUserData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show(getFragmentManager(), "showLoadingDialog");
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
        }
    }

    /**
     * 下一步
     */
    @Override
    public void gotoNext() {
        try {
            ((CVApplication) getActivity().getApplication()).getQueue().next(getActivity(), 10);
             getActivity().finish();
        } catch (Exception e) {
            ToastUtil.showFailure(getContext(), "下一步方法异常");
        }
    }

}
