package com.cardvlaue.sys.home;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import java.util.List;

class HomeSlideNewAdapter extends PagerAdapter {

    private List<SimpleDraweeView> imgData = new ArrayList<>();

    @Override
    public int getCount() {
        return imgData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imgData.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(imgData.get(position));
        return imgData.get(position);
    }

    public void updateData(List<SimpleDraweeView> data) {
        imgData.clear();
        imgData.addAll(data);
        notifyDataSetChanged();
    }

}
