package com.cardvlaue.sys.splash;

import android.net.Uri;
import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;
import com.cardvlaue.sys.data.SplashDataResponse;

interface SplashContract {

    interface View extends RxBaseView<Presenter> {

        /**
         * 设置活动信息
         *
         * @param title 标题
         * @param url 链接
         */
        void setUrlInfo(String title, String url);

        /**
         * 检测到新版本
         *
         * @param dataResponse 版本信息
         */
        void updateNewVersion(SplashDataResponse dataResponse);

        /**
         * 自动跳过
         */
        void autoOver();

        /**
         * 显示启动页图片
         *
         * @param uri 图片路径
         */
        void showSplashImage(Uri uri);
    }

    interface Presenter extends RxBasePresenter {

        /**
         * 开启定位
         */
        void startLoc();

        /**
         * 关闭定位
         */
        void stopLoc();

        /**
         * 激活设备
         */
        void getImei(String id, String channel, String cache);
    }
}
