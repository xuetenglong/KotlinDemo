package com.cardvlaue.sys.shopadd;

import com.cardvlaue.sys.data.UserInfoNewResponse;

public class ShopAddRequest extends UserInfoNewResponse {

    public String isCreate;

    /**
     * 红包编号
     */
    public String couponIds;

    /**
     * 同盾
     */
    public String blackBox;

}
