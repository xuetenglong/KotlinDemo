package com.cardvlaue.sys.posadd;

import com.cardvlaue.sys.BasePresenter;
import com.cardvlaue.sys.BaseView;
import java.util.ArrayList;

interface PosAddContract {

    interface View extends BaseView<Presenter> {

        /**
         * 显示问题验证界面
         *
         * @param verifyId 验证编号
         * @param question 验证问题
         * @param data 答案
         */
        void showVerifyDialog(String verifyId, String question, ArrayList<String> data);

        void closeMe();

        void reAddPos();

        void showPosAddingDialog();//正在添加商编

        void dismissPosAddingDialog();

        void showPosGetingDialog();//正在获取流水

        void dismissPosGetingDialog();

        void showPosQuetionDialogDialog();//正在获取验证问题

        void dismissPosQuetionDialogDialog();

        void showVerifyingDialog();

        void dismissVerifyingDialog();

    }

    interface Presenter extends BasePresenter {

        /**
         * 验证商编
         *
         * @param answer 答案
         */
        void verifyMid(String answer);

        void createPos(String mid);

    }

}
