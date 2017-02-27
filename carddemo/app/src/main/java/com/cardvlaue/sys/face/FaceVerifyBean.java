package com.cardvlaue.sys.face;

/**
 * 人脸识别验证
 */
public class FaceVerifyBean {

    /**
     * 验证状态 <br> 成功或失败
     */
    public boolean status;

    /**
     * 失败次数
     */
    public int count;

    public FaceVerifyBean() {
    }

    public FaceVerifyBean(boolean status, int count) {
        this.status = status;
        this.count = count;
    }
}
