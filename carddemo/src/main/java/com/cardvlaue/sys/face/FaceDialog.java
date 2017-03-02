package com.cardvlaue.sys.face;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cardvlaue.sys.R;

public class FaceDialog extends DialogFragment {

    public static final String DIALOG_TITLE = "dialogTitle";
    public static final String DIALOG_CONTENT = "dialogContent";
    public static final String DIALOG_BUTTON = "dialogButton";
    private OnClickOk okListener;

    public static FaceDialog newInstance(String titile, String content) {
        FaceDialog fragment = new FaceDialog();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, titile);
        args.putString(DIALOG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_face, container, false);

        ((TextView) view.findViewById(R.id.tv_tip_title2))
            .setText(getArguments().getString(DIALOG_CONTENT));

        view.findViewById(R.id.iv_cancel).setOnClickListener(view1 -> dismiss());

        view.findViewById(R.id.btn_ok).setOnClickListener(view1 -> {
            if (okListener != null) {
                okListener.ok();
            }
            dismiss();
        });

        LinearLayout image = (LinearLayout) view.findViewById(R.id.layout);
        PropertyValuesHolder valueHolder_3 = PropertyValuesHolder.ofFloat(
            "scaleX", 0.1f, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat(
            "scaleY", 0.1f, 1f);
        Animator anim2 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_3,
            valuesHolder_4);
        anim2.setDuration(300);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.start();

        return view;
    }

    /**
     * 设置确认点击事件
     */
    public void setOnClickOkListener(OnClickOk okListener) {
        this.okListener = okListener;
    }

    public interface OnClickOk {

        void ok();
    }

}
