package com.example.xuetenglong.kotlindemo;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by shu.xinghu on 2015/11/27.
 */
public class Px2Dpi {
    public static float convertDpToPixel(Context context,float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelToDp(Context context,float px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


}
