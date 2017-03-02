package com.cardvlaue.sys.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cardvlaue.sys.R;


public class ToastUtil {

    /**
     * 自定义吐司
     *
     * @param msg 内容
     * @param resId 图标
     * @param duration 显示时长
     */
    public static Toast showCustomImage(Context context, @NonNull String msg, int resId,
        int duration) {
        if (context == null) {
            return null;
        }
        if (resId == 0) {
            return Toast.makeText(context, msg, duration);
        }

        @SuppressLint("InflateParams") View view = LayoutInflater.from(context)
            .inflate(R.layout.toast_tip, null);
        ImageView status = (ImageView) view.findViewById(R.id.iv_toast_status);
        status.setImageResource(resId);
        TextView text = (TextView) view.findViewById(R.id.tv_toast_text);
        text.setText(TextUtils.isEmpty(msg) ? "" : msg);

        LinearLayout image = (LinearLayout) view.findViewById(R.id.layout);
        PropertyValuesHolder valueHolder_1 = PropertyValuesHolder.ofFloat(
            "scaleX", 1f, 0.93f);
        PropertyValuesHolder valuesHolder_2 = PropertyValuesHolder.ofFloat(
            "scaleY", 1f, 0.93f);
        Animator anim1 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_1,
            valuesHolder_2);
        anim1.setDuration(200);
        anim1.setInterpolator(new LinearInterpolator());

        PropertyValuesHolder valueHolder_3 = PropertyValuesHolder.ofFloat(
            "scaleX", 0.9f, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat(
            "scaleY", 0.9f, 1f);
        Animator anim2 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_3,
            valuesHolder_4);
        anim2.setDuration(300);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.start();

        new Handler().postDelayed(() -> {
            anim2.end();
            anim1.start();
        }, 300);

        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        return toast;
    }

    /**
     * 成功
     */
    public static void showSuccess(Context context, @NonNull String msg) {
        Toast toast = showCustomImage(context, msg, R.mipmap.icon_toast_success,
            Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 失败
     */
    public static void showFailure(Context context, @NonNull String msg) {
        Toast toast = showFailure(context, msg, false);
        toast.show();
    }

    /**
     * 失败可取消
     */
    public static Toast showFailure(Context context, @NonNull String msg, boolean cancel) {
        return showCustomImage(context, msg, R.mipmap.icon_toast_fail, Toast.LENGTH_LONG);
    }

}
