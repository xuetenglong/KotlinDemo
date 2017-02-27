package com.cardvlaue.sys.apply;

import com.cardvlaue.sys.data.ErrorResponse;
import java.util.List;

/**
 * 更新/创建用户<br/> Created by cardvalue on 2016/4/29.
 */
public class UpdateUserInfoBO extends ErrorResponse {

    private String userId;
    /**
     * 所属行业(一级)
     */
    private String industryGId;
    /**
     * 所属行业(二级)
     */
    private String industryPId;
    /**
     * 所属行业(三级)
     */
    private String industryCId;
    /**
     * 所属省份
     */
    private String provinceId;
    /**
     * 所属城市
     */
    private String cityId;
    /**
     * 拟融资金额
     */
    private String loanAmount;
    /**
     * 聚信立验证状态	0-未验证，1-已验证
     */
    private String isJxlValid;
    /**
     * 征信授权
     */
    private String creditReportStatus;
    /**
     * 优惠券ID<br/> 若使用多个优惠券，则用英文逗号分隔
     */
    private String couponIds;
    /**
     * 营业执照注册号
     */
    private String bizRegisterNo;
    /**
     * 营业地址
     */
    private String businessAddress;
    /**
     * 注册名称
     */
    private String corporateNem;
    /**
     * 经营名称
     */
    private String businessName;
    /**
     * 营业地址经纬度
     */
    private String bizAddrLonlat;
    /**
     * 房东手机号
     */
    private String landlordPhone;
    /**
     * 紧急联系人
     */
    private String emergencyName;
    /**
     * 紧急联系人手机号
     */
    private String emergencyPhone;
    /**
     * 直系亲属类型<br/> 父母：父母，子女：子女
     */
    private String directType;
    /**
     * 直系亲属姓名
     */
    private String directName;
    /**
     * 直系亲属手机号
     */
    private String directPhone;
    /**
     * 放款账户号
     */
    private String secondaryBankDDA;
    /**
     * 放款账户号
     */
    private String secondaryBankName;
    /**
     * 是否有租赁合同<br/> 1：有，0：无
     */
    private String hasLeaseContract;
    /**
     * 无租赁合同原因<br/> 1：自有房产，2：无偿使用，3：合同丢失
     */
    private String noLeaseContractReason;
    /**
     * 业主姓名
     */
    private String proprietorName;
    /**
     * 业主电话
     */
    private String proprietorPhone;
    /**
     * 合同开始日期
     */
    private String leaseContractStartTime;
    /**
     * 合同结束日期
     */
    private String leaseContractEndTime;
    /**
     * 年租金
     */
    private String leaseYearAmt;
    /**
     * 门店数
     */
    private String numLocations;
    /**
     * 年营业额
     */
    private String yearTurnover;
    /**
     * 拟融资期限月数
     */
    private String planFundTerm;
    /**
     * 申请人
     */
    private String ownerName;
    private String proposerName;
    private String blackBox;
    private String mobilePhone;
    private String agreeToLicense;
    private String createdAt;
    private String updatedAt;
    private String openId;
    private String isEmailVerified;
    /**
     * 允许重新提交申请时间
     */
    private String renewTime;
    private String objectId;
    private String applicationId;
    private String isRenewable;
    private String industryGName;
    private String industryPName;
    private String industryCName;
    /**
     * 法人住所地址
     */
    private String ownerAddress;
    private String longitude;
    private String latitude;
    private String planFundTermName;
    private String isBasicCreditValid;
    private String isPosCreditValid;
    private String qrcodeUrl;
    private List<ItemBO> selHasLeaseContract;
    private List<ItemBO> selNoLeaseContractReason;
    /**
     * 红包数量
     */
    private String couponCount;
    /**
     * 邀请数量
     */
    private String inviteCount;
    private List<ApplyItemBO> coupons;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIndustryGId() {
        return industryGId;
    }

    public void setIndustryGId(String industryGId) {
        this.industryGId = industryGId;
    }

    public String getIndustryPId() {
        return industryPId;
    }

    public void setIndustryPId(String industryPId) {
        this.industryPId = industryPId;
    }

    public String getIndustryCId() {
        return industryCId;
    }

    public void setIndustryCId(String industryCId) {
        this.industryCId = industryCId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getIsJxlValid() {
        return isJxlValid;
    }

    public void setIsJxlValid(String isJxlValid) {
        this.isJxlValid = isJxlValid;
    }

    public String getCreditReportStatus() {
        return creditReportStatus;
    }

    public void setCreditReportStatus(String creditReportStatus) {
        this.creditReportStatus = creditReportStatus;
    }

    public String getCouponIds() {
        return couponIds;
    }

    public void setCouponIds(String couponIds) {
        this.couponIds = couponIds;
    }

    public String getBizRegisterNo() {
        return bizRegisterNo;
    }

    public void setBizRegisterNo(String bizRegisterNo) {
        this.bizRegisterNo = bizRegisterNo;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getCorporateNem() {
        return corporateNem;
    }

    public void setCorporateNem(String corporateNem) {
        this.corporateNem = corporateNem;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBizAddrLonlat() {
        return bizAddrLonlat;
    }

    public void setBizAddrLonlat(String bizAddrLonlat) {
        this.bizAddrLonlat = bizAddrLonlat;
    }

    public String getLandlordPhone() {
        return landlordPhone;
    }

    public void setLandlordPhone(String landlordPhone) {
        this.landlordPhone = landlordPhone;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getDirectType() {
        return directType;
    }

    public void setDirectType(String directType) {
        this.directType = directType;
    }

    public String getDirectName() {
        return directName;
    }

    public void setDirectName(String directName) {
        this.directName = directName;
    }

    public String getDirectPhone() {
        return directPhone;
    }

    public void setDirectPhone(String directPhone) {
        this.directPhone = directPhone;
    }

    public String getSecondaryBankDDA() {
        return secondaryBankDDA;
    }

    public void setSecondaryBankDDA(String secondaryBankDDA) {
        this.secondaryBankDDA = secondaryBankDDA;
    }

    public String getSecondaryBankName() {
        return secondaryBankName;
    }

    public void setSecondaryBankName(String secondaryBankName) {
        this.secondaryBankName = secondaryBankName;
    }

    public String getHasLeaseContract() {
        return hasLeaseContract;
    }

    public void setHasLeaseContract(String hasLeaseContract) {
        this.hasLeaseContract = hasLeaseContract;
    }

    public String getNoLeaseContractReason() {
        return noLeaseContractReason;
    }

    public void setNoLeaseContractReason(String noLeaseContractReason) {
        this.noLeaseContractReason = noLeaseContractReason;
    }

    public String getProprietorName() {
        return proprietorName;
    }

    public void setProprietorName(String proprietorName) {
        this.proprietorName = proprietorName;
    }

    public String getProprietorPhone() {
        return proprietorPhone;
    }

    public void setProprietorPhone(String proprietorPhone) {
        this.proprietorPhone = proprietorPhone;
    }

    public String getLeaseContractStartTime() {
        return leaseContractStartTime;
    }

    public void setLeaseContractStartTime(String leaseContractStartTime) {
        this.leaseContractStartTime = leaseContractStartTime;
    }

    public String getLeaseContractEndTime() {
        return leaseContractEndTime;
    }

    public void setLeaseContractEndTime(String leaseContractEndTime) {
        this.leaseContractEndTime = leaseContractEndTime;
    }

    public String getLeaseYearAmt() {
        return leaseYearAmt;
    }

    public void setLeaseYearAmt(String leaseYearAmt) {
        this.leaseYearAmt = leaseYearAmt;
    }

    public String getNumLocations() {
        return numLocations;
    }

    public void setNumLocations(String numLocations) {
        this.numLocations = numLocations;
    }

    public String getYearTurnover() {
        return yearTurnover;
    }

    public void setYearTurnover(String yearTurnover) {
        this.yearTurnover = yearTurnover;
    }

    public String getPlanFundTerm() {
        return planFundTerm;
    }

    public void setPlanFundTerm(String planFundTerm) {
        this.planFundTerm = planFundTerm;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getProposerName() {
        return proposerName;
    }

    public void setProposerName(String proposerName) {
        this.proposerName = proposerName;
    }

    public String getBlackBox() {
        return blackBox;
    }

    public void setBlackBox(String blackBox) {
        this.blackBox = blackBox;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getAgreeToLicense() {
        return agreeToLicense;
    }

    public void setAgreeToLicense(String agreeToLicense) {
        this.agreeToLicense = agreeToLicense;
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

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(String isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public String getRenewTime() {
        return renewTime;
    }

    public void setRenewTime(String renewTime) {
        this.renewTime = renewTime;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getIsRenewable() {
        return isRenewable;
    }

    public void setIsRenewable(String isRenewable) {
        this.isRenewable = isRenewable;
    }

    public String getIndustryGName() {
        return industryGName;
    }

    public void setIndustryGName(String industryGName) {
        this.industryGName = industryGName;
    }

    public String getIndustryPName() {
        return industryPName;
    }

    public void setIndustryPName(String industryPName) {
        this.industryPName = industryPName;
    }

    public String getIndustryCName() {
        return industryCName;
    }

    public void setIndustryCName(String industryCName) {
        this.industryCName = industryCName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPlanFundTermName() {
        return planFundTermName;
    }

    public void setPlanFundTermName(String planFundTermName) {
        this.planFundTermName = planFundTermName;
    }

    public String getIsBasicCreditValid() {
        return isBasicCreditValid;
    }

    public void setIsBasicCreditValid(String isBasicCreditValid) {
        this.isBasicCreditValid = isBasicCreditValid;
    }

    public String getIsPosCreditValid() {
        return isPosCreditValid;
    }

    public void setIsPosCreditValid(String isPosCreditValid) {
        this.isPosCreditValid = isPosCreditValid;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public List<ItemBO> getSelHasLeaseContract() {
        return selHasLeaseContract;
    }

    public void setSelHasLeaseContract(List<ItemBO> selHasLeaseContract) {
        this.selHasLeaseContract = selHasLeaseContract;
    }

    public List<ItemBO> getSelNoLeaseContractReason() {
        return selNoLeaseContractReason;
    }

    public void setSelNoLeaseContractReason(List<ItemBO> selNoLeaseContractReason) {
        this.selNoLeaseContractReason = selNoLeaseContractReason;
    }

    public String getCouponCount() {
        return couponCount;
    }

    public void setCouponCount(String couponCount) {
        this.couponCount = couponCount;
    }

    public String getInviteCount() {
        return inviteCount;
    }

    public void setInviteCount(String inviteCount) {
        this.inviteCount = inviteCount;
    }

    public List<ApplyItemBO> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<ApplyItemBO> coupons) {
        this.coupons = coupons;
    }
}
