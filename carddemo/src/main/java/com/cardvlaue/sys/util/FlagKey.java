package com.cardvlaue.sys.util;

import android.app.Activity;

/**
 * Created by Administrator on 2016/9/23.
 */

public class FlagKey {

    public Class activity;
    public int position;
    public Activity ac;

    public FlagKey(int position) {
        this.position = position;
    }

    public FlagKey(Class activity, int position) {
        this.activity = activity;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        FlagKey fk = (FlagKey) o;
        return fk.position == this.position;
    }
}
