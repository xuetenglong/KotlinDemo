package com.cardvlaue.sys.message;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Administrator on 2016/9/9.
 */
public class MessageFragmentAdapter extends FragmentPagerAdapter {

    private final String[] titles = {"用户消息", "系统消息"};
    /**
     * 用户消息
     */
    private UserInfoFragment userInfoFragment;
    /**
     * 系统消息
     */

    private SystemFragment systemFragment;

    public MessageFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }


    /**
     * @param position
     * @return
     */

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (userInfoFragment == null) {
                    userInfoFragment = new UserInfoFragment();
                }
                return userInfoFragment;
            case 1:
                if (systemFragment == null) {
                    systemFragment = new SystemFragment();
                }
                return systemFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
