package com.cardvlaue.sys.data;

public class CheckAuthorizeResponse extends ErrorResponse {

    private String hasRegisterAuth;

    public void setHasRegisterAuth(String hasRegisterAuth) {
        this.hasRegisterAuth = hasRegisterAuth;
    }

    public boolean notAuthorize() {
        return "0".equals(hasRegisterAuth);
    }

}
