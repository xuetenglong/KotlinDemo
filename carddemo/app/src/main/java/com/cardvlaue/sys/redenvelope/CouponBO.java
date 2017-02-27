package com.cardvlaue.sys.redenvelope;

/**
 * Created by Administrator on 2016/7/19.
 */
class CouponBO {

    private String id;
    private String type;
    private String amount;
    private String memo;
    private String createTime;
    private String timeoutTime;
    private String status;
    private boolean isSelected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTimeoutTime() {
        return timeoutTime;
    }

    public void setTimeoutTime(String timeoutTime) {
        this.timeoutTime = timeoutTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
