package com.cardvlaue.sys.financeintention;

import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;
import com.cardvlaue.sys.data.UserInfoNewResponse;

interface FinanceIntentionContract {

    interface View extends RxBaseView<Presenter> {

        /**
         * 补全信息对话框
         *
         * @param dialogMsg 提示
         * @param shopId 店铺编号
         */
        void showPromoteAmountDialog(String dialogMsg, String shopId);

        /**
         * 店铺是否为空的状态
         *
         * @param status 状态
         */
        void changeShopStatus(boolean status);

        /**
         * 下一步
         */
        void gotoNext();

        void showLoadingDialog();

        void dismissLoadingDialog();

        /**
         * 初始化界面数据
         *
         * @param s 数据
         */
        void initFaceData(UserInfoNewResponse s);
    }

    interface Presenter extends RxBasePresenter {

        /**
         * 读取本地用户信息数据
         */
        void loadUserData();

        /**
         * 更新用户信息数据
         *
         * @param shopStr 融资店铺
         * @param useStr 融资用途
         * @param moneyAmount 融资金额
         * @param deadline 融资期限
         * @param gift 红包
         */
        void updateUserInfo(String shopStr, String useStr, String moneyAmount, String deadline,
            String gift);
    }

}
