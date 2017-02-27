package com.cardvlaue.sys.main;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.AbstractWeexActivity;
import com.cardvlaue.sys.util.ActivityUtils;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.RxBus2;
import java.sql.Time;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * <p>主界面<p/> Displays task details screen.
 */
public class MainActivity extends AbstractWeexActivity {

    private static int startCount;

    @Inject
    MainPresenter mMainPresenter;
    private long touchTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
            .findFragmentById(R.id.contentFrame);

        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();

            ActivityUtils.INSTANCE
                .addFragmentToActivity(getSupportFragmentManager(), mainFragment,
                    R.id.contentFrame);
        }

        DaggerMainComponent.builder()
            .mainPresenterModule(new MainPresenterModule(this, mainFragment))
            .tasksRepositoryComponent(
                ((CVApplication) getApplication()).getTasksRepositoryComponent())
            .build().inject(this);

        startCount = 1;
    }

    /**
     * 返回到桌面
     */
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - touchTime >= 2000) {
            Toast.makeText(this, "再按一次关闭", Toast.LENGTH_SHORT).show();
            touchTime = currentTime;
        } else {
            Timber.e("===============返回到桌面====");
            MainActivity.this.finish();
            // RxBus2.Companion.get().send(BaseActivity.Companion.getEVENT_APP_EXIT());
            // RxBus2.Companion.get().send(MainFragment.BUS_APP_EXIT);
           /* ActivityManager activityMgr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(getPackageName());
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);*/
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        startCount = 0;
    }

    public static int isStart() {
        return startCount;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Timber.e("mian=========================首页");
    }
}
