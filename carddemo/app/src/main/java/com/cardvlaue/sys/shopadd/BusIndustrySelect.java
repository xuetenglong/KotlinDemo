package com.cardvlaue.sys.shopadd;

import com.cardvlaue.sys.data.IndustryItemResponse;

public class BusIndustrySelect extends IndustryItemResponse {

    private String bus;

    public BusIndustrySelect(String bus) {
        this.bus = bus;
    }

    public String getBus() {
        return bus;
    }

}
