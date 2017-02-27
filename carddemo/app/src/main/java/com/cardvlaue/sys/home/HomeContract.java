package com.cardvlaue.sys.home;

import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;
import com.cardvlaue.sys.data.HomeImageItemDO;
import java.util.List;

interface HomeContract {

    interface View extends RxBaseView<Presenter> {

        /**
         * 停止刷新
         */
        void closeRefresh();

        /**
         * 显示系统公告
         *
         * @param msg1 公告
         * @param msg2 公告
         */
        void showSysMsg(String msg1, String msg2);

        /**
         * 显示用户公告
         *
         * @param msg 公告
         */
        void showUserMsg(String msg);

        /**
         * 显示首页图片
         *
         * @param data 图片数据
         */
        void showHomeImage(List<HomeImageItemDO> data);
    }

    interface Presenter extends RxBasePresenter {

        /**
         * 点击公告
         */
        void clickTempNotice();

        /**
         * 查询首页图片
         */
        void loadHomeData();

        /**
         * 点击申请
         */
        void clickApply();
    }

}
