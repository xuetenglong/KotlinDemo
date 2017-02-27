package com.cardvlaue.sys.userdatails;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.view.OnItemClickListener;

public class UserDetailsDialog extends DialogFragment {

    public static final String ARGUMENTS_TYPE = "type";

    public static final String ARGUMENTS_CONTENT = "content";
    @BindView(R.id.tv_tip_msg)
    TextView mContentView;
    private OnItemClickListener mListener;

    public static UserDetailsDialog newInstance(int type, String content) {
        Bundle args = new Bundle();
        args.putInt(ARGUMENTS_TYPE, type);
        args.putString(ARGUMENTS_CONTENT, content);

        UserDetailsDialog f = new UserDetailsDialog();
        f.setArguments(args);

        return f;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_userdetail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContentView.setText(getArguments().getString(ARGUMENTS_CONTENT));
    }

    @OnClick(R.id.btn_user_detail_cancel)
    public void clickCancel() {
        dismiss();
    }

    @OnClick(R.id.btn_user_detail_ok)
    public void clickOk() {
        switch (getArguments().getInt(ARGUMENTS_TYPE)) {
            case 1:
                dismiss();
                if (mListener != null) {
                    mListener.onItemClick(404);
                }
                break;
        }
    }

}
