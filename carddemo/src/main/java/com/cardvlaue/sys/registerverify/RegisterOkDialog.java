package com.cardvlaue.sys.registerverify;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.util.RxBus2;

public class RegisterOkDialog extends DialogFragment {

    /**
     * 注册成功
     */
    public static final String BUS_REGISTER_OK = "RegisterOkDialog_BUS_REGISTER_OK";

    public static final String DIALOG_CONTENT = "dialogContent";

    public static final String DIALOG_BUTTON = "dialogButton";

    private OnClickOk okListener;

    public static RegisterOkDialog newInstance() {
        return new RegisterOkDialog();
    }

    public static RegisterOkDialog newInstance(String content, String button) {
        RegisterOkDialog fragment = new RegisterOkDialog();
        Bundle args = new Bundle();
        args.putString(DIALOG_CONTENT, content);
        args.putString(DIALOG_BUTTON, button);
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.confirm_btn)
    void clickOk() {
        if (!TextUtils.isEmpty(getArguments().getString(DIALOG_CONTENT))) {
            if (okListener != null) {
                okListener.ok();
            }
            dismiss();
        } else {
            RxBus2.Companion.get().send(BUS_REGISTER_OK);
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_register_ok, container, false);
        ButterKnife.bind(this, view);
        String content = getArguments().getString(DIALOG_CONTENT);
        if (!TextUtils.isEmpty(content)) {
            ((TextView) view.findViewById(R.id.message)).setText(content);
            ((Button) view.findViewById(R.id.confirm_btn))
                .setText(getArguments().getString(DIALOG_BUTTON));
        }
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
