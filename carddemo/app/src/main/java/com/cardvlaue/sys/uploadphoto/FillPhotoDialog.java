package com.cardvlaue.sys.uploadphoto;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cardvlaue.sys.R;

/**
 * <p>照片  补照片的原因<p/> Created by Administrator on 2016/7/8.
 */
public class FillPhotoDialog extends DialogFragment {

    public static final String TIP_MSG = "TIP_MSG";
    public static final String DIALOG_TITLE = "dialogTitle";
    public static final String DIALOG_CONTENT = "dialogContent";
    private Animator anim1;
    private Animator anim2;
    private Handler mHandler = new Handler();

   /* public static FillPhotoDialog newInstance() {
        FillPhotoDialog fragment = new FillPhotoDialog();
        Bundle args = new Bundle();
        args.putString("TIP_MSG", "");
        fragment.setArguments(args);
        return fragment;
    }*/

    public static FillPhotoDialog newInstance(String title, String content) {
        FillPhotoDialog fragment = new FillPhotoDialog();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, title);
        args.putString(DIALOG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_fill_photo, container, false);
        view.findViewById(R.id.iv_cancel).setOnClickListener(v -> dismiss());
        LinearLayout image = (LinearLayout) view.findViewById(R.id.layout);
       /* PropertyValuesHolder valueHolder_1 = PropertyValuesHolder.ofFloat(
                "scaleX", 1f, 0.93f);
        PropertyValuesHolder valuesHolder_2 = PropertyValuesHolder.ofFloat(
                "scaleY", 1f, 0.93f);
        anim1 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_1,
                valuesHolder_2);
        anim1.setDuration(250);
        anim1.setInterpolator(new LinearInterpolator());*/
        String title = getArguments().getString(DIALOG_TITLE);
        String content = getArguments().getString(DIALOG_CONTENT);
        ((TextView) view.findViewById(R.id.tv_titile)).setText(title + "-补件原因");
        ((TextView) view.findViewById(R.id.tv_content)).setText(content);

        PropertyValuesHolder valueHolder_3 = PropertyValuesHolder.ofFloat(
            "scaleX", 0.1f, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat(
            "scaleY", 0.1f, 1f);
        anim2 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_3,
            valuesHolder_4);
        anim2.setDuration(300);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.start();

     /*   mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                anim2.end();
                anim1.start();
            }
        },300);*/
        return view;
    }
}
