package com.cardvlaue.sys.message;

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

/**
 * Created by Administrator on 2016/9/9.
 */
public class MessagesDailog extends DialogFragment {

    private OnClickMessage messageListener;

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

        view.findViewById(R.id.cancel_btn).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            if (messageListener != null) {
                messageListener.message();
            }
        });
        return view;
    }

    public void setMessageListener(OnClickMessage messageListener) {
        this.messageListener = messageListener;
    }

    public interface OnClickMessage {

        void message();
    }
}
