package com.cardvlaue.sys.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.apply.ApplyFragment;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.MainTabEvent;
import com.cardvlaue.sys.data.TabItemDO;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.home.HomeFragment;
import com.cardvlaue.sys.more.MoreFragment;
import com.cardvlaue.sys.my.MyFragment;
import com.cardvlaue.sys.registerverify.RegisterOkDialog;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.RxBus2;
import com.facebook.drawee.view.SimpleDraweeView;
import com.trello.rxlifecycle2.components.support.RxFragment;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import org.lzh.framework.updatepluginlib.UpdateBuilder;
import timber.log.Timber;

/**
 * Main UI for the task detail screen.
 */
public class MainFragment extends RxFragment implements MainContract.View {
    /**
     * 应用退出
     */
    public static final String BUS_APP_EXIT = "MainFragment_BUS_APP_EXIT";
    /**
     * 获取用户信息数据结束
     */
    public static final String BUS_LOAD_USER_END = "MainFragment_BUS_LOAD_USER_END";

    public static final String BUS_APPLY_CODE = "BUS_APPLY_CODE";
    /**
     * 选项卡图片事件
     */
    public static final String EVENT_TAB_IMAGES = "MainFragment_EVENT_TAB_IMAGES";

    private static final String content = "请先进入系统设置->应用->小企额->权限,开启所有权限后,才能进行后续操作！";
    private static final String dialogButton = "我知道了，马上开启权限";
    private static final String PROG_DIALOG_TAG = "PROG_DIALOG_TAG_tag";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1001;
    /**
     * 引导图片资源
     */
    private static final int[] pics = {R.mipmap.p1, R.mipmap.p2, R.mipmap.p4};

    /**
     * 单个选项卡
     */
    @BindViews({R.id.ll_main_tab_home, R.id.ll_main_tab_apply, R.id.ll_main_tab_my,
        R.id.ll_main_tab_more})
    List<LinearLayout> mTabItems;
    /**
     * 选项卡中文字
     */
    @BindViews({R.id.tv_tab_text_home, R.id.tv_tab_text_apply, R.id.tv_tab_text_my,
        R.id.tv_tab_text_more})
    List<TextView> mTabTexts;
    /**
     * 选项卡中图片
     */
    @BindViews({R.id.iv_tab_icon_home, R.id.iv_tab_icon_apply, R.id.iv_tab_icon_my,
        R.id.iv_tab_icon_more})
    List<SimpleDraweeView> mTabImages;
    /**
     * 显示引导页
     */
    @BindView(R.id.rl_main_start)
    RelativeLayout mStartView;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    /**
     * 小圆点容器
     */
    @BindView(R.id.pointGroup)
    ViewGroup mPointGroup;
    @BindView(R.id.butt)
    Button mNextView;

    private MainContract.Presenter mPresenter;
    private boolean mShowDialog = false;
    private boolean mShowFragment = true;
    /**
     * 引导页视图
     */
    private List<ImageView> mImgViews = new ArrayList<>();
    /**
     * 底部小点的图片
     */
    private ImageView[] mPointViews = new ImageView[pics.length];
    private Animation mEndAnimation;
    /**
     * 默认选项
     */
    private int currentItem = -1;
    private int currentItems = 0;
    /**
     * 选项卡数据
     */
    private List<TabItemDO> tabItemData = new ArrayList<>();
    private HomeFragment homeFragment;
    private ApplyFragment applyFragment;
    private MyFragment myFragment;
    private MoreFragment moreFragment;
    private int[] defaultImage = {R.mipmap.icon_tab_home_default0, R.mipmap.icon_tab_apply_default0,
        R.mipmap.icon_tab_my_default0, R.mipmap.icon_tab_more_default0};
    private int[] selectImage = {R.mipmap.icon_tab_home_selected0,
        R.mipmap.icon_tab_apply_selected0,
        R.mipmap.icon_tab_my_selected0, R.mipmap.icon_tab_more_selected0};

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void setPresenter(@NonNull MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * 点击“立即体验”，隐藏当前视图
     */
    @OnClick(R.id.butt)
    void clickNext() {
        mStartView.startAnimation(mEndAnimation);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEndAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        mEndAnimation.setFillAfter(true);
        mEndAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mStartView.setVisibility(View.GONE);
                mStartView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mPresenter.loadUserInfo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 初始化“首页”TAB
        mShowFragment = true;
        changeTabImage(0);

        // 检查是否是首次安装
        SharedPreferences sp = getActivity().getSharedPreferences("share", Activity.MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("isFirst", true);
        if (isFirst) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirst", false);
            editor.apply();
            showStartPage();
        }
        // 显示“申请”
        String applyStr = getActivity().getIntent().getStringExtra("apply");
        Timber.e("  onViewCreated  getIntent()=====:::%s", applyStr);
        if (!TextUtils.isEmpty(applyStr)) {
            if("UserDetails".equals(applyStr)){
                mShowFragment = true;
                changeTabImage(2);
                currentItems=2;
            }else{
                mShowFragment = true;
                changeTabImage(1);
                currentItems=1;
            }
        }
    }

    public static String TAB_HOME = "homehomehome";

    public static String TAB_APPLY = "homehomeapply";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RxBus2.Companion.get().toObservable()
            .compose(bindToLifecycle()).subscribe(o -> {
            // 收到TAB数据
            Flowable.just(o)
                .compose(bindToLifecycle())
                .filter(o1 -> o1 instanceof MainTabEvent)
                .map(o1 -> (MainTabEvent) o1)
//                .filter(mainTabEvent -> EVENT_TAB_IMAGES.equals(mainTabEvent.getEvent()))
                .subscribe(mainTabEvent -> {
                    if (EVENT_TAB_IMAGES.equals(mainTabEvent.getEvent())) {
                        Timber.i("收到TAB数据");
                        tabItemData.clear();
                        tabItemData.addAll(mainTabEvent.getData());
                        mShowFragment=false;
                        currentItem = -1;
                        changeTabImage(0);
                    }

                    /*else if (TAB_HOME.equals(mainTabEvent)) {
                        currentItem = -1;
                        changeTabImage(0);
                    } else if (TAB_APPLY.equals(mainTabEvent)) {
                        currentItem = 1;
                        changeTabImage(1);
                    }*/
                }, Throwable::printStackTrace);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
                //申请读取通讯录权限
                MainFragment.this
                    .requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            mPresenter.uploadContract();
        }
    }

    /**
     * 点击首页选项
     */
    @OnClick(R.id.ll_main_tab_home)
    void clickTabHome() {
        mShowFragment = true;
        changeTabImage(0);
    }

    /**
     * 点击申请选项
     */
    @OnClick(R.id.ll_main_tab_apply)
    void clickTabApply()
    {
        TasksRepository repository = ((CVApplication) getActivity().getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        LoginResponse loginResponse = repository.getLogin();
        if(!TextUtils.isEmpty(loginResponse.objectId)){
            BusIndustrySelect select = new BusIndustrySelect(
                MainFragment.BUS_APPLY_CODE);
            select.setTypeId("isApply");
            RxBus.getDefaultBus().send(select);
        }
        mShowFragment = true;
        changeTabImage(1);

    }

    /**
     * 点击我的选项
     */
    @OnClick(R.id.ll_main_tab_my)
    void clickTabMy() {
        mShowFragment = true;
        changeTabImage(2);
    }

    /**
     * 点击更多选项
     */
    @OnClick(R.id.ll_main_tab_more)
    void clickTabMore() {
        mShowFragment = true;
        changeTabImage(3);
    }

    /**
     * 切换 TAB
     */
    private void changeTabImage(int index) {
        if (currentItem == index) {
            return;
        }
        currentItem = index;
        Timber.e("===================="+mShowFragment);
        Timber.e("changeTabImage---Text"+tabItemData.size());
        if(mShowFragment){
            showFragment(currentItem);
        }else{
            TasksRepository repository = ((CVApplication) getActivity().getApplication()).getTasksRepositoryComponent()
                .getTasksRepository();
            LoginResponse loginResponse = repository.getLogin();
            if(!TextUtils.isEmpty(loginResponse.accessToken)){
                String defaultColor = tabItemData.get(0).getColor();
                int defaultColorInt = Color.BLACK;
                if (!TextUtils.isEmpty(defaultColor)) {
                    Timber.e("changeTabImage-- defaultColorInt-Text111");
                    defaultColorInt = Color.parseColor(defaultColor);
                }
                String selectColor = tabItemData.get(1).getColor();
                int selectColorInt = ContextCompat.getColor(getContext(), R.color.app_main_color);
                if (!TextUtils.isEmpty(selectColor)) {
                    Timber.e("changeTabImage-- selectColor-Text111");
                    selectColorInt = Color.parseColor(selectColor);
                }
                for (int i = 0; i < mTabTexts.size(); i++) {
                    if (i == currentItems) {
                        Timber.e("currentItem=selectColorInt=="+currentItems+"==i="+i);
                        mTabTexts.get(i).setTextColor(selectColorInt);
                    } else {
                        Timber.e("currentItem==defaultColorInt="+currentItems+"==i="+i);
                        mTabTexts.get(i).setTextColor(defaultColorInt);
                    }
                }
                return;
            }else{
                showFragment(currentItem);
            }

        }

        if (tabItemData.size() <= 0) {
            for (int i = 0; i < mTabImages.size(); i++) {
                mTabImages.get(i).getHierarchy()
                    .setBackgroundImage(ContextCompat.getDrawable(getContext(), defaultImage[i]));
                switch (index) {
                    case 0:
                        mTabImages.get(index).getHierarchy()
                            .setBackgroundImage(
                                ContextCompat.getDrawable(getContext(), selectImage[index]));
                        break;
                    case 1:
                        mTabImages.get(index).getHierarchy()
                            .setBackgroundImage(
                                ContextCompat.getDrawable(getContext(), selectImage[index]));
                        break;
                    case 2:
                        mTabImages.get(index).getHierarchy()
                            .setBackgroundImage(
                                ContextCompat.getDrawable(getContext(), selectImage[index]));
                        break;
                    case 3:
                        mTabImages.get(index).getHierarchy()
                            .setBackgroundImage(
                                ContextCompat.getDrawable(getContext(), selectImage[index]));
                        break;
                    default:
                        break;
                }
            }
            return;
        }

        Timber.e("changeTabImage---Image");
        for (int i = 0; i < mTabImages.size(); i++) {
            mTabImages.get(i).getHierarchy().setBackgroundImage(null);
            mTabImages.get(i).setImageURI(tabItemData.get(i * 2).getUrl());
            switch (index) {
                case 0:
                    mTabImages.get(index).setImageURI(tabItemData.get(index + 1).getUrl());
                    break;
                case 1:
                    mTabImages.get(index).setImageURI(tabItemData.get(index + 2).getUrl());
                    break;
                case 2:
                    mTabImages.get(index).setImageURI(tabItemData.get(index + 3).getUrl());
                    break;
                case 3:
                    mTabImages.get(index).setImageURI(tabItemData.get(index + 4).getUrl());
                    break;
                default:
                    break;
            }
        }

        Timber.e("changeTabImage---Text111");
        String defaultColor = tabItemData.get(0).getColor();
        int defaultColorInt = Color.BLACK;
        if (!TextUtils.isEmpty(defaultColor)) {
            Timber.e("changeTabImage-- defaultColorInt-Text111");
            defaultColorInt = Color.parseColor(defaultColor);
        }
        String selectColor = tabItemData.get(1).getColor();
        int selectColorInt = ContextCompat.getColor(getContext(), R.color.app_main_color);
        if (!TextUtils.isEmpty(selectColor)) {
            Timber.e("changeTabImage-- selectColor-Text111");
            selectColorInt = Color.parseColor(selectColor);
        }

        for (int i = 0; i < mTabTexts.size(); i++) {
            if (i == currentItem) {
                Timber.e("currentItem=selectColorInt=="+currentItem+"==i="+i);
                mTabTexts.get(i).setTextColor(selectColorInt);
            } else {
                Timber.e("currentItem==defaultColorInt="+currentItem+"==i="+i);
                mTabTexts.get(i).setTextColor(defaultColorInt);
            }
        }

        // 检查更新

        UpdateBuilder.create().check(getActivity());

    }

    /**
     * 显示碎片
     *
     * @param i <br> 0 -> 显示“首页”<br> 1 -> 显示“申请”<br> 2 -> 显示“我的”<br> 3 -> 显示“更多”
     */
    private void showFragment(int i) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (moreFragment != null) {
            fragmentTransaction.hide(moreFragment);
        } else {
            moreFragment = new MoreFragment();
            fragmentTransaction.add(R.id.fragment_main_content, moreFragment);
        }
        if (myFragment != null) {
            fragmentTransaction.hide(myFragment);
        } else {
            myFragment = new MyFragment();
            fragmentTransaction.add(R.id.fragment_main_content, myFragment);
        }
        if (applyFragment != null) {
            fragmentTransaction.hide(applyFragment);
        } else {
            applyFragment = new ApplyFragment();
            fragmentTransaction.add(R.id.fragment_main_content, applyFragment);
        }
        if (homeFragment != null) {
            fragmentTransaction.hide(homeFragment);
        } else {
            homeFragment = new HomeFragment();
            fragmentTransaction.add(R.id.fragment_main_content, homeFragment);
        }
        switch (i) {
            case 0:
                fragmentTransaction.show(homeFragment);
                break;
            case 1:
                fragmentTransaction.show(applyFragment);
                break;
            case 2:
                fragmentTransaction.show(myFragment);
                break;
            case 3:
                fragmentTransaction.show(moreFragment);
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }

    /**
     * 显示引导页
     */
    private void showStartPage() {
        // 初始化引导图片列表
        for (int pic : pics) {
            ImageView iv = new ImageView(getActivity());
            iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
            // 防止图片不能填满屏幕
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            ViewTreeObserver viewTreeObserver = iv.getViewTreeObserver();
            viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    iv.getViewTreeObserver().removeOnPreDrawListener(this);
                    int imageViewHeight = iv.getMeasuredHeight();
                    int imageViewWidth = iv.getMeasuredWidth();

                    iv.setImageBitmap(
                        decodeSampledBitmapFromResource(getResources(),
                            pic, imageViewWidth, imageViewHeight));
                    return true;
                }
            });
            mImgViews.add(iv);
        }

        // 初始化圆点
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(14, 14);
            lp.setMargins(10, 0, 10, 0);
            iv.setLayoutParams(lp);
            if (i == 0) {
                iv.setBackgroundResource(R.drawable.white_dot);
            } else {
                iv.setBackgroundResource(R.drawable.dark_dot);
            }

            mPointGroup.addView(iv);
            mPointViews[i] = iv;
        }

        PagerAdapter mAdapter = new ViewPagerAdapter(mImgViews);
        mViewPager.setAdapter(mAdapter);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Timber.e("CurrentPager:%d", position);
                mPointViews[position].setBackgroundResource(R.drawable.white_dot);
                for (int i = 0; i < pics.length; i++) {
                    if (position != i) {
                        mPointViews[i].setBackgroundResource(R.drawable.dark_dot);
                    }
                    if (position == pics.length - 1) {
                        mNextView.setVisibility(View.VISIBLE);
                    } else {
                        mNextView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mStartView.setVisibility(View.VISIBLE);
    }

    private void setScaledImage(ImageView imageView, final int resId) {
        final ImageView iv = imageView;
        ViewTreeObserver viewTreeObserver = iv.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv.getViewTreeObserver().removeOnPreDrawListener(this);
                int imageViewHeight = iv.getMeasuredHeight();
                int imageViewWidth = iv.getMeasuredWidth();
                iv.setImageBitmap(
                    decodeSampledBitmapFromResource(getResources(),
                        resId, imageViewWidth, imageViewHeight));
                return true;
            }
        });
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
        int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds = true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 4;
            final int halfWidth = width / 4;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 4;
            }
        }
        return inSampleSize;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.uploadContract();
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        mShowDialog = true;
                        onResumeFragments();
                    } else {
                        mShowDialog = true;
                        onResumeFragments();
                    }
                }
                break;
            }
        }
    }

    protected void onResumeFragments() {
        if (mShowDialog) {
            mShowDialog = false;
            if (getActivity().getSupportFragmentManager().findFragmentByTag(PROG_DIALOG_TAG)
                == null) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                new Handler().post(() -> {
                    RegisterOkDialog registerOkDialog = RegisterOkDialog
                        .newInstance(content, dialogButton);
                    if (!registerOkDialog.isVisible()) {
                        registerOkDialog.show(manager, PROG_DIALOG_TAG);
                    }
                    registerOkDialog
                        .setOnClickOkListener(() -> startActivity(getAppDetailSettingIntent()));
                });
            }
        }
    }

    /**
     * 获取应用详情页面 Intent
     */
    protected Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent
                .setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent
                .putExtra("com.android.settings.ApplicationPkgName",
                    getActivity().getPackageName());
        }
        return localIntent;
    }

}
