package com.cardvlaue.sys.cardverify;

import com.cardvlaue.sys.data.ErrorResponse;

/**
 * <p>更新申请</p> Created by Administrator on 2016/7/4.
 */
public class UpdateApplyBO extends ErrorResponse {

    private String blackBox;
    private String type;
    private String ipAddress;
    private String isWithdrawConfirm;
    private String status;
    private int isEnabled;
    private int merchantId;
    private String statusDetail;
    private String appStatus;
    private String isBankInfoEditable;
    private String mobilePhoneVerifyCode;
    private String leadStatus;
    private String cashadvanceStatus;
    private String cashadvanceId;
    private int isDocumentLocked;
    private String creditId;
    private String amountRequested;
    private int isAmountLocked;
    private String isSubmitApplication;
    private String createdAt;
    private String updatedAt;
    private String objectId;
    private String verifyVideoStatus;
    private String creditReportStatus;
    private String isJxlValid;
    private String directDebitBankCode;
    private String directDebitBankName;
    private String directDebitAcctName;
    private String directDebitAcctNo;
    private String directDebitAcctId;
    private String directDebitAcctPhone;
    private String secondaryBankAccountType;
    private String secondaryBankAcctName;
    private String secondaryBankABA;
    private String secondaryBankName;
    private String secondaryBankDDA;
    private String familyInfoCompleted;
    private String storeInfoCompleted;
    private String leaseInfoCompleted;
    private int setStep;
    private String isKalaRecognize;
    private String isConfirmation;
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

    public String getIsKalaRecognize() {
        return isKalaRecognize;
    }

    public void setIsKalaRecognize(String isKalaRecognize) {
        this.isKalaRecognize = isKalaRecognize;
    }

    public String getBlackBox() {
        return blackBox;
    }

    public void setBlackBox(String blackBox) {
        this.blackBox = blackBox;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIsWithdrawConfirm() {
        return isWithdrawConfirm;
    }

    public void setIsWithdrawConfirm(String isWithdrawConfirm) {
        this.isWithdrawConfirm = isWithdrawConfirm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(int isEnabled) {
        this.isEnabled = isEnabled;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
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

    public String getIsBankInfoEditable() {
        return isBankInfoEditable;
    }

    public void setIsBankInfoEditable(String isBankInfoEditable) {
        this.isBankInfoEditable = isBankInfoEditable;
    }

    public String getMobilePhoneVerifyCode() {
        return mobilePhoneVerifyCode;
    }

    public void setMobilePhoneVerifyCode(String mobilePhoneVerifyCode) {
        this.mobilePhoneVerifyCode = mobilePhoneVerifyCode;
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

    public int getIsDocumentLocked() {
        return isDocumentLocked;
    }

    public void setIsDocumentLocked(int isDocumentLocked) {
        this.isDocumentLocked = isDocumentLocked;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public String getAmountRequested() {
        return amountRequested;
    }

    public void setAmountRequested(String amountRequested) {
        this.amountRequested = amountRequested;
    }

    public int getIsAmountLocked() {
        return isAmountLocked;
    }

    public void setIsAmountLocked(int isAmountLocked) {
        this.isAmountLocked = isAmountLocked;
    }

    public String getIsSubmitApplication() {
        return isSubmitApplication;
    }

    public void setIsSubmitApplication(String isSubmitApplication) {
        this.isSubmitApplication = isSubmitApplication;
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

    public String getVerifyVideoStatus() {
        return verifyVideoStatus;
    }

    public void setVerifyVideoStatus(String verifyVideoStatus) {
        this.verifyVideoStatus = verifyVideoStatus;
    }

    public String getCreditReportStatus() {
        return creditReportStatus;
    }

    public void setCreditReportStatus(String creditReportStatus) {
        this.creditReportStatus = creditReportStatus;
    }

    public String getIsJxlValid() {
        return isJxlValid;
    }

    public void setIsJxlValid(String isJxlValid) {
        this.isJxlValid = isJxlValid;
    }

    public String getDirectDebitBankCode() {
        return directDebitBankCode;
    }

    public void setDirectDebitBankCode(String directDebitBankCode) {
        this.directDebitBankCode = directDebitBankCode;
    }

    public String getDirectDebitBankName() {
        return directDebitBankName;
    }

    public void setDirectDebitBankName(String directDebitBankName) {
        this.directDebitBankName = directDebitBankName;
    }

    public String getDirectDebitAcctName() {
        return directDebitAcctName;
    }

    public void setDirectDebitAcctName(String directDebitAcctName) {
        this.directDebitAcctName = directDebitAcctName;
    }

    public String getDirectDebitAcctNo() {
        return directDebitAcctNo;
    }

    public void setDirectDebitAcctNo(String directDebitAcctNo) {
        this.directDebitAcctNo = directDebitAcctNo;
    }

    public String getDirectDebitAcctId() {
        return directDebitAcctId;
    }

    public void setDirectDebitAcctId(String directDebitAcctId) {
        this.directDebitAcctId = directDebitAcctId;
    }

    public String getDirectDebitAcctPhone() {
        return directDebitAcctPhone;
    }

    public void setDirectDebitAcctPhone(String directDebitAcctPhone) {
        this.directDebitAcctPhone = directDebitAcctPhone;
    }

    public String getSecondaryBankAccountType() {
        return secondaryBankAccountType;
    }

    public void setSecondaryBankAccountType(String secondaryBankAccountType) {
        this.secondaryBankAccountType = secondaryBankAccountType;
    }

    public String getSecondaryBankAcctName() {
        return secondaryBankAcctName;
    }

    public void setSecondaryBankAcctName(String secondaryBankAcctName) {
        this.secondaryBankAcctName = secondaryBankAcctName;
    }

    public String getSecondaryBankABA() {
        return secondaryBankABA;
    }

    public void setSecondaryBankABA(String secondaryBankABA) {
        this.secondaryBankABA = secondaryBankABA;
    }

    public String getSecondaryBankName() {
        return secondaryBankName;
    }

    public void setSecondaryBankName(String secondaryBankName) {
        this.secondaryBankName = secondaryBankName;
    }

    public String getSecondaryBankDDA() {
        return secondaryBankDDA;
    }

    public void setSecondaryBankDDA(String secondaryBankDDA) {
        this.secondaryBankDDA = secondaryBankDDA;
    }

    public String getFamilyInfoCompleted() {
        return familyInfoCompleted;
    }

    public void setFamilyInfoCompleted(String familyInfoCompleted) {
        this.familyInfoCompleted = familyInfoCompleted;
    }

    public String getStoreInfoCompleted() {
        return storeInfoCompleted;
    }

    public void setStoreInfoCompleted(String storeInfoCompleted) {
        this.storeInfoCompleted = storeInfoCompleted;
    }

    public String getLeaseInfoCompleted() {
        return leaseInfoCompleted;
    }

    public void setLeaseInfoCompleted(String leaseInfoCompleted) {
        this.leaseInfoCompleted = leaseInfoCompleted;
    }

    public int getSetStep() {
        return setStep;
    }

    public void setSetStep(int setStep) {
        this.setStep = setStep;
    }
}
