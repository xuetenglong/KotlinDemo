package com.cardvlaue.sys.about;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.R;

public class AboutFragment extends Fragment implements AboutContract.View {

    @BindView(R.id.rl_white_content)
    RelativeLayout mTitleContentView;
    @BindView(R.id.ibtn_white_back)
    ImageButton mBackView;
    @BindView(R.id.tv_white_back)
    TextView mTitleTextView;
    /**
     * 缓存
     */
    @BindView(R.id.tv_about_clear_cache)
    TextView mCacheView;
    /**
     * 版本
     */
    @BindView(R.id.tv_about_version)
    TextView mVersionView;
    private AboutContract.Presenter mPresenter;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @OnClick(R.id.tv_about_clear_cache)
    void clickCache() {
        new AlertDialog.Builder(getContext())
            .setMessage(getString(R.string.about_wipe_cache_hint))
            .setPositiveButton(getString(R.string.about_cache_clear_yes), (dialogInterface, i) -> {
                mPresenter.clearCache();
            })
            .setNegativeButton(getString(R.string.about_cache_clear_no), null)
            .show();
    }

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        getActivity().finish();
    }

    @Override
    public void setPresenter(@NonNull AboutContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleContentView
            .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_main_color));
        mBackView.setImageResource(R.mipmap.icon_back);
        mTitleTextView.setTextColor(Color.WHITE);
        mTitleTextView.setText(R.string.about_name);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void setVersion(String v) {
        mVersionView.setText(v);
    }

    @Override
    public void setCache(String c) {
        mCacheView.setText(c);
    }

}
