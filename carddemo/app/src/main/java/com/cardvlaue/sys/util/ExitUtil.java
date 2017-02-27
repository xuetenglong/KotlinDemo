package com.cardvlaue.sys.util;

import android.app.Activity;
import android.os.Process;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by cardvalue on 2016/3/30.
 */
public class ExitUtil {

    private static final String TAG = "ExitUtil";
    private static Deque<Activity> sStack;

    private ExitUtil() {
        throw new UnsupportedOperationException(TAG + "无需实例化");
    }

    public synchronized static void addActivity(Activity a) {
        if (sStack == null) {
            sStack = new ArrayDeque<>();
        }
        sStack.add(a);
    }

    private static void finishAllActivity() {
        for (Activity a : sStack) {
            if (a != null && !a.isFinishing()) {
                a.finish();
            }
        }
        sStack.clear();
    }

    public synchronized static void quit() {
        finishAllActivity();
        Process.killProcess(Process.myPid());
    }
}
