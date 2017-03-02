package com.cardvlaue.sys.face;

public class FaceBean {

    /**
     * 返回码
     */
    public String retCode;
    /**
     * 字段说明
     */
    public String retMsg;
    /**
     * 返回数据
     */
    public RetData retData;

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public boolean success() {
        return "000000".equals(retCode);
    }

    public class RetData {

        /**
         * 返回信息
         */
        public String message;
        /**
         * 照片比对结果 <p> 00一致、01不一致、02无法验证
         */
        public String photoResult;
        /**
         * 身份证验证结果 <p> 00一致、01不一致、02无法验证
         */
        public String idCardResult;
        /**
         * 用户照片
         */
        public String userPhotoBase64;
        /**
         * 用户照片与身份证网格照片的相似度
         */
        public String similarity;
        /**
         * 身份证网格照片
         */
        public String idCardPhotoBase64;

        public void setPhotoResult(String photoResult) {
            this.photoResult = photoResult;
        }

        public void setIdCardResult(String idCardResult) {
            this.idCardResult = idCardResult;
        }

        public void setSimilarity(String similarity) {
            this.similarity = similarity;
        }

        /**
         * 身份证是否一致
         */
        public boolean idSuccess() {
            return "00".equals(idCardResult);
        }

        /**
         * 人脸是否一致
         */
        public boolean photoSuccess() {
            return "00".equals(photoResult);
        }

        public boolean unknown() {
            return "02".equals(idCardResult) || "02".equals(photoResult);
        }
    }
}
