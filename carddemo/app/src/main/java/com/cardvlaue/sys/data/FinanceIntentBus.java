package com.cardvlaue.sys.data;

public class FinanceIntentBus {

    /**
     * 事件
     */
    public String event;

    /**
     * 店铺名
     */
    public String shop;

    /**
     * 融资用途
     */
    public String use;

    /**
     * 融资金额
     */
    public String amount;

    /**
     * 融资期限
     */
    public String line;

    public FinanceIntentBus(String event, String shop, String use, String amount, String line) {
        this.event = event;
        this.shop = shop;
        this.use = use;
        this.amount = amount;
        this.line = line;
    }
}
