package com.cardvlaue.sys.about;

import com.cardvlaue.sys.RxBasePresenter;
import com.cardvlaue.sys.RxBaseView;

interface AboutContract {

    interface View extends RxBaseView<Presenter> {

        void setVersion(String v);

        void setCache(String c);
    }

    interface Presenter extends RxBasePresenter {

        /**
         * 清除缓存
         */
        void clearCache();
    }
}
