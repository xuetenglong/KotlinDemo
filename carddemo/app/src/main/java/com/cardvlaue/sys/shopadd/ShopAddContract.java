package com.cardvlaue.sys.shopadd;

import com.cardvlaue.sys.BasePresenter;
import com.cardvlaue.sys.BaseView;
import com.cardvlaue.sys.data.UserInfoNewResponse;

interface ShopAddContract {

    interface View extends BaseView<Presenter> {

        /**
         * 店铺创建成功
         */
        void createSuccess();

        /**
         * 店铺更新成功
         */
        void updateSuccess();

        void showLoadingDialog();

        void dismissLoadingDialog();

        /**
         * 修改店铺时，初始化数据
         */
        void initFaceData(UserInfoNewResponse response);

    }

    interface Presenter extends BasePresenter {

        /**
         * 提交信息
         *
         * @param id 编号
         * @param info 信息
         */
        void commitShopInfo(String id, ShopAddRequest info);

        /**
         * 获取店铺信息
         *
         * @param objectId 店铺编号
         */
        void obtainShopInfo(String objectId);
    }
}
