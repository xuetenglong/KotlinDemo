package com.cardvlaue.sys.posadd;

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
 * Created by Administrator on 2016/10/20.
 */

/**
 * <p>提升额度小技巧<p/> Created by Administrator on 2016/7/8.
 */
public class PosAddDialog extends DialogFragment {

    public static final String DIALOG_TITLE = "dialogTitle";
    public static final String DIALOG_CONTENT = "dialogContent";
    public static final String DIALOG_BUTTON = "dialogButton";
    private Animator anim1;
    private Animator anim2;
    private OnClickOk okListener;
    private OnClickCancle cancleListener;

    public static PosAddDialog newInstance() {
        PosAddDialog fragment = new PosAddDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }


    /*

    public static PosAddDialog newInstance(String title, String content, String button) {
        PosAddDialog fragment = new PosAddDialog();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, title);
        args.putString(DIALOG_CONTENT, content);
        args.putString(DIALOG_BUTTON, button);
        fragment.setArguments(args);
        return fragment;
    }
*/


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.dialog_posadd, container, false);
        //取消的按钮
        view.findViewById(R.id.iv_cancel).setOnClickListener(v -> {
            if (cancleListener != null) {
                cancleListener.cancle();
            }
            dismiss();
        });

       /* String title = getArguments().getString(DIALOG_TITLE);
        String content = getArguments().getString(DIALOG_CONTENT);
        String button = getArguments().getString(DIALOG_BUTTON);

        ((Button)view.findViewById(R.id.btn_ok)).setText(button);
        ((TextView)view.findViewById(R.id.tv_titile)).setText(title);
        ((TextView)view.findViewById(R.id.tv_content)).setText(content);*/

        //确认的按钮
        view.findViewById(R.id.btn_ok).setOnClickListener(v -> {
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
        anim2 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_3,
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

    public void setOnClickCancleListener(OnClickCancle cancleListener) {
        this.cancleListener = cancleListener;
    }

    public interface OnClickOk {

        void ok();
    }

    public interface OnClickCancle {

        void cancle();
    }


}
