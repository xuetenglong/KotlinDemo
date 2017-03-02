package com.cardvlaue.sys.cardverify;

import android.content.Context;
import android.support.v4.app.FragmentManager;

/**
 * Created by Administrator on 2016/7/4.
 */
public interface CardPrivatePresenter {

    /**
     * 获取短信验证码
     */
    void getSmsCode(Context context, String phone, String type, FragmentManager manager);


}
