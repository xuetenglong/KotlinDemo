package com.cardvlaue.sys.uploadphoto;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import com.cardvlaue.sys.R;

/**
 * 弹出来绑卡的dialog Created by Administrator on 2016/7/16.
 */
public class CardDialog extends DialogFragment {

    private Animator anim1;
    private Animator anim2;
    // private OnClickPhone phoneListener;
    private OnClickCard cardListener;

    public static CardDialog newInstance() {
        CardDialog fragment = new CardDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.dialog_card, container, false);
        //取消
        view.findViewById(R.id.iv_cancel).setOnClickListener(v -> dismiss());


       /* //手机验证
        view.findViewById(R.id.rl_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneListener != null) {
                    phoneListener.phone();
                }
                dismiss();
            }
        });*/

        //征信验证
        view.findViewById(R.id.rl_credit).setOnClickListener(v -> {
            if (cardListener != null) {
                cardListener.card();
            }
            dismiss();
        });

        LinearLayout image = (LinearLayout) view.findViewById(R.id.layout);
       /* PropertyValuesHolder valueHolder_1 = PropertyValuesHolder.ofFloat(
                "scaleX", 1f, 0.93f);
        PropertyValuesHolder valuesHolder_2 = PropertyValuesHolder.ofFloat(
                "scaleY", 1f, 0.93f);
        anim1 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_1,
                valuesHolder_2);
        anim1.setDuration(250);
        anim1.setInterpolator(new LinearInterpolator());*/

        PropertyValuesHolder valueHolder_3 = PropertyValuesHolder.ofFloat(
            "scaleX", 0.1f, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat(
            "scaleY", 0.1f, 1f);
        anim2 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_3,
            valuesHolder_4);
        anim2.setDuration(300);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.start();

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                anim2.end();
                anim1.start();
            }
        },300);*/
        return view;
    }


 /*   *//**
     * 手机验证
     *//*
    public void setPhoneListener(OnClickPhone phoneListener) {
        this.phoneListener = phoneListener;
    }

    public interface OnClickPhone {
        void phone();
    }
*/

    /**
     * 征信验证
     */
    public void setCardListener(OnClickCard cardListener) {
        this.cardListener = cardListener;
    }

    public interface OnClickCard {

        void card();
    }

}