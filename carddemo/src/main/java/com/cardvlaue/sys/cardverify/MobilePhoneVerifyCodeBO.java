package com.cardvlaue.sys.cardverify;

import com.cardvlaue.sys.data.ErrorResponse;

/**
 * <p>发送手机验证码<p/> Created by cardvalue on 2016/4/12.
 */
public class MobilePhoneVerifyCodeBO extends ErrorResponse {

    private String mobilePhone;
    /**
     * 图片验证码
     */
    private String imageVerifyCode;
    private String imgUrl;
    private String createdAt;
    private String sessionId;

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getImageVerifyCode() {
        return imageVerifyCode;
    }

    public void setImageVerifyCode(String imageVerifyCode) {
        this.imageVerifyCode = imageVerifyCode;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

