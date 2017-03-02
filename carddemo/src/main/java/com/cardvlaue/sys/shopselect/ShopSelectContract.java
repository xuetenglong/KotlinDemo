package com.cardvlaue.sys.shopselect;

import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;
import com.cardvlaue.sys.data.ShopListsBean;
import java.util.List;

interface ShopSelectContract {

    interface View extends RxBaseView<Presenter> {

        /**
         * 关闭当前界面
         */
        void closeMe();

        /**
         * 更新商铺列表
         *
         * @param data 数据
         */
        void updateLists(List<ShopListsBean> data);

        /**
         * 显示进度对话框
         */
        void showLoadingDialog();

        /**
         * 隐藏进度对话框
         */
        void dismissLoadingDialog();
    }

    interface Presenter extends RxBasePresenter {

        /**
         * 检查店铺是否切换
         *
         * @param shopId 店铺编号
         * @param shopName 店铺名称
         */
        void checkShopChange(String shopId, String shopName);

        /**
         * 店铺切换
         *
         * @param shopId 店铺编号
         * @param shopName 店铺名称
         */
        void setCurrentShop(String shopId, String shopName);
    }

}
