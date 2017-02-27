package com.cardvlaue.sys.shopadd;

import com.cardvlaue.sys.data.AddressSearchItemResponse;

class BusAddressSearch extends AddressSearchItemResponse {

    private String bus;

    BusAddressSearch(String bus) {
        this.bus = bus;
    }

    public String getBus() {
        return bus;
    }
}
