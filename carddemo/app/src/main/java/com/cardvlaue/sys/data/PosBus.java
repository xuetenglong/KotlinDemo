package com.cardvlaue.sys.data;

public class PosBus {

    public String event;

    public String verifyId;

    /**
     * 答案
     */
    public String mId;

    public PosBus(String event, String verifyId, String mId) {
        this.event = event;
        this.verifyId = verifyId;
        this.mId = mId;
    }

}
