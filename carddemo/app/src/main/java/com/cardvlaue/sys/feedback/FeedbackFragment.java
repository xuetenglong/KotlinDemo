package com.cardvlaue.sys.feedback;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cardvlaue.sys.R;

public class FeedbackFragment extends Fragment implements FeedbackContract.View {

    private FeedbackContract.Presenter mPresenter;

    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    @Override
    public void setPresenter(@NonNull FeedbackContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed_back, container, false);
        return view;
    }
}
