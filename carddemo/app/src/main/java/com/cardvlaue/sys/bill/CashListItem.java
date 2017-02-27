package com.cardvlaue.sys.bill;

class CashListItem {

    private String receiveDate;
    private String shouldReturnDate;
    private int shouldReturnMoney;
    private int receiveMoney;
    private int code;
    private String error;

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getShouldReturnDate() {
        return shouldReturnDate;
    }

    public void setShouldReturnDate(String shouldReturnDate) {
        this.shouldReturnDate = shouldReturnDate;
    }

    public int getShouldReturnMoney() {
        return shouldReturnMoney;
    }

    public void setShouldReturnMoney(int shouldReturnMoney) {
        this.shouldReturnMoney = shouldReturnMoney;
    }

    public int getReceiveMoney() {
        return receiveMoney;
    }

    public void setReceiveMoney(int receiveMoney) {
        this.receiveMoney = receiveMoney;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
