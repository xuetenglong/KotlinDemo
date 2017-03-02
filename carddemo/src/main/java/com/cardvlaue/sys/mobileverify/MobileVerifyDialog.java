package com.cardvlaue.sys.mobileverify;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.cardvlaue.sys.R;

public class MobileVerifyDialog extends DialogFragment {

    public static final String TIP_TITLE = "TIP_TITLE";
    public static final String TIP_MSG = "TIP_MSG";
    public static final String TIP_CANCEL = "TIP_CANCEL";
    public static final String TIP_OK = "TIP_OK";

    public static MobileVerifyDialog newInstance() {
        MobileVerifyDialog fragment = new MobileVerifyDialog();
        Bundle args = new Bundle();
        args.putString(TIP_MSG, "");
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_mobileverify, container, false);
        view.findViewById(R.id.iv_cancel).setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams winLayoutParams = window.getAttributes();
        winLayoutParams.alpha = 0.9f;
        window.setAttributes(winLayoutParams);
    }
}