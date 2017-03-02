package com.cardvlaue.sys.invitat;

import com.cardvlaue.sys.data.ErrorResponse;

/**
 * Created by Administrator on 2016/9/7.
 */
public class ShareLatestItem extends ErrorResponse {

    private String title;

    private String describe;

    private String link;

    private String imgUrl;

    private String showImgUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getShowImgUrl() {
        return showImgUrl;
    }

    public void setShowImgUrl(String showImgUrl) {
        this.showImgUrl = showImgUrl;
    }
}
