package com.cardvlaue.sys.shopadd;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.bigkoo.pickerview.TimePickerView;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.EventConst;
import com.cardvlaue.sys.data.SearchSelectEvent;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.searchselect.SearchSelectActivity;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.StringUtils;
import com.cardvlaue.sys.util.ToastUtil;
import com.trello.rxlifecycle2.components.support.RxFragment;
import io.reactivex.Flowable;
import java.util.Date;
import timber.log.Timber;

public class ShopAddFragment extends RxFragment implements ShopAddContract.View {

    /**
     * 店铺添加成功，关闭店铺管理
     */
    public static final String BUS_SHOP_CREATE_SUCCESS = "ShopAddFragment_BUS_SHOP_CREATE_SUCCESS";

    /**
     * 0 : 新增店铺 1 : 更新店铺
     */
    public static final String ARGUMENT_TYPE = "type";

    public static final String STORE_TYPE = "store";

    /**
     * 店铺列表的 ID
     */
    public static final String ARGUMENT_SHOP_ID = "shop_id";
    /**
     * 标题文字
     */
    @BindView(R.id.tv_white_back)
    TextView mTitleTextView;
    /**
     * 标题背景
     */
    @BindView(R.id.rl_white_content)
    RelativeLayout mTitleContentView;
    @BindView(R.id.ll_white_root)
    LinearLayout mRootView;
    /**
     * 后退按钮
     */
    @BindView(R.id.ibtn_white_back)
    ImageButton mBackView;
    /**
     * 所属行业
     */
    @BindView(R.id.tv_shop_add_industry)
    TextView mIndustryView;
    /**
     * 营业执照注册号
     */
    @BindView(R.id.et_shop_add_register_id)
    EditText mRegIdView;
    /**
     * 营业地址
     */
    @BindView(R.id.tv_shop_add_shop_address)
    TextView mAddressView;
    /**
     * 门店数
     */
    @BindView(R.id.et_shop_add_shop_number)
    EditText mNumberView;
    /**
     * 企业经营名称
     */
    @BindView(R.id.et_shop_add_company_name)
    EditText mNameView;
    /**
     * 是否有店铺租赁合同
     */
    @BindView(R.id.tv_shop_add_has_contract)
    TextView mHasContractView;
    /**
     * 房东姓名
     */
    @BindView(R.id.et_shop_add_landlord_name)
    EditText mLandlordNameView;
    /**
     * 房东电话号码
     */
    @BindView(R.id.et_shop_add_landlord_phone)
    EditText mLandlordPhoneView;
    /**
     * 无店铺租赁合同原因
     */
    @BindView(R.id.tv_shop_add_why_no_contract)
    TextView mWhyNoContractView;
    /**
     * 合同开始时间
     */
    @BindView(R.id.tv_shop_add_contract_start_time)
    TextView mStartContractView;
    /**
     * 合同结束时间
     */
    @BindView(R.id.tv_shop_add_contract_end_time)
    TextView mEndContractView;
    /**
     * 年租金
     */
    @BindView(R.id.et_shop_add_rental)
    EditText mRentalView;
    /**
     * 有租赁合同
     */
    @BindView(R.id.ll_shop_add_contract_yes)
    LinearLayout mContractYesView;
    /**
     * 无租赁合同
     */
    @BindView(R.id.ll_shop_add_contract_no)
    LinearLayout mContractNoView;
    /**
     * 合同丢失
     */
    @BindView(R.id.ll_shop_add_contract_loss)
    LinearLayout mContractLossView;
    /**
     * 无偿使用
     */
    @BindView(R.id.ll_shop_add_free_use)
    LinearLayout mFreeUseView;
    /**
     * 业主姓名
     */
    @BindView(R.id.et_shop_add_proprietor_name)
    EditText mProprietorNameView;
    /**
     * 业主电话
     */
    @BindView(R.id.et_shop_add_proprietor_phone)
    EditText mProprietorPhoneView;
    @BindView(R.id.btn_shop_add_commit)
    Button mCommit;
    @BindString(R.string.shop_add_select)
    String selectStr;

    @BindView(R.id.et_shop_add_address_details)
    EditText mDetailAddress;
    private ShopAddContract.Presenter mPresenter;
    /**
     * 1：更新 0：创建
     */
    private int faceType;

    /**
     * 查看店铺
     */
    private int storeType;

    /**
     * 待更新店铺的编号
     */
    private String shopId;

    private ContentLoadingDialog mLoadingDialog;

    /**
     * 行业编号
     */
    private String industryId;

    /**
     * 营业地址经纬度
     */
    private String gpsAddress;

    private TimePickerView mTimePickerView;

    /**
     * 当前时间类型（开始时间、结束时间）
     */
    private int currentTimePick;

    public static ShopAddFragment newInstance() {
        return new ShopAddFragment();
    }

    @Override
    public void createSuccess() {
        RxBus2.Companion.get().send(BUS_SHOP_CREATE_SUCCESS);
        updateSuccess();
    }

    @Override
    public void updateSuccess() {
        getActivity().finish();
    }

    @OnClick(R.id.btn_shop_add_commit)
    void clickCommit() {
        ShopAddRequest user = new ShopAddRequest();
        if (TextUtils.isEmpty(industryId)) {
            ToastUtil.showFailure(getContext(), "请选择所属行业");
            return;
        } else {
            String[] ids = industryId.split(",");
            user.industryGId = ids[0];
            user.industryPId = ids[1];
            user.industryCId = ids[2];
        }
        String regIdStr = mRegIdView.getText().toString();
        if (TextUtils.isEmpty(regIdStr)) {
            ToastUtil.showFailure(getContext(), "请输入营业执照注册号");
            return;
        } else if (!CheckUtil.isValidRizNO(regIdStr)) {
            ToastUtil.showFailure(getContext(), "错误的营业执照注册号");
            return;
        } else {
            user.bizRegisterNo = regIdStr;
        }
        String addressStr = mAddressView.getText().toString();
        if (TextUtils.isEmpty(addressStr) || selectStr.equals(addressStr)) {
            ToastUtil.showFailure(getContext(), "请选择营业地址");
            return;
        } else {
            user.businessAddress = addressStr;
            user.bizAddrLonlat = gpsAddress;
        }
        String numberStr = mNumberView.getText().toString();
        if (TextUtils.isEmpty(numberStr)) {
            ToastUtil.showFailure(getContext(), "请输入门店数");
            return;
        } else if (!TextUtils.isDigitsOnly(numberStr)) {
            ToastUtil.showFailure(getContext(), "门店数不是数字");
            return;
        } else {
            user.numLocations = numberStr;
        }
        String nameStr = mNameView.getText().toString();
        if (TextUtils.isEmpty(nameStr)) {
            ToastUtil.showFailure(getContext(), "请输入企业经营名称");
            return;
        } else {
            user.corporateName = nameStr;
        }
        String detailStr = mDetailAddress.getText().toString().trim();
        if (TextUtils.isEmpty(detailStr)) {
            ToastUtil.showFailure(getContext(), "请输入店铺详细地址");
            return;
        } else {
            user.businessAccurateAddress=detailStr;
        }
        String hasContractStr = mHasContractView.getText().toString();
        if (TextUtils.isEmpty(hasContractStr) || selectStr.equals(hasContractStr)) {
            ToastUtil.showFailure(getContext(), "请选择是否有店铺租赁合同");
            return;
        } else {
            switch (hasContractStr) {
                case "有":
                    user.hasLeaseContract = "1";
                    String landlordNameStr = mLandlordNameView.getText().toString();
                    if (TextUtils.isEmpty(landlordNameStr)) {
                        ToastUtil.showFailure(getContext(), "请输入房东姓名");
                        return;
                    } else {
                        user.landlordName = landlordNameStr;
                    }
                    String landlordPhoneStr = StringUtils.INSTANCE
                        .notEmptyPhone(mLandlordPhoneView.getText().toString().trim());
                    if (TextUtils.isEmpty(landlordPhoneStr)) {
                        ToastUtil.showFailure(getContext(), "请输入房东电话号码");
                        return;
                    } else if (!CheckUtil.isMobilePhone(landlordPhoneStr) && !CheckUtil
                        .isLandlinePhone(landlordPhoneStr)) {
                        ToastUtil.showFailure(getContext(), "非法房东电话号码");
                        return;
                    } else {
                        user.landlordPhone = landlordPhoneStr;
                    }
                    break;
                case "无":
                    user.hasLeaseContract = "0";
                    String WhyNoContractStr = mWhyNoContractView.getText().toString();
                    if (TextUtils.isEmpty(WhyNoContractStr) || selectStr.equals(WhyNoContractStr)) {
                        ToastUtil.showFailure(getContext(), "请选择无店铺租赁合同原因");
                        return;
                    } else {
                        switch (WhyNoContractStr) {
                            case "自有房产":
                                user.noLeaseContractReason = "1";
                                break;
                            case "无偿使用":
                                user.noLeaseContractReason = "2";
                                String freeProprietorNameStr = mProprietorNameView.getText()
                                    .toString();
                                if (TextUtils.isEmpty(freeProprietorNameStr)) {
                                    ToastUtil.showFailure(getContext(), "请输入业主姓名");
                                    return;
                                } else {
                                    user.proprietorName = freeProprietorNameStr;
                                }
                                String freeProprietorPhoneStr = StringUtils.INSTANCE.notEmptyPhone(
                                    mProprietorPhoneView.getText().toString().trim());
                                if (TextUtils.isEmpty(freeProprietorPhoneStr)) {
                                    ToastUtil.showFailure(getContext(), "请输入业主电话");
                                    return;
                                } else if (!CheckUtil.isMobilePhone(freeProprietorPhoneStr)) {
                                    ToastUtil.showFailure(getContext(), "非法业主电话号码");
                                    return;
                                } else {
                                    user.proprietorPhone = freeProprietorPhoneStr;
                                }
                                break;
                            case "合同丢失":
                                user.noLeaseContractReason = "3";
                                String startContractStr = mStartContractView.getText().toString();
                                if (TextUtils.isEmpty(startContractStr) || selectStr
                                    .equals(startContractStr)) {
                                    ToastUtil.showFailure(getContext(), "请选择合同开始时间");
                                    return;
                                } else {
                                    user.leaseContractStartTime = startContractStr;
                                }
                                String endContractStr = mEndContractView.getText().toString();
                                if (TextUtils.isEmpty(endContractStr) || selectStr
                                    .equals(endContractStr)) {
                                    ToastUtil.showFailure(getContext(), "请选择合同结束时间");
                                    return;
                                } else {
                                    user.leaseContractEndTime = endContractStr;
                                }
                                String yearMoneyStr = mRentalView.getText().toString();
                                if (TextUtils.isEmpty(yearMoneyStr)) {
                                    ToastUtil.showFailure(getContext(), "请输入年租金");
                                    return;
                                } else {
                                    user.leaseYearAmt = yearMoneyStr;
                                }
                                String lossProprietorNameStr = mProprietorNameView.getText()
                                    .toString();
                                if (TextUtils.isEmpty(lossProprietorNameStr)) {
                                    ToastUtil.showFailure(getContext(), "请输入业主姓名");
                                    return;
                                } else {
                                    user.proprietorName = lossProprietorNameStr;
                                }
                                String lossProprietorPhoneStr = StringUtils.INSTANCE.notEmptyPhone(
                                    mProprietorPhoneView.getText().toString().trim());
                                if (TextUtils.isEmpty(lossProprietorPhoneStr)) {
                                    ToastUtil.showFailure(getContext(), "请输入业主电话");
                                    return;
                                } else if (!CheckUtil.isMobilePhone(lossProprietorPhoneStr)) {
                                    ToastUtil.showFailure(getContext(), "非法业主电话号码");
                                    return;
                                } else {
                                    user.proprietorPhone = lossProprietorPhoneStr;
                                }
                                break;
                        }
                    }
                    break;
            }
        }
        switch (faceType) {
            case 0:
                Timber.e("更新");
                user.isCreate = "1";
                break;
            case 1:
                Timber.e("创建并更新");
                user.isCreate = "0";
                break;
        }
        mPresenter.commitShopInfo(shopId, user);
    }

    /**
     * 点击合同结束时间
     */
    @OnClick(R.id.tv_shop_add_contract_end_time)
    void clickEndTime() {
        if (!mTimePickerView.isShowing()) {
            currentTimePick = 2;
            mTimePickerView.show();
        }
    }

    /**
     * 点击合同开始时间
     */
    @OnClick(R.id.tv_shop_add_contract_start_time)
    void clickStartTime() {
        if (!mTimePickerView.isShowing()) {
            currentTimePick = 1;
            mTimePickerView.show();
        }
    }

    @OnTextChanged(R.id.tv_shop_add_why_no_contract)
    void changeWhyNoContract(CharSequence charSequence) {
        switch (charSequence.toString()) {
            case "自有房产":
                mContractLossView.setVisibility(View.GONE);
                mFreeUseView.setVisibility(View.GONE);
                break;
            case "无偿使用":
                mContractLossView.setVisibility(View.GONE);
                mFreeUseView.setVisibility(View.VISIBLE);
                break;
            case "合同丢失":
                mContractLossView.setVisibility(View.VISIBLE);
                mFreeUseView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 点击无租赁合同
     */
    @OnClick(R.id.tv_shop_add_why_no_contract)
    void clickWhyNoContract() {
        startActivity(new Intent(getContext(), ContractSelectActivity.class)
            .putExtra(ContractSelectActivity.EXTRA_HAS_CONTRACT, 1));
    }

    @OnTextChanged(R.id.tv_shop_add_has_contract)
    void changeHasContract(CharSequence charSequence) {
        switch (charSequence.toString()) {
            case "有":
                mContractYesView.setVisibility(View.VISIBLE);
                mContractNoView.setVisibility(View.GONE);
                break;
            case "无":
                mContractYesView.setVisibility(View.GONE);
                mContractNoView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 点击是否有租赁合同
     */
    @OnClick(R.id.tv_shop_add_has_contract)
    void clickHasContract() {
        startActivity(new Intent(getContext(), ContractSelectActivity.class)
            .putExtra(ContractSelectActivity.EXTRA_HAS_CONTRACT, 0));
    }

    /**
     * 点击营业地址
     */
    @OnClick(R.id.tv_shop_add_shop_address)
    void clickAddress() {
        startActivity(new Intent(getContext(), AddressSearchActivity.class));
    }

    /**
     * 点击所属行业
     */
    @OnClick(R.id.tv_shop_add_industry)
    void clickIndustry() {
        startActivity(new Intent(getContext(), SearchSelectActivity.class)
            .putExtra(SearchSelectActivity.Companion.getTYPE_FLAG(), 1));
    }

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        getActivity().finish();
    }

    @Override
    public void initFaceData(UserInfoNewResponse userInfo) {
        String gName = userInfo.industryGName;
        String pName = userInfo.industryPName;
        String cName = userInfo.industryCName;
        if (!TextUtils.isEmpty(gName) && !TextUtils.isEmpty(pName) && !TextUtils.isEmpty(cName)) {
            String industryName = gName + "," + pName + "," + cName;
            mIndustryView.setText(industryName);
            mIndustryView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.lists_item_text_left));
            industryId =
                userInfo.industryGId + "," + userInfo.industryPId + "," + userInfo.industryCId;
            Timber.e("InitIndustry:%s||%s", industryName, industryId);
        }
        mRegIdView.setText(userInfo.bizRegisterNo);
        String addressStr = userInfo.businessAddress;
        if (!TextUtils.isEmpty(addressStr)) {
            gpsAddress = userInfo.bizAddrLonlat;
            mAddressView.setText(addressStr);
            mAddressView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.lists_item_text_left));
        }
        mNumberView.setText(userInfo.numLocations);
        String corporateStr = userInfo.corporateName;
        if (TextUtils.isEmpty(corporateStr)) {
            String businessNameStr = userInfo.businessName;
            if (TextUtils.isEmpty(businessNameStr)) {
                mNameView.setText(getString(R.string.shop_add_select));
            } else {
                mNameView.setText(businessNameStr);
            }
        } else {
            mNameView.setText(corporateStr);
        }
        mDetailAddress.setText(userInfo.businessAccurateAddress);
        String hasLeaseContractStr = userInfo.hasLeaseContract;
        if ("0".equals(hasLeaseContractStr)) {
            mHasContractView.setText("无");
            mHasContractView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.lists_item_text_left));

            String noContractReason = userInfo.noLeaseContractReason;
            if (!TextUtils.isEmpty(noContractReason)) {
                String proprietorNameStr = userInfo.proprietorName;
                if (!TextUtils.isEmpty(proprietorNameStr)) {
                    mProprietorNameView.setText(proprietorNameStr);
                }
                String proprietorPhoneStr = userInfo.proprietorPhone;
                if (!TextUtils.isEmpty(proprietorPhoneStr)) {
                    mProprietorPhoneView.setText(proprietorPhoneStr);
                }
                mWhyNoContractView
                    .setTextColor(
                        ContextCompat.getColor(getContext(), R.color.lists_item_text_left));
                switch (noContractReason) {
                    case "1":
                        mWhyNoContractView.setText("自有房产");
                        break;
                    case "2":
                        mWhyNoContractView.setText("无偿使用");
                        break;
                    case "3":
                        mWhyNoContractView.setText("合同丢失");
                        String leaseContractStartTimeStr = userInfo.leaseContractStartTime;
                        if (!TextUtils.isEmpty(leaseContractStartTimeStr)) {
                            mStartContractView.setText(leaseContractStartTimeStr);
                            mStartContractView
                                .setTextColor(ContextCompat
                                    .getColor(getContext(), R.color.lists_item_text_left));
                        }
                        String leaseContractEndTimeStr = userInfo.leaseContractEndTime;
                        if (!TextUtils.isEmpty(leaseContractEndTimeStr)) {
                            mEndContractView.setText(leaseContractEndTimeStr);
                            mEndContractView
                                .setTextColor(ContextCompat
                                    .getColor(getContext(), R.color.lists_item_text_left));
                        }
                        String leaseYearAmtStr = userInfo.leaseYearAmt;
                        if (!TextUtils.isEmpty(leaseYearAmtStr)) {
                            mRentalView.setText(leaseYearAmtStr);
                        }
                        break;
                }
            }
        } else if ("1".equals(hasLeaseContractStr)) {
            mHasContractView.setText("有");
            mHasContractView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.lists_item_text_left));
            mLandlordNameView.setText(userInfo.landlordName);
            mLandlordPhoneView.setText(userInfo.landlordPhone);
        }
    }

    @Override
    public void setPresenter(ShopAddContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        faceType = getActivity().getIntent().getIntExtra(ARGUMENT_TYPE, -1);
        storeType = getActivity().getIntent().getIntExtra(STORE_TYPE, -1);
        shopId = getActivity().getIntent().getStringExtra(ARGUMENT_SHOP_ID);

        mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
        mLoadingDialog.setCancelable(false);

        mTimePickerView = new TimePickerView(getActivity(), TimePickerView.Type.YEAR_MONTH_DAY);
        mTimePickerView.setRange(1900, 2100);
        mTimePickerView.setTime(new Date());
        mTimePickerView.setCyclic(false);
        mTimePickerView.setCancelable(true);
        mTimePickerView.setOnTimeSelectListener(date -> {
            switch (currentTimePick) {
                case 1:
                    if (date.getTime() > System.currentTimeMillis()) {
                        ToastUtil.showFailure(getContext(), "合同开始时间不能大于当前时间");
                    } else {
                        mStartContractView
                            .setText(DateFormat.format("yyyy-MM-dd", date).toString());
                        mStartContractView
                            .setTextColor(
                                ContextCompat.getColor(getContext(), R.color.lists_item_text_left));
                    }
                    break;
                case 2:
                    if (date.getTime() < System.currentTimeMillis()) {
                        ToastUtil.showFailure(getContext(), "合同结束时间不能小于当前时间");
                    } else {
                        mEndContractView
                            .setTextColor(
                                ContextCompat.getColor(getContext(), R.color.lists_item_text_left));
                        mEndContractView.setText(DateFormat.format("yyyy-MM-dd", date).toString());
                    }
                    break;
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_add, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switch (faceType) {
            case 0:
                mTitleTextView.setText(getString(R.string.shop_add_name));
                break;
            case 1:
                mTitleTextView.setText("更新店铺");
                mPresenter.obtainShopInfo(shopId);
                break;
        }

        if (storeType == 2) {
            mTitleTextView.setText("查看店铺");
            mRootView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            mTitleContentView
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            Drawable mIndustryViewImg = ContextCompat
                .getDrawable(getContext(), R.drawable.mq_ic_add_img);
            mIndustryViewImg.setBounds(0, 0, 0, 0);
            mIndustryView.setCompoundDrawables(mIndustryViewImg, mIndustryViewImg, mIndustryViewImg,
                mIndustryViewImg);
            mIndustryView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mIndustryView.setClickable(false);

            mRegIdView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mRegIdView.setEnabled(false);

            Drawable mAddressViewImg = ContextCompat
                .getDrawable(getContext(), R.drawable.mq_ic_add_img);
            mAddressViewImg.setBounds(0, 0, 0, 0);
            mAddressView
                .setCompoundDrawables(mAddressViewImg, mAddressViewImg, mAddressViewImg,
                    mAddressViewImg);
            mAddressView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mAddressView.setClickable(false);

            mNumberView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mNumberView.setEnabled(false);

            mNameView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mNameView.setEnabled(false);

            Drawable mHasContractViewImg = ContextCompat
                .getDrawable(getContext(), R.drawable.mq_ic_add_img);
            mHasContractViewImg.setBounds(0, 0, 0, 0);
            mHasContractView
                .setCompoundDrawables(mHasContractViewImg, mHasContractViewImg, mHasContractViewImg,
                    mHasContractViewImg);
            mHasContractView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mHasContractView.setClickable(false);

            mProprietorPhoneView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mProprietorPhoneView.setEnabled(false);

            mProprietorNameView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mProprietorNameView.setEnabled(false);

            mRentalView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mRentalView.setEnabled(false);

            mLandlordNameView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mLandlordNameView.setEnabled(false);
            mLandlordPhoneView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mLandlordPhoneView.setEnabled(false);

            Drawable mStartContractViewImg = ContextCompat
                .getDrawable(getContext(), R.drawable.mq_ic_add_img);
            mStartContractViewImg.setBounds(0, 0, 0, 0);
            mStartContractView
                .setCompoundDrawables(mStartContractViewImg, mStartContractViewImg,
                    mStartContractViewImg,
                    mStartContractViewImg);
            mStartContractView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mStartContractView.setClickable(false);

            Drawable mEndContractViewImg = ContextCompat
                .getDrawable(getContext(), R.drawable.mq_ic_add_img);
            mEndContractViewImg.setBounds(0, 0, 0, 0);
            mEndContractView
                .setCompoundDrawables(mEndContractViewImg, mEndContractViewImg, mEndContractViewImg,
                    mEndContractViewImg);
            mEndContractView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mEndContractView.setClickable(false);

            Drawable mWhyNoContractViewImg = ContextCompat
                .getDrawable(getContext(), R.drawable.mq_ic_add_img);
            mWhyNoContractViewImg.setBounds(0, 0, 0, 0);
            mWhyNoContractView
                .setCompoundDrawables(mWhyNoContractViewImg, mWhyNoContractViewImg,
                    mWhyNoContractViewImg,
                    mWhyNoContractViewImg);
            mWhyNoContractView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.shop_add_text_hint));
            mWhyNoContractView.setClickable(false);

            mCommit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RxBus2.Companion.get().toObservable()
            .compose(bindToLifecycle())
            .subscribe(o -> {
                if (o instanceof BusAddressSearch) {
                    // 经营地址搜索
                    Flowable.just(o)
                        .compose(bindToLifecycle())
                        .map(o1 -> (BusAddressSearch) o1)
                        .filter(busAddressSearch -> AddressSearchActivity.BUS_ADDRESS_SEARCH
                            .equals(busAddressSearch.getBus()))
                        .subscribe(busAddressSearch -> {
                            gpsAddress = busAddressSearch.lngAndlat;
                            mAddressView.setText(busAddressSearch.city + busAddressSearch.district
                                + busAddressSearch.name);
                            mAddressView
                                .setTextColor(
                                    ContextCompat
                                        .getColor(getContext(), R.color.lists_item_text_left));
                        });
                } else if (o instanceof BusIndustrySelect) {
                    Flowable.just(o)
                        .compose(bindToLifecycle())
                        .map(o1 -> (BusIndustrySelect) o1)
                        .filter(busIndustrySelect -> !TextUtils.isEmpty(busIndustrySelect.getBus()))
                        .subscribe(busIndustrySelect -> {
                            switch (busIndustrySelect.getBus()) {
                                case ContractSelectActivity.BUS_HAS_CONTRACT:
                                    mHasContractView.setText(busIndustrySelect.getTitle());
                                    mHasContractView
                                        .setTextColor(
                                            ContextCompat
                                                .getColor(getContext(),
                                                    R.color.lists_item_text_left));
                                    break;
                                case ContractSelectActivity.BUS_WHY_NO_CONTRACT:
                                    mWhyNoContractView.setText(busIndustrySelect.getTitle());
                                    mWhyNoContractView
                                        .setTextColor(
                                            ContextCompat
                                                .getColor(getContext(),
                                                    R.color.lists_item_text_left));
                                    break;
                                default:
                                    break;
                            }
                        });
                } else if (o instanceof SearchSelectEvent) {
                    Flowable.just(o)
                        .compose(bindToLifecycle())
                        .map(o1 -> (SearchSelectEvent) o1)
                        .filter(selectEvent -> EventConst.INSTANCE.getINDUSTRY_SELECT()
                            .equals(selectEvent.getEvent()))
                        .subscribe(selectEvent -> {
                            mIndustryView.setText(selectEvent.getData().getTitle());
                            mIndustryView
                                .setTextColor(ContextCompat
                                    .getColor(getContext(), R.color.lists_item_text_left));
                            industryId = selectEvent.getData().getId();
                        });
                }
            });
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

}
