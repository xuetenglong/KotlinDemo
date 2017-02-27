package com.cardvlaue.sys.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.cardvlaue.sys.data.HomeImageItemDO;
import com.cardvlaue.sys.webshow.WebShowActivity;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

/**
 * Created by Administrator on 2016/10/10.
 */
public class MyRxPager extends ViewPager {

    private Context mContext;
    /**
     * 是否在触摸
     */
    private boolean mIsTouch = false;
    private int mCurrentPage;
    private LinkedList<HomeImageItemDO> mImgs = new LinkedList<>();
    private Disposable mDisposable;
    private MyAdapter myAdapter;

    public MyRxPager(Context context) {
        super(context);
    }

    public MyRxPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context) {
        mContext = context;
        mCurrentPage = 1;
        myAdapter = new MyAdapter();
        setAdapter(myAdapter);
        setCurrentItem(mCurrentPage);
    }

    /**
     * 更新数据
     */
    public void updateData(LinkedList<HomeImageItemDO> imgIds) {
        mImgs.clear();
        mImgs.addAll(imgIds);
        //尾部加原来的头部
        mImgs.add(imgIds.get(0));
        //头部加原来的尾部
        mImgs.addFirst(imgIds.get(imgIds.size() - 1));
        myAdapter.notifyDataSetChanged();
    }

    /**
     * 开启自动轮播
     */
    public void startInterval() {
        Timber.i("开启自动轮播");
        if (mDisposable != null && mDisposable.isDisposed()) {
            return;
        }
        mDisposable = Observable.interval(5, 5, TimeUnit.SECONDS)  // 5s的延迟，5s的循环时间
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(aLong -> {
                // 进行轮播操作
                // 如果正在触摸就不执行自动轮播
                if (!mIsTouch) {
                    Timber.i("轮播");
                    mCurrentPage++;
                    setCurrentItem(mCurrentPage);
                }
            });
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    // 停下的时候
                    case ViewPager.SCROLL_STATE_IDLE:
                        //如果0就改为原来的最后一个
                        if (getCurrentItem() == 0) {
                            setCurrentItem(mImgs.size() - 2, false);
                            //如果是后来的最后一个就改为1
                        } else if (getCurrentItem() == mImgs.size() - 1) {
                            setCurrentItem(1, false);
                        }
                        mCurrentPage = getCurrentItem();
                        mIsTouch = false;
                        break;
                    //用手滑动ViewPager的时候
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        mIsTouch = true;
                        break;
                }
            }
        });
    }

    /**
     * 关闭自动轮播
     */
    public void stopInterval() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            Timber.i("停止轮播");
            mDisposable.dispose();
        }
        clearOnPageChangeListeners();
    }

    private SimpleDraweeView buildImageView(String imgStr, String titleStr, String urlStr) {
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
            .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
            .build();
        SimpleDraweeView draweeView = new SimpleDraweeView(mContext);
        draweeView.setHierarchy(hierarchy);
        if (!TextUtils.isEmpty(imgStr)) {
            try {
                draweeView.setImageURI(Uri.parse(imgStr));
            } catch (Exception e) {
                Timber.e(e.getMessage());
            }
            if (!TextUtils.isEmpty(urlStr)) {
                draweeView.setOnClickListener(
                    v -> mContext.startActivity(new Intent(mContext, WebShowActivity.class)
                        .putExtra(WebShowActivity.EXTRA_TITLE, titleStr)
                        .putExtra(WebShowActivity.EXTRA_URL, urlStr)));
            }
        }
        return draweeView;
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImgs.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SimpleDraweeView view = buildImageView(mImgs.get(position).getImgUrl(),
                mImgs.get(position).getForwardTitle(), mImgs.get(position).getForwardUrl());
            container.addView(view);
            return view;
        }
    }
}