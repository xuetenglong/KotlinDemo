package com.cardvlaue.sys.data;

public class UserInfoNewResponse extends ErrorResponse {

    /**
     * 经营详细地址
     */
    public String businessAccurateAddress ;
    /**
     * 店铺数
     */
    public String numShops;

    /**
     * 经营名称
     */
    public String businessName;

    /**
     * 手机号码
     */
    public String mobilePhone;

    /**
     * 年租金
     */
    public String leaseYearAmt;

    /**
     * 合同结束日期
     */
    public String leaseContractEndTime;

    /**
     * 合同开始日期
     */
    public String leaseContractStartTime;

    /**
     * 房东姓名
     */
    public String landlordName;

    /**
     * 房东手机号
     */
    public String landlordPhone;

    /**
     * 业主姓名
     */
    public String proprietorPhone;

    /**
     * 业主电话
     */
    public String proprietorName;

    /**
     * 无租赁合同原因	1：自有房产，2：无偿使用，3：合同丢失
     */
    public String noLeaseContractReason;

    /**
     * 是否有租赁合同	1：有，0：无
     */
    public String hasLeaseContract;

    /**
     * 营业地址经纬度
     */
    public String bizAddrLonlat;

    /**
     * 营业地址
     */
    public String businessAddress;

    /**
     * 营业执照注册号
     */
    public String bizRegisterNo;

    /**
     * 一级行业编号
     */
    public String industryGId;

    /**
     * 一级行业名称
     */
    public String industryGName;

    /**
     * 二级行业编号
     */
    public String industryCId;

    /**
     * 二级行业名称
     */
    public String industryCName;

    /**
     * 三级行业编号
     */
    public String industryPId;

    /**
     * 三级行业名称
     */
    public String industryPName;

    /**
     * 二维码图片地址
     */
    public String qrcodeUrl;

    /**
     * 邀请数量
     */
    public String inviteCount;

    /**
     * 店铺数量
     */
    public String numLocations;

    /**
     * 红包数量
     */
    public String couponCount;

    public String objectId;
    public String applicationId;

    /**
     * 法定代表
     */
    public String ownerName;

    /**
     * 身份证号
     */
    public String ownerSSN;

    /**
     * 省编号
     */
    public String provinceId;

    /**
     * 市编号
     */
    public String cityId;

    /**
     * 区编号
     */
    public String countyId;

    /**
     * 省名称
     */
    public String provinceName;

    /**
     * 市名称
     */
    public String cityName;

    /**
     * 区名称
     */
    public String countyName;

    /**
     * 详细地址
     */
    public String ownerAddress;

    /**
     * 直系亲属
     */
    public String directName;

    /**
     * 亲属手机
     */
    public String directPhone;

    /**
     * 亲属类型
     */
    public String directType;

    /**
     * 紧急联络
     */
    public String emergencyName;

    /**
     * 联络手机
     */
    public String emergencyPhone;

    /**
     * 融资商铺名
     */
    public String corporateName;

    /**
     * 融资用途
     */
    public String loanPurpose;

    /**
     * 意向融资金额
     */
    public String loanAmount;

    /**
     * 意向融期限
     */
    public String planFundTerm;

}
