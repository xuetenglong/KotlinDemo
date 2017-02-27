package com.cardvlaue.sys.main;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;

class ViewPagerAdapter extends PagerAdapter {

    /**
     * 视图集合
     */
    private List<ImageView> viewData;

    ViewPagerAdapter(List<ImageView> views) {
        viewData = views;
    }

    @Override
    public int getCount() {
        return viewData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewData.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = viewData.get(position);
        container.addView(itemView);
        return itemView;
    }

}
