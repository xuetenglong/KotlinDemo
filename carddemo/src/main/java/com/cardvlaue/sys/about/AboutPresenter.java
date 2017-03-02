package com.cardvlaue.sys.about;

import android.content.Context;
import android.support.annotation.NonNull;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.util.DeviceUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import java.util.Locale;
import javax.inject.Inject;

final class AboutPresenter implements AboutContract.Presenter {

    @NonNull
    private final TasksDataSource mTasksRepository;
    @NonNull
    private final Context mContext;
    @NonNull
    private final AboutContract.View mAboutView;

    @Inject
    AboutPresenter(@NonNull Context context, @NonNull AboutContract.View aboutView,
        @NonNull TasksRepository tasksRepository) {
        mContext = context;
        mAboutView = aboutView;
        mTasksRepository = tasksRepository;
    }

    @Inject
    void setupListeners() {
        mAboutView.setPresenter(this);
    }

    private String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "i" : "");
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @Override
    public void subscribe() {
        Fresco.getImagePipelineFactory().getMainFileCache().trimToMinimum();
        long cacheSize = Fresco.getImagePipelineFactory().getMainFileCache().getSize();
        mAboutView.setCache(String.format("共有缓存 %s", humanReadableByteCount(cacheSize, false)));
        mAboutView.setVersion("版本号:" + DeviceUtil.getVersionName(mContext));
    }

    @Override
    public void unsubscribe() {
    }

    @Override
    public void clearCache() {
        mAboutView.setCache("共有缓存 0 B");
        Fresco.getImagePipelineFactory().getMainFileCache().trimToMinimum();
        Fresco.getImagePipeline().clearCaches();
        mTasksRepository.saveHomeImageData("");
    }
}
