package com.cardvlaue.sys.apply;

/**
 * 查询申请<br/> Created by cardvalue on 2016/5/11.
 */
class QueryApplyItemBO {

    private String creditId;

    private int setStep;

    private String status;

    private String statusDetail;

    private String appStatus;

    private String leadStatus;

    private String cashadvanceStatus;

    private String cashadvanceId;

    private String createdAt;

    private String updatedAt;

    private String objectId;

    private String corporateName;

    private String amountRequested;

    private String planFundTerm;

    private String applicationId;

    private String merchantId;

    private String isSubmitApplication;

    private String isDocumentLocked;

    private String isRenewable;//1 重新申请  0   失败

    private String isWithdrawConfirm;//0   融资方案页面

    private String secondaryBankAccountType;//对私   对公

    private String isKalaRecognize;//是否人脸识别验证

    private String isValidBankCard;//是否绑卡

    private String isConfirmation;//是否上传确认书

    private String isUploadComplete;//是否要重新上传照片

    public String getIsUploadComplete() {
        return isUploadComplete;
    }

    public void setIsUploadComplete(String isUploadComplete) {
        this.isUploadComplete = isUploadComplete;
    }

    public String getIsConfirmation() {
        return isConfirmation;
    }

    public void setIsConfirmation(String isConfirmation) {
        this.isConfirmation = isConfirmation;
    }

    public String getIsValidBankCard() {
        return isValidBankCard;
    }

    public void setIsValidBankCard(String isValidBankCard) {
        this.isValidBankCard = isValidBankCard;
    }

    public String getIsKalaRecognize() {
        return isKalaRecognize;
    }

    public void setIsKalaRecognize(String isKalaRecognize) {
        this.isKalaRecognize = isKalaRecognize;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public int getSetStep() {
        return setStep;
    }

    public void setSetStep(int setStep) {
        this.setStep = setStep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public String getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(String leadStatus) {
        this.leadStatus = leadStatus;
    }

    public String getCashadvanceStatus() {
        return cashadvanceStatus;
    }

    public void setCashadvanceStatus(String cashadvanceStatus) {
        this.cashadvanceStatus = cashadvanceStatus;
    }

    public String getCashadvanceId() {
        return cashadvanceId;
    }

    public void setCashadvanceId(String cashadvanceId) {
        this.cashadvanceId = cashadvanceId;
    }

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

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getAmountRequested() {
        return amountRequested;
    }

    public void setAmountRequested(String amountRequested) {
        this.amountRequested = amountRequested;
    }

    public String getPlanFundTerm() {
        return planFundTerm;
    }

    public void setPlanFundTerm(String planFundTerm) {
        this.planFundTerm = planFundTerm;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getIsSubmitApplication() {
        return isSubmitApplication;
    }

    public void setIsSubmitApplication(String isSubmitApplication) {
        this.isSubmitApplication = isSubmitApplication;
    }

    public String getIsDocumentLocked() {
        return isDocumentLocked;
    }

    public void setIsDocumentLocked(String isDocumentLocked) {
        this.isDocumentLocked = isDocumentLocked;
    }

    public String getIsRenewable() {
        return isRenewable;
    }

    public void setIsRenewable(String isRenewable) {
        this.isRenewable = isRenewable;
    }

    public String getIsWithdrawConfirm() {
        return isWithdrawConfirm;
    }

    public void setIsWithdrawConfirm(String isWithdrawConfirm) {
        this.isWithdrawConfirm = isWithdrawConfirm;
    }

    public String getSecondaryBankAccountType() {
        return secondaryBankAccountType;
    }

    public void setSecondaryBankAccountType(String secondaryBankAccountType) {
        this.secondaryBankAccountType = secondaryBankAccountType;
    }
}