package com.cardvlaue.sys.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.HomeImageItemDO;
import com.cardvlaue.sys.data.MainTabEvent;
import com.cardvlaue.sys.data.TabItemDO;
import com.cardvlaue.sys.main.MainFragment;
import com.cardvlaue.sys.message.MessageActivity;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.RxBus2;
import com.cardvlaue.sys.util.ScreenUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.trello.rxlifecycle2.components.support.RxFragment;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * 首页选项卡
 */
public class HomeFragment extends RxFragment implements HomeContract.View {

    @Inject
    HomePresenter mPresenter;

    @BindView(R.id.vp_home_slideshow)
    MyRxPager mRxPager;

    @BindView(R.id.hsr_home_swipe_refresh)
    SwipeRefreshLayout mRefreshView;

    @BindView(R.id.tv_home_notice_one)
    TextView mNoticeOneView;

    @BindView(R.id.tv_home_notice_two)
    TextView mNoticeTwoView;

    @BindView(R.id.tv_home_notice_three)
    TextView mNoticeThreeView;

    @BindView(R.id.tv_home_notice_temp)
    TextView mNoticeTempView;

    @BindView(R.id.vf_home_notice)
    ViewFlipper mFlipperView;

    /**
     * 费率低
     */
    @BindView(R.id.icon_home_card)
    SimpleDraweeView mCardView;

    /**
     * 额度高
     */
    @BindView(R.id.icon_home_package)
    SimpleDraweeView mPackageView;

    /**
     * 审核快
     */
    @BindView(R.id.icon_home_flash)
    SimpleDraweeView mFlashView;

    /**
     * 操作易
     */
    @BindView(R.id.icon_home_pc)
    SimpleDraweeView mPcView;

    @BindViews({R.id.sdv_home_one, R.id.sdv_home_two, R.id.sdv_home_three, R.id.sdv_home_four})
    List<SimpleDraweeView> mOldSiView;

    /**
     * 立即申请
     */
    @BindView(R.id.sdv_home_apply)
    SimpleDraweeView mApplyView;

    private List<String> applyImg = new ArrayList<>();

    @Override
    public void closeRefresh() {
        if (mRefreshView != null && mRefreshView.isRefreshing()) {
            mRefreshView.setRefreshing(false);
        }
    }

    /**
     * 点击暂无公告
     */
    @OnClick(R.id.tv_home_notice_temp)
    void clickTempNotice() {
        mPresenter.clickTempNotice();
    }

    @Override
    public void showSysMsg(String msg1, String msg2) {
        mFlipperView.removeView(mNoticeTwoView);
        mFlipperView.removeView(mNoticeThreeView);

        if (!TextUtils.isEmpty(msg1)) {
            mNoticeTwoView.setText(msg1);

            mNoticeTempView.setVisibility(View.GONE);
            mFlipperView.setVisibility(View.VISIBLE);
            mFlipperView.addView(mNoticeTwoView);
        }

        if (!TextUtils.isEmpty(msg2)) {
            mNoticeThreeView.setText(msg2);

            mNoticeTempView.setVisibility(View.GONE);
            mFlipperView.setVisibility(View.VISIBLE);
            mFlipperView.addView(mNoticeThreeView);
        }
    }

    @Override
    public void showUserMsg(String msg) {
        mFlipperView.removeView(mNoticeOneView);

        if (!TextUtils.isEmpty(msg)) {
            mNoticeOneView.setText(msg);

            mNoticeTempView.setVisibility(View.GONE);
            mFlipperView.setVisibility(View.VISIBLE);
            mFlipperView.addView(mNoticeOneView);
        }
    }

    @Override
    public void showHomeImage(List<HomeImageItemDO> data) {
        Timber.e("更新首页图片");
        LinkedList<HomeImageItemDO> lunBoImg = new LinkedList<>();
        List<String> newSiImg = new ArrayList<>();
        List<String> oldSiImg = new ArrayList<>();
        // 主页 TAB 数据
        List<TabItemDO> tabItemData = new ArrayList<>();
        applyImg.clear();
        for (HomeImageItemDO resp : data) {
            switch (resp.getType()) {
                //　轮播图
                case 2:
                    lunBoImg.add(resp);
                    break;
                // 四图片
                case 3:
                    newSiImg.add(resp.getImgUrl());
                    break;
                case 4:
                    oldSiImg.add(resp.getImgUrl());
                    break;
                case 5:
                    applyImg.add(resp.getImgUrl());
                    break;
                case 6:
                    tabItemData.add(new TabItemDO(resp.getImgUrl(), resp.getImgDesc()));
                    break;
                default:
                    break;
            }
        }
        RxBus2.Companion.get().send(new MainTabEvent(MainFragment.EVENT_TAB_IMAGES, tabItemData));
        mRxPager.updateData(lunBoImg);
        for (int i = 0; i < newSiImg.size(); i++) {
            switch (i) {
                case 0:
                    mCardView.setImageURI(newSiImg.get(i));
                    break;
                case 1:
                    mPackageView.setImageURI(newSiImg.get(i));
                    break;
                case 2:
                    mFlashView.setImageURI(newSiImg.get(i));
                    break;
                case 3:
                    mPcView.setImageURI(newSiImg.get(i));
                    break;
            }
        }
        try {
            for (int i = 0; i < mOldSiView.size(); i++) {
                mOldSiView.get(i).setImageURI(oldSiImg.get(i));
            }
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }

        Flowable.just(applyImg)
            .compose(bindToLifecycle())
            .filter(s -> s.size() >= 2)
            .subscribe(strings -> {
                String img = strings.get(0);
                if (!img.contains("selected")) {
                    mApplyView.setImageURI(img);
                } else {
                    mApplyView.setImageURI(strings.get(1));
                }
            }, throwable -> {
                Timber.e("MotionEvent.applyImg:%s", throwable.getMessage());
            });

        /*
        if (mStateListDrawable != null) {
            mApplyView.setImageDrawable(mStateListDrawable);
            mApplyView.setBackgroundColor(
                ContextCompat.getColor(getActivity(), android.R.color.transparent));
        } else {
            Flowable.just(applyImg)
                .filter(s -> s.size() == 2)
                .map(strings -> {
                    StateListDrawable drawable = new StateListDrawable();
                    String img = strings.get(0);
                    Drawable selected;
                    Drawable unSelected;
                    if (img.contains("selected")) {
                        selected = loadImageFromNetwork(img);
                        unSelected = loadImageFromNetwork(strings.get(1));
                    } else {
                        selected = loadImageFromNetwork(strings.get(1));
                        unSelected = loadImageFromNetwork(img);
                    }
                    drawable.addState(new int[]{android.R.attr.state_pressed},
                        selected);
                    drawable.addState(new int[]{-android.R.attr.state_pressed},
                        unSelected);
                    return drawable;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(stateListDrawable -> {
                    mStateListDrawable = stateListDrawable;
                    mApplyView.setImageDrawable(mStateListDrawable);
                    mApplyView.setBackgroundColor(
                        ContextCompat.getColor(getActivity(), android.R.color.transparent));
                });
        }
        */
    }

    /*
    private Drawable loadImageFromNetwork(String address) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(address).openStream(), "image.jpg");
        } catch (IOException e) {
            Timber.e("test:::%s", e.getMessage());
        }
        return drawable;
    }
    */

    @OnClick(R.id.sdv_home_apply)
    void clickApply() {
        mPresenter.clickApply();
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        mPresenter = (HomePresenter) presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerHomeComponent.builder()
            .homePresenterModule(new HomePresenterModule(getActivity(), this))
            .tasksRepositoryComponent(
                ((CVApplication) getActivity().getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        mRxPager.init(getActivity());
        // 修改 ViewPager 大小
        LinearLayout.LayoutParams sdvP = (LinearLayout.LayoutParams) mRxPager.getLayoutParams();
        int screenWidth = DeviceUtil.getScreenWidth(getContext());
        Timber.e("screenWidth:%s", screenWidth);
        if (screenWidth > 0) {
            sdvP.height = (int) (screenWidth * 0.67);
        }

        mNoticeTempView.setVisibility(View.VISIBLE);
        mFlipperView.setVisibility(View.GONE);

        mNoticeOneView.setOnClickListener(v -> startActivity(
            new Intent(getContext(), MessageActivity.class).putExtra("type", "0")));
        mNoticeTwoView.setOnClickListener(v -> startActivity(
            new Intent(getContext(), MessageActivity.class).putExtra("type", "1")));
        mNoticeThreeView.setOnClickListener(v -> startActivity(
            new Intent(getContext(), MessageActivity.class).putExtra("type", "1")));
        mNoticeTempView.setOnClickListener(v -> startActivity(
            new Intent(getContext(), MessageActivity.class)));

        // 移除公告
        mFlipperView.removeView(mNoticeOneView);
        mFlipperView.removeView(mNoticeTwoView);
        mFlipperView.removeView(mNoticeThreeView);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 下拉刷新
        mRefreshView.setColorSchemeResources(R.color.app_blue);
        mRefreshView.setOnRefreshListener(() -> {
            Timber.e("setOnRefreshListener");
            /*
            Fresco.getImagePipelineFactory().getMainFileCache().trimToMinimum();
            Fresco.getImagePipeline().clearCaches();
            */
            Fresco.getImagePipeline().clearDiskCaches();
            mPresenter.loadHomeData();
        });

        mApplyView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    Flowable.just(applyImg)
                        .filter(s -> s.size() >= 2)
                        .subscribe(strings -> {
                            String img = strings.get(0);
                            if (!img.contains("selected")) {
                                mApplyView.setImageURI(img);
                            } else {
                                mApplyView.setImageURI(strings.get(1));
                            }
                        }, throwable -> Timber
                            .e("MotionEvent.ACTION_UP:%s", throwable.getMessage()));
                    break;
                case MotionEvent.ACTION_DOWN:
                    Flowable.just(applyImg)
                        .filter(s -> s.size() >= 2)
                        .subscribe(strings -> {
                            String img = strings.get(0);
                            if (img.contains("selected")) {
                                mApplyView.setImageURI(img);
                            } else {
                                mApplyView.setImageURI(strings.get(1));
                            }
                        }, throwable -> Timber
                            .e("MotionEvent.ACTION_DOWN:%s", throwable.getMessage()));
                    break;
            }
            return false;
        });

        Timber.e("subscribesubscribe");
        mPresenter.subscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        ScreenUtil.FlymeSetStatusBarLightMode(getActivity().getWindow(), true);
        ScreenUtil.setStatusBarDarkMode(true, getActivity());

        mRxPager.startInterval();
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.e("onPauseonPause");
        mPresenter.unsubscribe();

        if (mRefreshView != null) {
            mRefreshView.setRefreshing(false);
            mRefreshView.destroyDrawingCache();
            mRefreshView.clearAnimation();
        }

        mRxPager.stopInterval();
    }
}
