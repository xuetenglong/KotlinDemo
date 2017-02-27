package com.cardvlaue.sys.message;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewConfiguration;
import android.widget.TextView;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.redenvelope.PagerSlidingTabStrip;
import java.lang.reflect.Field;

public class MessageActivity extends BaseActivity {

    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private TextView mTitleTextRightView;
    /**
     * PagerSlidingTabStrip的实例
     */
    private PagerSlidingTabStrip tabs;

    /**
     * 获取当前屏幕的密度
     */
    private DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initViews();
        setOverflowShowingAlways();
        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.viewpage);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MessageFragmentAdapter(getSupportFragmentManager()));
        if ("1".equals(getIntent().getStringExtra("type"))) {
            pager.setCurrentItem(1);
        } else {
            pager.setCurrentItem(0);
        }
        tabs.setViewPager(pager);
        setTabsValue();
    }

    private void initViews() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mTitleTextRightView = (TextView) findViewById(R.id.title_default_right);
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_back, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.message_center));
        mBackView.setOnClickListener(v -> MessageActivity.this.finish());
    }

    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#359DF5"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#359DF5"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
