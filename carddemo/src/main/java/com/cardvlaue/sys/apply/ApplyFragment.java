package com.cardvlaue.sys.apply;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.applyinfo.ApplyInfoActivity;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.financeintention.FinanceIntentionActivity;
import com.cardvlaue.sys.financeintention.FinanceIntentionFragment;
import com.cardvlaue.sys.login.LoginActivity;
import com.cardvlaue.sys.main.MainFragment;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ScreenUtil;
import com.cardvlaue.sys.util.ToastUtil;
import com.trello.rxlifecycle2.components.support.RxFragment;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>我的申请<p/> Created by cardvalue on 2016/6/21.
 */
public class ApplyFragment extends RxFragment {

    // 用来记录应用程序的信息
    List<AppsItemInfo> list;
    JSONObject cData;
    private PackageManager pManager;
    private IFinancingRest rest;
    private TasksRepository repository;
    private String objectId, token;
    private ApplyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<QueryApplyItemBO> mlist = new ArrayList<>();
    private View view;
    private ContentLoadingDialog mLoadingDialog;
    private LoginResponse loginResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_apply, container, false);
        View statusBarView = view.findViewById(R.id.view_home_bar);
        ScreenUtil.statusBarHeight(getResources(), statusBarView);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        initView(view);

        pManager = getActivity().getPackageManager();
        List<PackageInfo> appList = DeviceUtil.getAllApps(getActivity());

        list = new ArrayList<AppsItemInfo>();
        cData = new JSONObject();
        for (int i = 0; i < appList.size(); i++) {
            PackageInfo pinfo = appList.get(i);
            AppsItemInfo shareItem = new AppsItemInfo();
            // 设置应用程序名字
            shareItem.setAppName(pManager.getApplicationLabel(pinfo.applicationInfo).toString());
            // 设置应用程序的包名
            shareItem.setAppId(pinfo.applicationInfo.packageName);
            //shareItem.versionCode = pinfo.versionCode;
            // 设置应用程序版本号
            shareItem.setAppVersion(pinfo.versionName);
            list.add(shareItem);

        }
        cData.put("appList", JSON.toJSONString(list));

        Timber.e("list==================" + JSON.toJSONString(list));
        Timber.e("map==================" + cData);
        Timber.e("map==================" + JSON.toJSONString(cData));
        return view;
    }

    public void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_apply);
        //创建一个线性布局管理器,然后设置布局的方向
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayout.VERTICAL);
        //设置布局管理器
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ApplyAdapter(getActivity());
        //  mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        repository = ((CVApplication) getActivity().getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        loginResponse = repository.getLogin();
        if (!TextUtils.isEmpty(loginResponse.accessToken)) {
            view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ll_no_apply).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.ll_no_apply).setVisibility(View.VISIBLE);
            view.findViewById(R.id.scrollView).setVisibility(View.GONE);
        }

        view.findViewById(R.id.btn_apply_commit).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(loginResponse.accessToken)) {
                startActivity(new Intent(getActivity(), FinanceIntentionActivity.class)
                    .putExtra(FinanceIntentionFragment.EXTRA_IS_LOAD_DATA, false));
            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        view.findViewById(R.id.title_default_right).setOnClickListener(v -> {
            if (TextUtils.isEmpty(loginResponse.accessToken)) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                repository.saveApplicationId(null);
                repository.saveSetStep(null);
                //repository.saveLogin(null);//objectId
                repository.saveMerchantId(null);
                repository.saveCreditId(null);
                Timber.e("ApplyFragment==新建申请=="+repository.getApplicationId()+"==getMerchantId==="+repository.getMerchantId()+"==getCreditId=="+repository.getCreditId());
                // startActivity(new Intent(getActivity(), NewAlipayVerifyActivity.class));//MobileVerifyActivity   AlipayVerifyActivity
                startActivity(new Intent(getActivity(), FinanceIntentionActivity.class)
                    .putExtra(FinanceIntentionFragment.EXTRA_IS_LOAD_DATA, false));
            }
        });

        if (!TextUtils.isEmpty(loginResponse.accessToken) && !TextUtils.isEmpty(loginResponse.objectId)) {
            LoginResponse loginResponse = repository.getLogin();
            token = loginResponse.accessToken;
            UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
            objectId = userInfoNewResponse.objectId;
            if(TextUtils.isEmpty(objectId)){
                objectId=loginResponse.objectId;
            }
            mLoadingDialog = ContentLoadingDialog.newInstance("加载中...");
            //mLoadingDialog.setCancelable(false);
            //queryApplyState(getContext());
            if (!mLoadingDialog.isVisible()) {
                mLoadingDialog.show(getFragmentManager(), "ApplyFragment_tag");
            }
        }


        RxBus.getDefaultBus().toObserverable()
            .observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (MainFragment.BUS_APPLY_CODE.equals(busIndustrySelect.getBus())) {
                    if(busIndustrySelect.getTypeId().equals("isApply")){
                        if (!TextUtils.isEmpty(loginResponse.accessToken) && !TextUtils
                            .isEmpty(loginResponse.objectId)) {
                            queryApplyState(getContext());
                        }
                    }
                }
            }
        });
        /*mDisposable = RxBus2.Companion.get().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof BusIndustrySelect) {
                        Timber.e("main：main");
                        BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                        if (ApplyFragment.BUS_APPLY_CODE.equals(busIndustrySelect.getBus())) {
                            if (!TextUtils.isEmpty(loginResponse.accessToken) && busIndustrySelect.getTypeId().equals("Valid")) {
                                LoginResponse loginResponse = repository.getLogin();
                                token = loginResponse.accessToken;
                                UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
                                objectId = userInfoNewResponse.objectId;
                                if (!TextUtils.isEmpty(loginResponse.accessToken) && !TextUtils.isEmpty(loginResponse.objectId)) {
                                    queryApplyState(getContext());
                                }
                            }
                        }
                    }
                });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.e("onResume 1111111```````````````````````````````");
        ScreenUtil.FlymeSetStatusBarLightMode(getActivity().getWindow(), true);
        ScreenUtil.setStatusBarDarkMode(true, getActivity());
        loginResponse = repository.getLogin();
        Timber.e(repository.getUserInfo().objectId + "onResume====" + loginResponse.objectId+"================"+loginResponse.accessToken);
        if (!TextUtils.isEmpty(loginResponse.accessToken) && !TextUtils
            .isEmpty(loginResponse.objectId)) {
            queryApplyState(getContext());
        }
    }

    /**
     * 查询多申请
     */
    public void queryApplyState(Context context) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId", objectId);
        jsonObject.put("isEnabled", "1");//isEnabled申请对象

        loginResponse = repository.getLogin();
        UserInfoNewResponse userInfoNewResponse = repository.getUserInfo();
        Timber.e("查询多申请============"+userInfoNewResponse.objectId+"============"+loginResponse.objectId+"=============="+loginResponse.accessToken);
        //

        objectId=userInfoNewResponse.objectId != null ? userInfoNewResponse.objectId:loginResponse.objectId;

        rest.queryApplyState(objectId, loginResponse.accessToken)//, jsonObject.toString()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(queryApplyBO -> {
                Timber.e("查询多申请:" + JSON.toJSONString(queryApplyBO));
                switch (queryApplyBO.responseSuccess(context)) {
                    case 0:
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismissAllowingStateLoss();
                        }
                        mlist = queryApplyBO.getResults();
                        if (mlist.size() == 0) {
                            view.findViewById(R.id.ll_no_apply).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.scrollView).setVisibility(View.GONE);
                            return;
                        } else {
                            view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.ll_no_apply).setVisibility(View.GONE);
                        }
                        mAdapter.update(mlist);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                            getActivity());
                        linearLayoutManager.setStackFromEnd(true);
                        mRecyclerView.setLayoutManager(linearLayoutManager);

                        //状态的点击事件 (续贷)
                        mAdapter.setOnItemRenewClickListener(position -> {
                            if (position <= mlist.size() - 1) {
                                QueryApplyItemBO queryApplyItemBO = mlist.get(position);
                                queryApplyItemBO.getObjectId();
                                //创建新的申请
                                LoginBO loginBO = new LoginBO();
                                loginBO.setMerchantId(queryApplyItemBO.getMerchantId());
                                CreateApplication(getContext(), loginBO);
                            }
                        });
                        //点击整个item的点击事件
                        mAdapter.setOnItemClickListenter((v, position) -> {
                            QueryApplyItemBO queryApplyItemBO = mlist.get(position);
                            getUserInfo("1", queryApplyItemBO.getMerchantId(), queryApplyItemBO);
                            repository.saveSetStep(queryApplyItemBO.getSetStep() + "");
                            repository.saveLogin(queryApplyItemBO.getMerchantId());//objectId
                            repository.saveMerchantId(queryApplyItemBO.getMerchantId());
                            repository.saveApplicationId(queryApplyItemBO.getApplicationId());
                            repository.saveCreditId(queryApplyItemBO.getCreditId());
                        });
                        break;
                    case -1:
                        ToastUtil.showFailure(getActivity(), queryApplyBO.getError());
                        if (mLoadingDialog != null) {
                            mLoadingDialog.dismissAllowingStateLoss();
                        }
                        break;
                }
            }, throwable -> {
                Timber.e("queryApplyStateEEE:%s", throwable.getMessage());
                if (mLoadingDialog != null && mLoadingDialog.isVisible()) {
                    mLoadingDialog.dismissAllowingStateLoss();
                }
            });
    }

    /**
     * 创建申请   正在获取用户信息    正在获取最新申请
     */
    public void CreateApplication(Context context, LoginBO merchantId) {
        Timber.e("创建申请head" + objectId + "===" + loginResponse.accessToken + "===" + JSON.toJSONString(merchantId));
        //  objectId   merchantId

        Timber.e("============="+merchantId.getMerchantId()+"========="+token+"++++"+merchantId);
        rest.createApply(merchantId.getMerchantId(), loginResponse.accessToken, merchantId)
            .subscribeOn(Schedulers.io())
            .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                {
                    Timber.e(JSON.toJSONString(loginBO) + "创建成功");
                    switch (loginBO.responseSuccess(context)) {
                        case 0:
                            repository.saveSetStep("0");
                            repository.saveApplicationId(null);
                            repository.saveSetStep(null);
                           // repository.saveLogin(null);//objectId
                            repository.saveMerchantId(null);
                            repository.saveApplicationId(null);
                            repository.saveCreditId(null);
                            getUserInfo("0", merchantId.getMerchantId(), null);
                            queryApplyState(getActivity());
                            break;
                        case -1:
                            ToastUtil.showFailure(getActivity(), loginBO.getError());
                            break;
                    }
                }
            }, throwable -> {
                Timber.e("CreateApplicationERR:%s", throwable.getMessage());
            });
    }

    public void getUserInfo(String tag, String objectId, QueryApplyItemBO queryApplyItemBO) {
        repository.getUserInfo(objectId, token)
            .compose(bindToLifecycle())
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(s -> {
                if (TextUtils.isEmpty(s.getError())) {
                    repository.saveUserInfo(s);
                    if ("0".equals(tag)) {
                        startActivity(
                            new Intent(getActivity(), FinanceIntentionActivity.class));
                    } else if ("1".equals(tag)) {
                        if (queryApplyItemBO.getStatus().trim().equals("申请中")) {
                            int step = queryApplyItemBO.getSetStep();
                            if (step != 0) {
                                step = step + 10;
                                ((CVApplication) getContext().getApplicationContext())
                                    .getQueue()
                                    .setPosition(step, getContext());
                            } else {
                                getContext()
                                    .startActivity(new Intent(getContext(),
                                        FinanceIntentionActivity.class));
                            }
                        } else {
                            Intent intent = new Intent(getContext(), ApplyInfoActivity.class);
                            intent.putExtra("applyinfo", "tag");
                            getContext().startActivity(intent);
                        }
                    }
                } else {
                    ToastUtil.showFailure(getActivity(), s.getError());
                }
            }, Throwable::printStackTrace);
    }

    @Override
    public void onStart() {
        super.onStart();
       /* loginResponse = repository.getLogin();
        Timber.e(repository.getUserInfo().objectId + "onStart新申请====" + loginResponse.objectId+"================"+loginResponse.accessToken);
        if (!TextUtils.isEmpty(loginResponse.accessToken) && !TextUtils
            .isEmpty(loginResponse.objectId)) {
            queryApplyState(getContext());
        }*/
    }


    // 自定义一个 AppsItemInfo 类，用来存储应用程序的相关信息
    private class AppsItemInfo {

        private String appId;//com.cardvalue.sys  // 存放应用程序包名
        private String appName; // 存放应用程序名
        private String appVersion;//存放应用的版本号

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }
    }

}
