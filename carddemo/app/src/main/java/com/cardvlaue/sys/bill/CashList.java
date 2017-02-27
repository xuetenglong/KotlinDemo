package com.cardvlaue.sys.bill;

import java.util.List;

class CashList {

    private List<CashListItem> results;
    private String count;
    private int code;
    private String error;

    public List<CashListItem> getResults() {
        return results;
    }

    public void setResults(List<CashListItem> results) {
        this.results = results;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
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
