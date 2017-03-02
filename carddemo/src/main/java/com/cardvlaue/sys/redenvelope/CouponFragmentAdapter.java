package com.cardvlaue.sys.redenvelope;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Administrator on 2016/7/19.
 */


public class CouponFragmentAdapter extends FragmentPagerAdapter {

    private final String[] titles = {"优惠券", "现金券"};
    /**
     * 优惠券Fragment
     */
    private DiscountCouponFrament chatFragment;
    /**
     * 现金券的Fragment
     */
    private CouponFrament foundFragment;

    public CouponFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (chatFragment == null) {
                    chatFragment = new DiscountCouponFrament();
                }
                return chatFragment;
            case 1:
                if (foundFragment == null) {
                    foundFragment = new CouponFrament();
                }
                return foundFragment;

            default:
                return null;
        }
    }


}

