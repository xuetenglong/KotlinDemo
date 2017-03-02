package com.cardvlaue.sys.feedback;

import dagger.Module;
import dagger.Provides;

@Module
public class FeedbackPresenterModule {

    private final FeedbackContract.View mView;

    public FeedbackPresenterModule(FeedbackContract.View view) {
        mView = view;
    }

    @Provides
    FeedbackContract.View provideFeedbackContractView() {
        return mView;
    }
}
