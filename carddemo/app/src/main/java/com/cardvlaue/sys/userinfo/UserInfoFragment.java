package com.cardvlaue.sys.userinfo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.tongdun.android.shell.FMAgent;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.EventConst;
import com.cardvlaue.sys.data.SearchSelectEvent;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.searchselect.SearchSelectActivity;
import com.cardvlaue.sys.shopadd.ShopAddRequest;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.IdCardUtil;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.StringUtils;
import com.cardvlaue.sys.util.ToastUtil;
import com.trello.rxlifecycle2.components.support.RxFragment;
import java.util.List;
import timber.log.Timber;

public class UserInfoFragment extends RxFragment implements UserInfoContract.View {

    public static final int REQUEST_IMMEDIATE_CODE = 10001;

    public static final int REQUEST_EMERGENCE_CODE = 10002;
    /**
     * 标题文本
     */
    @BindView(R.id.tv_white_back)
    TextView mTitleTextView;
    /**
     * 法定代表
     */
    @BindView(R.id.et_user_info_representative)
    EditText mRepresentativeView;
    /**
     * 身份证号
     */
    @BindView(R.id.et_user_info_id_card)
    EditText mIdView;
    @BindViews({R.id.rb_user_info_three, R.id.rb_user_info_six, R.id.rb_user_info_nine})
    List<RadioButton> mRelationshipViews;
    @BindViews({R.id.view_user_info_three, R.id.view_user_info_six, R.id.view_user_info_nine})
    List<View> mTickViews;
    /**
     * 居住地址
     */
    @BindView(R.id.tv_user_info_residence_address)
    TextView mResidenceAddressView;
    /**
     * 详细地址
     */
    @BindView(R.id.et_user_info_detail_address)
    EditText mDetailAddressView;
    /**
     * 直系亲属
     */
    @BindView(R.id.et_user_info_immediate_relatives)
    EditText mNameImmediateRelativesView;
    /**
     * 亲属手机
     */
    @BindView(R.id.et_user_info_relatives_mobile)
    EditText mPhoneImmediateRelativesView;
    /**
     * 紧急联络
     */
    @BindView(R.id.et_user_info_emergence_interconnection)
    EditText mNameEmergenceInterconnectionView;
    /**
     * 联络手机
     */
    @BindView(R.id.et_user_info_interconnection_mobile)
    EditText mPhoneEmergenceInterconnectionView;
    /**
     * 居住地址提示文本
     */
    @BindString(R.string.user_info_residence_address_hint)
    String bResidenceAddress;
    private UserInfoContract.Presenter mPresenter;
    private String qinType, provinceId, cityId, countyId;

    private ContentLoadingDialog mLoadingDialog;

    private boolean isFirstLoadData;

    public static UserInfoFragment newInstance() {
        return new UserInfoFragment();
    }

    @OnClick(R.id.btn_user_info_commit)
    void clickCommit() {
        Timber.e("`````````" + StringUtils.INSTANCE
            .notEmptyPhone(mPhoneImmediateRelativesView.getText().toString()
                .trim()));

        ShopAddRequest userInfo = new ShopAddRequest();
        String representativeStr = mRepresentativeView.getText().toString();
        if (TextUtils.isEmpty(representativeStr)) {
            ToastUtil.showFailure(getContext(), "请输入法定代表人姓名");
            return;
        } else {
            userInfo.ownerName = representativeStr;
        }
        String ssnStr = mIdView.getText().toString();
        if (TextUtils.isEmpty(ssnStr)) {
            ToastUtil.showFailure(getContext(), "请输入身份证号");
            return;
        } else {
            IdCardUtil idCardUtil = new IdCardUtil();
            String resultSSN = idCardUtil.IDCardValidate(ssnStr);
            if (!TextUtils.isEmpty(resultSSN)) {
                ToastUtil.showFailure(getContext(), "非法身份证号");
                return;
            }
            userInfo.ownerSSN = ssnStr;
        }
        String residenceAddressStr = mResidenceAddressView.getText().toString();
        if (TextUtils.isEmpty(residenceAddressStr) || bResidenceAddress
            .equals(residenceAddressStr)) {
            ToastUtil.showFailure(getContext(), "请选择居住地址");
            return;
        } else {
            userInfo.provinceId = provinceId;
            userInfo.cityId = cityId;
            userInfo.countyId = countyId;
        }
        String detailAddressStr = mDetailAddressView.getText().toString();
        if (TextUtils.isEmpty(detailAddressStr)) {
            ToastUtil.showFailure(getContext(), "请输入详细地址");
            return;
        } else {
            userInfo.ownerAddress = detailAddressStr;
        }
        String nameImmediateRelativesStr = mNameImmediateRelativesView.getText().toString();
        if (TextUtils.isEmpty(nameImmediateRelativesStr)) {
            ToastUtil.showFailure(getContext(), "请输入直系亲属姓名");
            return;
        } else {
            userInfo.directName = nameImmediateRelativesStr;
        }
        String phoneImmediateRelativesStr = StringUtils.INSTANCE
            .notEmptyPhone(mPhoneImmediateRelativesView.getText().toString()
                .trim());
        if (TextUtils.isEmpty(phoneImmediateRelativesStr)) {
            ToastUtil.showFailure(getContext(), "请输入亲属手机");
            return;
        } else if (!CheckUtil.isMobilePhone(phoneImmediateRelativesStr) || mPresenter
            .checkPhoneIsMe(phoneImmediateRelativesStr)) {
            ToastUtil.showFailure(getContext(), "非法亲属手机");
            return;
        } else {
            userInfo.directPhone = phoneImmediateRelativesStr;
        }
        if (TextUtils.isEmpty(qinType)) {
            ToastUtil.showFailure(getContext(), "请选择亲属类型");
            return;
        } else {
            userInfo.directType = qinType;
        }
        String nameEmergenceInterconnectionStr = mNameEmergenceInterconnectionView.getText()
            .toString();
        if (TextUtils.isEmpty(nameEmergenceInterconnectionStr)) {
            ToastUtil.showFailure(getContext(), "请输入紧急联络姓名");
            return;
        } else {
            userInfo.emergencyName = nameEmergenceInterconnectionStr;
        }
        String phoneEmergenceInterconnectionStr = StringUtils.INSTANCE.notEmptyPhone(mPhoneEmergenceInterconnectionView.getText()
            .toString().trim());
        if (TextUtils.isEmpty(phoneEmergenceInterconnectionStr)) {
            ToastUtil.showFailure(getContext(), "请输入联络手机");
            return;
        } else if (!CheckUtil.isMobilePhone(phoneEmergenceInterconnectionStr) || mPresenter
            .checkPhoneIsMe(phoneEmergenceInterconnectionStr)) {
            ToastUtil.showFailure(getContext(), "非法联络手机");
            return;
        } else {
            userInfo.emergencyPhone = phoneEmergenceInterconnectionStr;
        }
        userInfo.isCreate = "0";
        userInfo.blackBox = FMAgent.onEvent(getContext());
        mPresenter.updateInfo(userInfo);
    }

    @OnClick(R.id.ib_user_info_immediate_relatives_add)
    void clickImmediateRelatives() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMMEDIATE_CODE);
        } else {
            ToastUtil.showFailure(getContext(), "不支持的操作");
        }
    }

    @OnClick(R.id.ib_user_info_emergence_interconnection_add)
    void clickEmergenceInterconnection() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_EMERGENCE_CODE);
        } else {
            ToastUtil.showFailure(getContext(), "不支持的操作");
        }
    }

    @OnClick(R.id.tv_user_info_residence_address)
    void clickLive() {
        startActivity(new Intent(getContext(), SearchSelectActivity.class)
            .putExtra(SearchSelectActivity.Companion.getTYPE_FLAG(), 2));
    }

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        try {
            ((CVApplication) getActivity().getApplication()).getQueue().back(getActivity());
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initFaceData(UserInfoNewResponse s) {
        mRepresentativeView.setText(s.ownerName);
        mIdView.setText(s.ownerSSN);
        String provinceNameStr = s.provinceName;
        String cityNameStr = s.cityName;
        String countyNameStr = s.countyName;
        if (!TextUtils.isEmpty(provinceNameStr) && !TextUtils.isEmpty(cityNameStr) && !TextUtils
            .isEmpty(s.countyName)) {
            mResidenceAddressView
                .setText(provinceNameStr + "," + cityNameStr + "," + countyNameStr);
            mResidenceAddressView
                .setTextColor(ContextCompat.getColor(getContext(), R.color.lists_item_text_left));
            provinceId = s.provinceId;
            cityId = s.cityId;
            countyId = s.countyId;
        }
        mDetailAddressView.setText(s.ownerAddress);
        mNameImmediateRelativesView.setText(s.directName);
        mPhoneImmediateRelativesView.setText(s.directPhone);
        mNameEmergenceInterconnectionView.setText(s.emergencyName);
        mPhoneEmergenceInterconnectionView.setText(s.emergencyPhone);
        qinType = s.directType;
        switch (qinType) {
            case "父母":
                mRelationshipViews.get(0).setChecked(true);
                break;
            case "子女":
                mRelationshipViews.get(1).setChecked(true);
                break;
            case "配偶":
                mRelationshipViews.get(2).setChecked(true);
                break;
        }
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoadingDialog = ContentLoadingDialog.newInstance("提交中...");
        mLoadingDialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleTextView.setText(getString(R.string.user_info_name));

        mRelationshipViews.get(0).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mTickViews.get(0).setVisibility(View.VISIBLE);
                mTickViews.get(1).setVisibility(View.INVISIBLE);
                mTickViews.get(2).setVisibility(View.INVISIBLE);

                qinType = getString(R.string.user_info_relatives_type_parents);
            }
        });

        mRelationshipViews.get(1).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mTickViews.get(0).setVisibility(View.INVISIBLE);
                mTickViews.get(1).setVisibility(View.VISIBLE);
                mTickViews.get(2).setVisibility(View.INVISIBLE);

                qinType = getString(R.string.user_info_relatives_type_children);
            }
        });

        mRelationshipViews.get(2).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mTickViews.get(0).setVisibility(View.INVISIBLE);
                mTickViews.get(1).setVisibility(View.INVISIBLE);
                mTickViews.get(2).setVisibility(View.VISIBLE);

                qinType = getString(R.string.user_info_relatives_type_spouse);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isFirstLoadData) {
            isFirstLoadData = true;
            mPresenter.loadUserData();
        }

        // 居住地址的接收
        RxBus2.Companion.get().toObservable()
            .compose(bindToLifecycle())
            .filter(o -> o instanceof SearchSelectEvent)
            .map(o -> (SearchSelectEvent) o)
            .filter(busIndustrySelect -> EventConst.INSTANCE.getADDRESS_SELECT()
                .equals(busIndustrySelect.getEvent()))
            .subscribe(busIndustrySelect -> {
                String[] addressIds = busIndustrySelect.getData().getId().split(",");
                provinceId = addressIds[0];
                cityId = addressIds[1];
                countyId = addressIds[2];
                mResidenceAddressView.setText(busIndustrySelect.getData().getTitle());
                mResidenceAddressView
                    .setTextColor(
                        ContextCompat
                            .getColor(getContext(), R.color.lists_item_text_left));
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Timber.e("onActivityResult:%d", requestCode);
        if (data != null) {
            switch (requestCode) {
                case REQUEST_IMMEDIATE_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        queryContact(data, REQUEST_IMMEDIATE_CODE);
                    } else {
                        ToastUtil.showFailure(getActivity(), CONTACT_EMPTY);
                    }
                    break;
                case REQUEST_EMERGENCE_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        queryContact(data, REQUEST_EMERGENCE_CODE);
                    } else {
                        ToastUtil.showFailure(getActivity(), CONTACT_EMPTY);
                    }
                    break;
            }
        }
    }

    private static final String CONTACT_EMPTY = "联系人选择失败，检查是否拒绝联系人权限";

    /**
     * 查询联系人
     *
     * @param data 意图
     */
    private void queryContact(Intent data, int code) {
        Timber.e("queryContact:%s", JSON.toJSONString(data));

        Cursor cursor = getActivity().getContentResolver()
            .query(data.getData(), null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String contactId = cursor
                    .getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor
                    .getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String hasPhoneNumber = cursor
                    .getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (code == REQUEST_IMMEDIATE_CODE) {
                    mNameImmediateRelativesView.setText(name);
                } else if (code == REQUEST_EMERGENCE_CODE) {
                    mNameEmergenceInterconnectionView.setText(name);
                }

                if ("1".equalsIgnoreCase(hasPhoneNumber)) {
                    hasPhoneNumber = "true";
                } else {
                    hasPhoneNumber = "false";
                }

                if (Boolean.parseBoolean(hasPhoneNumber)) {
                    Cursor phoneCursor = getActivity().getContentResolver().query(ContactsContract
                        .CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract
                        .CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                    if (phoneCursor != null) {
                        if (phoneCursor.moveToFirst()) {
                            String number = phoneCursor.getString(phoneCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(number)) {
                                ToastUtil.showFailure(getActivity(), CONTACT_EMPTY);
                            }
                            if (code == REQUEST_IMMEDIATE_CODE) {
                                mPhoneImmediateRelativesView.setText(number);
                            } else if (code == REQUEST_EMERGENCE_CODE) {
                                mPhoneEmergenceInterconnectionView.setText(number);
                            }
                        }
                        phoneCursor.close();
                    }
                }
            }
            cursor.close();
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

    @Override
    public void gotoNext() {
        try {
            ((CVApplication) getActivity().getApplication()).getQueue().next(getActivity(), 20);
            getActivity().finish();
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
    }

}
