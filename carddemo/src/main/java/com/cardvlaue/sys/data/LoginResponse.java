package com.cardvlaue.sys.data;

/**
 * 用户登录
 */
public class LoginResponse extends ErrorResponse {

    public String createdAt;
    public String updatedAt;
    public String objectId;
    public String accessToken;
    public String applicationId;

    public LoginResponse() {
    }

    public LoginResponse(String objectId, String accessToken) {
        this.objectId = objectId;
        this.accessToken = accessToken;
    }

}
