package com.cardvlaue.sys.posmanagement;

import com.cardvlaue.sys.BasePresenter;
import com.cardvlaue.sys.BaseView;
import com.cardvlaue.sys.data.MidsItemResponse;
import java.util.ArrayList;
import java.util.List;

interface PosManagementContract {

    interface View extends BaseView<Presenter> {

        void showVerifyDialog(String verifyId, String question, ArrayList<String> data);

        void updateLists(List<MidsItemResponse> data);

        void showLoadingDialog(String msg);

        void dismissLoadingDialog();

    }

    interface Presenter extends BasePresenter {

        /**
         * 验证商编问题
         */
        void verifyMids(String answer);

        /**
         * 查询商编验证问题
         *
         * @param verifyId 商编
         */
        void questMids(String verifyId);

        /**
         * 加载商编列表
         */
        void loadPosLists();

    }
}
