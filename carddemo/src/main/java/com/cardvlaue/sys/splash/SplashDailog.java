package com.cardvlaue.sys.splash;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.message.MessagesDailog;

/**
 * Created by Administrator on 2016/10/18.
 */
public class SplashDailog extends DialogFragment {

    private OnClickSplash splashListener;

    private OnClickCancel cancelListener;

    public static MessagesDailog newInstance() {
        MessagesDailog fragmnet = new MessagesDailog();
        Bundle args = new Bundle();
        fragmnet.setArguments(args);
        return fragmnet;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_message, container, false);

        view.findViewById(R.id.cancel_btn).setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.cancel();
            }
        });

        view.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            if (splashListener != null) {
                splashListener.splash();
            }
        });
        return view;
    }

    public void setSplashListener(OnClickSplash splashListener) {
        this.splashListener = splashListener;
    }

    public void setCancelListener(OnClickCancel cancelListener) {
        this.cancelListener = cancelListener;
    }


    public interface OnClickSplash {

        void splash();
    }

    public interface OnClickCancel {

        void cancel();
    }

}
