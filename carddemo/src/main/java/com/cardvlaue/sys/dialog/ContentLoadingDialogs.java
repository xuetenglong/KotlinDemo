package com.cardvlaue.sys.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.cardvlaue.sys.R;

/**
 * 加载中进度框
 */
public class ContentLoadingDialogs extends DialogFragment {

    public static final String LOADING_MESSAGE = "message";

    /**
     * 进度框
     */
    @BindView(R.id.pb_content_loading_anim)
    ContentLoadingProgressBar mProgressBarView;

    /**
     * 提示文本
     */
    @BindView(R.id.tv_content_loading_message)
    TextView mTipView;

    public static ContentLoadingDialogs newInstance(String message) {
        ContentLoadingDialogs fragment = new ContentLoadingDialogs();
        Bundle args = new Bundle();
        args.putString(LOADING_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setDimAmount(0);
        View view = inflater.inflate(R.layout.dialog_web_shows, container, false);
        ButterKnife.bind(this, view);
       /* ShadowViewHelper.bindShadowHelper(
                new ShadowProperty()
                        .setShadowColor(0x77000000)
                        .setShadowRadius(10)
                        .setShadowDx(10)
                        .setShadowDy(10)
                , view.findViewById(R.id.layout));*/
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBarView.show();
        mTipView.setText(getArguments().getString(LOADING_MESSAGE));
    }

}
