package com.cardvlaue.sys.cardverify;

import com.cardvlaue.sys.data.ErrorResponse;

/**
 * <p>用户登录<p/> Created by cardvalue on 2016/4/6.
 */
class LoginBO extends ErrorResponse {

    private String createdAt;
    private String updatedAt;
    private String objectId;
    private String accessToken;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}