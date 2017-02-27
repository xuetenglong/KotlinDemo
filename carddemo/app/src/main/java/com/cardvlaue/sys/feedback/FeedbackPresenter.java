package com.cardvlaue.sys.feedback;

import android.support.annotation.NonNull;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import javax.inject.Inject;

final class FeedbackPresenter implements FeedbackContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private FeedbackContract.View mFeedbackView;

    @Inject
    FeedbackPresenter(@NonNull TasksRepository tasksRepository,
        @NonNull FeedbackContract.View feedbackView) {
        mTasksRepository = tasksRepository;
        mFeedbackView = feedbackView;
    }

    @Inject
    void setupListeners() {
        mFeedbackView.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
