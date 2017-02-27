package com.cardvlaue.sys.UploadImagePager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.utilpic.HackyViewPager;

public class ImagePagerActivity extends BaseActivity {

    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    public static final String EXTRA_DEMO = "image_demo_txt";
    private static final String STATE_POSITION = "STATE_POSITION";
    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;
    private String mDemotxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        String[] urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        String[] txt1 = getIntent().getStringArrayExtra(EXTRA_DEMO);
        mPager = (HackyViewPager) findViewById(R.id.pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls, txt1);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);

        CharSequence text = getString(R.string.viewpager_indicator, 1,
            mPager.getAdapter().getCount());
        indicator.setText(text);

        // 更新下标
        mPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CharSequence text = getString(R.string.viewpager_indicator,
                    position + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
            throw new IllegalStateException(
                "Can not perform this action after onSaveInstanceState");
        }

        mPager.setCurrentItem(pagerPosition);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        String[] fileList, txt;

        ImagePagerAdapter(FragmentManager fm, String[] fileList, String[] txt) {
            super(fm);
            this.fileList = fileList;
            this.txt = txt;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.length;
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList[position];
            String txt1 = txt[position];
            return ImageDetailFragment.newInstance(url, txt1);
        }
    }
}