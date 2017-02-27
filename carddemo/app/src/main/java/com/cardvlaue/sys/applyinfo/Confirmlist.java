package com.cardvlaue.sys.applyinfo;

import java.util.List;

/**
 * 查询确认清单
 * <p/>
 * 增加传入参数type表示清单类型，1：申请信息，2：融资方案，3：融资保理通知书
 */
public class Confirmlist {

    private String title;
    private List<Item> items;
    private int code;
    private String error;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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
