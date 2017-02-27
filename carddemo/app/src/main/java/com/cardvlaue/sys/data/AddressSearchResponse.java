package com.cardvlaue.sys.data;

import android.text.TextUtils;
import java.util.List;

public class AddressSearchResponse {

    public String resultMsg;

    public int code;

    public List<AddressSearchItemResponse> resultData;

    public String error;

    public boolean requestSuccess() {
        return code == 1;
    }

    public boolean requestError() {
        return !TextUtils.isEmpty(error);
    }

}
