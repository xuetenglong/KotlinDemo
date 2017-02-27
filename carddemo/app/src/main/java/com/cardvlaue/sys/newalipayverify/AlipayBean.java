package com.cardvlaue.sys.newalipayverify;

/**
 * Created by Administrator on 2016/12/22.
 */

public class AlipayBean {

    /**
     * 按钮是否点击
     */
    public boolean onclick;

    public String url;

    public AlipayBean() {
    }


    public AlipayBean(boolean onclick, String url) {
        this.onclick = onclick;
        this.url = url;
    }
}
