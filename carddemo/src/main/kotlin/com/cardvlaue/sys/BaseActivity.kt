package com.cardvlaue.sys

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import com.cardvlaue.sys.data.EventConst
import com.cardvlaue.sys.util.PrefUtil
import com.cardvlaue.sys.util.RxBus2
import com.cardvlaue.sys.util.ScreenUtil
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

open class BaseActivity : RxAppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBus2.get().toObservable()
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ s ->
                    Timber.i("RxBus2:%s", s)
                    if (!isFinishing && BaseActivity.EVENT_APP_EXIT == s) {
                        finish()
                    }
                }) { throwable ->
                    Timber.e(throwable.message)
                }
        Timber.i("BaseActivity-onCreate")
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBus2.get().toObservable()
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    s ->
                    Timber.i("收到退出:%s", s.toString())
                    EventConst.APP_EXIT == s
                }
                .subscribe({ finish() }) {
                    throwable ->
                    Timber.e(throwable.message)
                }
        Timber.i("BaseActivity-onCreate")
    }


    /*
    fun onCreate(saveInstanceState: Bundle, index: Int) {
        super.onCreate(saveInstanceState)
        CVApplication.getApplication().addActivity(this)
        CVApplication.getApplication().queue.addActivity(this, index)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
    */

    override fun onResume() {
        super.onResume()
        // 适配魅族小米状态栏
        ScreenUtil.FlymeSetStatusBarLightMode(window, true)
        ScreenUtil.setStatusBarDarkMode(true, this)
    }

    /*
     * 点击返回时的操作
    fun goBack() {
        try {
            CVApplication.getApplication().queue.back(this)
            finish()
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }
    */

    override fun onStop() {
        super.onStop()
        if (!isAppOnForeground) {
            // app 进入后台
            Timber.e("=============记录当前已经进入后台=============")
            // 全局变量 isActive = false 记录当前已经进入后台
            if (PrefUtil.getInfo(applicationContext, "finals").size > 0) {
                CVApplication.getApplication().getEditData()
            }
        }
    }

    /**
     * 程序是否在前台运行

     * @return true：已停止 false：运行中
     */
    val isAppOnForeground: Boolean
        get() {
            val runningAppProcesses = (applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses
            if (runningAppProcesses != null) {
                runningAppProcesses
                        .filter { it.processName == applicationContext.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
                        .forEach { return true }
            }
            return false
        }

    companion object {
        /**
         * 应用退出
         */
        val EVENT_APP_EXIT = "BaseActivity_EVENT_APP_EXIT"
    }

}
