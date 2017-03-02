package com.cardvlaue.sys.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ActivityUtils;
import javax.inject.Inject;

/**
 * 意见反馈
 */
public class FeedbackActivity extends AppCompatActivity {

    @Inject
    FeedbackPresenter mFeedbackPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_blue);

        FeedbackFragment feedbackFragment = (FeedbackFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (feedbackFragment == null) {
            feedbackFragment = FeedbackFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), feedbackFragment,
                    R.id.contentFrame);
        }

        DaggerFeedbackComponent.builder()
            .feedbackPresenterModule(new FeedbackPresenterModule(feedbackFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);
    }
}
