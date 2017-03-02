package com.cardvlaue.sys.cardverify;

class MerchantsByMobilePhoneBO extends LoginBO {

    private String mobilePhone;
    private String password;
    private String openId;
    private String pushId;
    private String type;
    private String mobilePhoneVerifyCode;
    private String ipAddress;
    private String blackBox;
    private String inviteCode;
    private String accessToken;

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobilePhoneVerifyCode() {
        return mobilePhoneVerifyCode;
    }

    public void setMobilePhoneVerifyCode(String mobilePhoneVerifyCode) {
        this.mobilePhoneVerifyCode = mobilePhoneVerifyCode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBlackBox() {
        return blackBox;
    }

    public void setBlackBox(String blackBox) {
        this.blackBox = blackBox;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
