package com.cardvlaue.sys.redenvelope;

/**
 * Created by Administrator on 2016/7/19.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class TransRelativeLayout extends RelativeLayout {

    public TransRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getBackground().setAlpha(50);
    }

}