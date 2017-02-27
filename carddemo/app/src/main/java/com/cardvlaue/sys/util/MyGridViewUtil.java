package com.cardvlaue.sys.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/8/22.
 */
public class MyGridViewUtil extends GridView {

    public MyGridViewUtil(Context context) {
        super(context);
    }


    public MyGridViewUtil(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public MyGridViewUtil(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //重写dispatchTouchEvent方法禁止GridView滑动
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
