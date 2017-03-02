package com.cardvlaue.sys.util;

/**
 * Created by Administrator on 2016/8/22.
 */

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;

public class MTextWatcherUtil implements TextWatcher, OnFocusChangeListener {

    protected int id;

    public MTextWatcherUtil() {

    }

    public MTextWatcherUtil(int id) {
        this.id = id;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub

    }

}
