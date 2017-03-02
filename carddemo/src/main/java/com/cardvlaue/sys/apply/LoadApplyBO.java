package com.cardvlaue.sys.apply;

import com.cardvlaue.sys.data.ErrorResponse;
import java.util.List;

/**
 * 申请
 * <p/>
 * Created by cardvalue on 2016/5/9.
 */
public class LoadApplyBO extends ErrorResponse {

    public String userId;
    public String status;
    public String statusDetail;
    public String appStatus;
    public String leadStatus;
    public String cashadvanceStatus;
    public String cashadvanceId;
    public int isDocumentLocked;
    public String creditId;
    public String amountRequested;
    public int isAmountLocked;
    public String isSubmitApplication;
    public String createdAt;
    public String updatedAt;
    public String objectId;
    public List<ApplyItemBO> coupons;
    public String verifyVideoStatus;
    public String creditReportStatus;
    public String isJxlValid;
    public String directDebitBankCode;
    public String directDebitBankName;
    public String directDebitAcctName;
    public String directDebitAcctNo;
    public String directDebitAcctId;
    public String directDebitAcctPhone;
    public String secondaryBankAccountType;
    public String secondaryBankAcctName;
    public String secondaryBankABA;
    public String secondaryBankName;
    public String secondaryBankDDA;
    public String familyInfoCompleted;
    public String identificationCompleted;
    public String storeInfoCompleted;
    public String leaseInfoCompleted;
    public String isEnabled;
    public String isWithdrawConfirm;//0   融资方案页面
    public String isKalaRecognize;//是否卡拉验证
    public String isConfirmation;//是否上传确认书
    public String isUploadComplete;//是否要重新上传照片
    public String baoliContractUrl;
    public String CompanyAddress;//公司的地址
/*

    public String getCompanyAddress() {
        return CompanyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        CompanyAddress = companyAddress;
    }

    public String getBaoliContractUrl() {
        return baoliContractUrl;
    }

    public void setBaoliContractUrl(String baoliContractUrl) {
        this.baoliContractUrl = baoliContractUrl;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<ApplyItemBO> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<ApplyItemBO> coupons) {
        this.coupons = coupons;
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

    public String getIdentificationCompleted() {
        return identificationCompleted;
    }

    public void setIdentificationCompleted(String identificationCompleted) {
        this.identificationCompleted = identificationCompleted;
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

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getIsWithdrawConfirm() {
        return isWithdrawConfirm;
    }

    public void setIsWithdrawConfirm(String isWithdrawConfirm) {
        this.isWithdrawConfirm = isWithdrawConfirm;
    }
*/


}
