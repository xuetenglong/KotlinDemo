package com.cardvlaue.sys.cardverify;


import com.cardvlaue.sys.data.ErrorResponse;

/**
 * <p>检查手机号是否已注册授权<p/> Created by cardvalue on 2016/4/8.
 */
public class CheckMobilePhoneRegisterAuthBO extends ErrorResponse {

    private String hasRegisterAuth;

    public String getHasRegisterAuth() {
        return hasRegisterAuth;
    }

    public void setHasRegisterAuth(String hasRegisterAuth) {
        this.hasRegisterAuth = hasRegisterAuth;
    }

    /**
     * 是否已验证 <br/> true 已验证
     */
    public boolean isAuth() {
        return "1".equals(hasRegisterAuth);
    }
}
