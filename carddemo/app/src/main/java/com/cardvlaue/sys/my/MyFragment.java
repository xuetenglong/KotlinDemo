package com.cardvlaue.sys.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.invitat.InvitatActivity;
import com.cardvlaue.sys.login.LoginActivity;
import com.cardvlaue.sys.lookstore.LookStoreActivity;
import com.cardvlaue.sys.main.MainFragment;
import com.cardvlaue.sys.message.MessageActivity;
import com.cardvlaue.sys.message.MessageRest;
import com.cardvlaue.sys.message.Messages;
import com.cardvlaue.sys.qrcode.QRCodeActivity;
import com.cardvlaue.sys.redenvelope.CouponCashActivity;
import com.cardvlaue.sys.userdatails.UserDetailsActivity;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.ScreenUtil;
import io.reactivex.disposables.Disposable;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 我的   loadUserInfo代码注释了
 */
public class MyFragment extends Fragment {

    public static final String BUS_MONEY_CODE = "BUS_MONEY_CODE";

    public static final String BUS_INIT_DATA = "MyFragment_BUS_INIT_DATA";
    @BindViews({R.id.tv_user_name, R.id.tv_user_phone})
    List<TextView> mTextViews;
    private TasksDataSource mTasksRepository;
    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private TextView mTitleTextRightView;
    private TextView mShop;//店铺管理
    private RelativeLayout mMoney;
    private String couponCount, numLocations, inviteCount;
    private String objectId, token;
    private TextView mShopNum, mInviteNum, mCcouponCount, mInviteCount;
    private TextView hongbao;
    private View view;
    private MessageRest messageRest;
    private boolean userMessage = false;//true  未读得消息    已读
    private boolean sysMessage = false;//true   未读得消息    true已读
    private boolean usermessageBoolean = false;
    private boolean sysMessageBoolean = false;

    private View.OnClickListener clickLinstenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LoginResponse loginResponse = mTasksRepository.getLogin();
            token = loginResponse.accessToken;
            if (TextUtils.isEmpty(token)) {
                startActivity(new Intent(getContext(), LoginActivity.class));
                return ;
            }
            switch (view.getId()) {
                case R.id.ll_couponCount:   // 我的红包
                    startActivity(new Intent(getActivity(), CouponCashActivity.class));
                    break;
                case R.id.ll_numLocations:   //我的店铺
                    startActivity(new Intent(getActivity(), LookStoreActivity.class));
                    break;
                case R.id.ll_inviteCount:    //我的邀请
                    startActivity(new Intent(getActivity(), InvitatActivity.class));
                    break;

                case R.id.tv_qrcodeUrl:   // 点击二维码
                    startActivity(new Intent(getActivity(), QRCodeActivity.class));
                    break;
                case R.id.rl_invitation:  // 点击邀请人
                    startActivity(new Intent(getActivity(), InvitatActivity.class));
                    break;
                case R.id.tv_shop: // 店铺管理
                    startActivity(new Intent(getActivity(), LookStoreActivity.class));
                    break;
                case R.id.rl_money://红包
                    startActivity(new Intent(getActivity(), CouponCashActivity.class));
                    break;
                case R.id.title_default_left://消息
                    Intent intentMessage = new Intent(getActivity(), MessageActivity.class);
                    if (userMessage) {//用户（用户未读的时候）
                        intentMessage.putExtra("type", "0");//用户
                    } else if (!userMessage & sysMessage) {//系统   （系统未读的时候,先要保证用户都读了）
                        intentMessage.putExtra("type", "1");//系统
                    } else if ((!userMessage & !sysMessage)
                        && !usermessageBoolean) {//用户   用户和系统都读了的情况,这个时候就要保证用户里不能为空
                        intentMessage.putExtra("type", "0");//用户
                    } else if (usermessageBoolean & sysMessageBoolean) {//用户和系统都为空的时候，显示用户
                        intentMessage.putExtra("type", "0");//用户
                    } else {//系统
                        intentMessage.putExtra("type", "1");//系统
                    }
                    startActivity(intentMessage);
            }
        }
    };
    private Disposable mDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTasksRepository = ((CVApplication) getActivity().getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
        messageRest = HttpConfig.getClient().create(MessageRest.class);

/*
        mMoneySubscriber = new Subscriber<Object>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object o) {
                if (o instanceof BusIndustrySelect) {
                    BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                    if (MyFragment.BUS_MONEY_CODE.equals(busIndustrySelect.getBus())) {
                        amount = busIndustrySelect.getAmount();
                        typeId = busIndustrySelect.getTypeId();
                        Timber.e("选择红包的返回："+amount+"===="+typeId);
                    }
                } else if (LoginPresenter.BUS_USER_INFO.equals(o)) {
                    loadUserInfo();
                }
            }
        };*/

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this, view);
        ScreenUtil.statusBarHeight(getResources(), view.findViewById(R.id.view_home_bar));
        hongbao = (TextView) view.findViewById(R.id.tv_coupon_num);
        mShopNum = (TextView) view.findViewById(R.id.tv_shop_num);
        mInviteNum = (TextView) view.findViewById(R.id.tv_invite_num);
        mCcouponCount = (TextView) view.findViewById(R.id.tv_couponCount);
        mInviteCount = (TextView) view.findViewById(R.id.tv_inviteCount);
        LoginResponse loginResponse = mTasksRepository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;
        UserInfoNewResponse userInfo = mTasksRepository.getUserInfo();
        if (userInfo != null) {
            couponCount = userInfo.couponCount;//红包
            numLocations = userInfo.numShops;//店铺
            inviteCount = userInfo.inviteCount;//邀请数
        }
        getLoadUserInfo();
        mToolbarView = (Toolbar) view.findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) view.findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) view.findViewById(R.id.title_default_middle);
        mTitleTextRightView = (TextView) view.findViewById(R.id.title_default_right);
        mShop = (TextView) view.findViewById(R.id.tv_shop);//店铺管理
        mMoney = (RelativeLayout) view.findViewById(R.id.rl_money);//红包
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbarView);
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_message, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.my_name));
        mTitleTextRightView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_setting, 0);
        view.findViewById(R.id.tv_qrcodeUrl).setOnClickListener(clickLinstenner);
        view.findViewById(R.id.rl_invitation).setOnClickListener(clickLinstenner);
        mShop.setOnClickListener(clickLinstenner);
        mMoney.setOnClickListener(clickLinstenner);
        mBackView.setOnClickListener(clickLinstenner);
        mShopNum.setOnClickListener(clickLinstenner);
        mInviteNum.setOnClickListener(clickLinstenner);
        mCcouponCount.setOnClickListener(clickLinstenner);
        mInviteCount.setOnClickListener(clickLinstenner);
        view.findViewById(R.id.ll_numLocations).setOnClickListener(clickLinstenner);
        view.findViewById(R.id.ll_couponCount).setOnClickListener(clickLinstenner);
        view.findViewById(R.id.ll_inviteCount).setOnClickListener(clickLinstenner);

       /* RxBus.getDefaultBus().toObserverable().compose(bindToLifecycle()).subscribe(o -> {
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (MyFragment.BUS_MONEY_CODE.equals(busIndustrySelect.getBus())) {
                    amount = busIndustrySelect.getAmount();
                    typeId = busIndustrySelect.getTypeId();
                }
            }
        }, Throwable::printStackTrace);*/

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDisposable = RxBus2.Companion.get().toObservable().subscribe(o -> {
            if (MainFragment.BUS_LOAD_USER_END.equals(o)) {
                getLoadUserInfo();
            } else if (BUS_INIT_DATA.equals(o)) {
                Timber.e("退出成功，刷新我的");
                loadUserInfo(null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ScreenUtil.FlymeSetStatusBarLightMode(getActivity().getWindow(), false);
        ScreenUtil.setStatusBarDarkMode(true, getActivity());

        Timber.e("onResume");
        getLoadUserInfo();
        if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(objectId)) {
            Timber.e("刷新消息");
            getUserMessage();//用户消息
            getSystemMessage();//系统消息
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @OnClick({R.id.rl_my_user_info, R.id.title_default_right})
    public void gotoUserDetails() {
        LoginResponse phone = mTasksRepository.getLogin();
        if (TextUtils.isEmpty(phone.accessToken)) {
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            startActivity(new Intent(getContext(), UserDetailsActivity.class));
        }
    }


    /**
     * 网络获取数据
     */
    public void getLoadUserInfo() {
        LoginResponse loginResponse = mTasksRepository.getLogin();
        String idStr = loginResponse.objectId;
        String tokenStr = loginResponse.accessToken;
        Timber.e("loadUserInfo:%s||%s", idStr, tokenStr);
        if (!TextUtils.isEmpty(tokenStr) && !TextUtils.isEmpty(idStr)) {
            mTasksRepository.getUserInfo(idStr, tokenStr)
                //.compose(getContext().bindToLifecycle())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Timber.e("getUserInfo:%s", JSON.toJSONString(s));
                    switch (s.responseSuccess(getContext())) {
                        case 0:
//                            mTasksRepository.saveUserInfo(s);
//                            RxBus2.Companion.get().send(MainFragment.BUS_LOAD_USER_END);
                            Timber.e("getUserInfo网络获取数据=========:%s", JSON.toJSONString(s));
                            loadUserInfo(s);
                            break;
                        default:
                            break;
                    }
                }, throwable -> {
                    Timber.e("getUserInfoError:%s", throwable.getMessage());
                });
        }
    }













    /**
     * 加载用户数据
     */
    private void loadUserInfo(UserInfoNewResponse user) {
        LoginResponse login = mTasksRepository.getLogin();
        if (!TextUtils.isEmpty(login.accessToken)) {
            String mobilePhone = mTasksRepository.getMobilePhone();

            if (TextUtils.isEmpty(mobilePhone)) {
                mTextViews.get(1).setText("--");
            } else {
                mTextViews.get(1).setText(mobilePhone);
            }
            String name = user.ownerName;
            if (TextUtils.isEmpty(name)) {
                mTextViews.get(0).setText("您好，--");
            } else {
                mTextViews.get(0).setText("您好，" + name);
            }

            //mShopNum,mInviteNum,mCcouponCount,mInviteCount;
            if (TextUtils.isEmpty(user.numShops)) {
                mShopNum.setText("0");
            } else {
                mShopNum.setText(user.numShops);
            }

            if (TextUtils.isEmpty(user.inviteCount)) {
                mInviteNum.setText("0");
            } else {
                mInviteNum.setText(user.inviteCount);
            }

            if (TextUtils.isEmpty(user.couponCount)) {
                mCcouponCount.setText(Html.fromHtml("剩余<font color='#359DF5'>" + 0 + "</font>个"));
            } else {
                mCcouponCount.setText(Html.fromHtml(
                    "剩余<font color='#359DF5'>" + user.couponCount
                        + "</font>个"));
            }

            if (TextUtils.isEmpty(user.inviteCount)) {
                mInviteCount.setText(Html.fromHtml("剩余<font color='#359DF5'>" + 0 + "</font>个"));
            } else {
                mInviteCount.setText(Html.fromHtml(
                    "剩余<font color='#359DF5'>" + user.inviteCount
                        + "</font>个"));
            }

            if (TextUtils.isEmpty(user.couponCount)) {
                hongbao.setText("0");
            } else {
                hongbao.setText(user.couponCount);
            }

        } else {
            mTextViews.get(1).setText("--");
            mTextViews.get(0).setText("请登录");
            mShopNum.setText("0");
            hongbao.setText("0");
            mInviteNum.setText("0");
            view.findViewById(R.id.tv_reds).setVisibility(View.GONE);
            mCcouponCount.setText(Html.fromHtml("剩余<font color='#359DF5'>" + 0 + "</font>个"));
            mInviteCount.setText(Html.fromHtml("剩余<font color='#359DF5'>" + 0 + "</font>个"));
        }
    }

    public void getUserMessage() {
        //获取系统消息  1 = 系统消息      0 =  用户消息
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Timber.e("objectId   token===:" + objectId+ "===="+token);
        messageRest.attemgetMessage(objectId, token, jsonObject, 0, 10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                Timber.e("user消息的返回===:" + s + "====");
                if (s.substring(0, 1).equals("{")) {
                    usermessageBoolean = true;
                    Timber.e("用户消息的返回===" + usermessageBoolean);
                    return;
                }
                List<Messages> message = JSON.parseArray(s, Messages.class);
                Timber.e("user消息的返回===555:" + message.size());
                if (message.size() != 0) {
                    for (Messages msg : message) {
                        if (TextUtils.isEmpty(msg.getReadTime())) {//未读
                            userMessage = true;
                            view.findViewById(R.id.tv_reds).setVisibility(View.VISIBLE);
                            Timber.e("用户消息11" + userMessage);
                            return;
                        } else {
                            view.findViewById(R.id.tv_reds).setVisibility(View.GONE);
                        }
                    }
                }
            }, throwable -> Timber.e("getUserMessageEEE:%s", throwable.getMessage()));
    }

    /**
     * 获取系统消息  1 = 系统消息      0 =  用户消息
     */
    public void getSystemMessage() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageRest.attemgetMessage(objectId, token, jsonObject, 0, 10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                if (s.substring(0, 1).equals("{")) {
                    sysMessageBoolean = true;
                    Timber.e("系统消息的返回===" + usermessageBoolean);
                    return;
                }
                List<Messages> message = JSON.parseArray(s, Messages.class);
                if (message.size() != 0) {
                    if (message.size() != 0) {
                        for (Messages msg : message) {
                            if (TextUtils.isEmpty(msg.getReadTime())) {
                                sysMessage = true;
                                view.findViewById(R.id.tv_reds).setVisibility(View.VISIBLE);
                                return;
                            } else {
                                view.findViewById(R.id.tv_reds).setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }, throwable -> Timber.e("getSystemMessageEEE:%s", throwable.getMessage()));
    }
}
