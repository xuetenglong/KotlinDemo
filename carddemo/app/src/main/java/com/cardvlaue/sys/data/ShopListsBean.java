package com.cardvlaue.sys.data;

/**
 * 查询商铺列表
 */
public class ShopListsBean {

    public String merchantId;

    public String applicationId;

    /**
     * 店铺名称
     */
    public String corporateName;

    /**
     * 法定代表
     */
    public String ownerName;

    /**
     * 营业执照注册号
     */
    public String bizRegisterNo;

    /**
     * 是否可编辑 1：锁定 0：未锁定
     */
    public String isDocumentLocked;

    public String status;

    /**
     * 当前选中店铺 1：当前店铺 0：不是
     */
    public String isCurrent;

    /**
     * 是否显示提示文本（本地显示用） true：显示 false：隐藏
     */
    public boolean isShowTip;

}
