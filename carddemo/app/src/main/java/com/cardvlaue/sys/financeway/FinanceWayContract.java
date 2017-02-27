package com.cardvlaue.sys.financeway;

import com.cardvlaue.sys.BasePresenter;
import com.cardvlaue.sys.BaseView;

interface FinanceWayContract {

    interface View extends BaseView<Presenter> {

        /**
         * 修改是否有额度
         *
         * @param status 是否有额度
         */
        void changeAmountStatus(boolean status);

        /**
         * 检查是否授信
         *
         * @param status 授信状态
         */
        void changeCreditStatus(boolean status);

        /**
         * 修改商编状态
         *
         * @param status 商编状态
         */
        void changePosStatus(boolean status);

        /**
         * 修改支付宝状态
         *
         * @param status 支付宝状态
         */
        void changeAlipayStatus(String status);

        void checkPosAdd(boolean status);

        void gotoNext();

        void showLoadingDialog();

        void dismissLoadingDialog();
    }

    interface Presenter extends BasePresenter {

        /**
         * 保存当前步骤
         */
        void setCurrentStep();
    }
}
