package com.cardvlaue.sys.uploadphoto;

import java.util.List;

class NewFileListsItemBO {

    private String abbr;
    private String checklistId;
    private String demo;
    private List<NewFileListsImgBO> files;
    private int filesCount;
    private int lackFiles;
    private String layerSecond;
    private String title;
    private String rfe;
    private List<NewFileListsImgBO> items;

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(String checklistId) {
        this.checklistId = checklistId;
    }

    public String getDemo() {
        return demo;
    }

    public void setDemo(String demo) {
        this.demo = demo;
    }

    public List<NewFileListsImgBO> getFiles() {
        return files;
    }

    public void setFiles(List<NewFileListsImgBO> files) {
        this.files = files;
    }

    public int getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(int filesCount) {
        this.filesCount = filesCount;
    }

    public int getLackFiles() {
        return lackFiles;
    }

    public void setLackFiles(int lackFiles) {
        this.lackFiles = lackFiles;
    }

    public String getLayerSecond() {
        return layerSecond;
    }

    public void setLayerSecond(String layerSecond) {
        this.layerSecond = layerSecond;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRfe() {
        return rfe;
    }

    public void setRfe(String rfe) {
        this.rfe = rfe;
    }

    public List<NewFileListsImgBO> getItems() {
        return items;
    }

    public void setItems(List<NewFileListsImgBO> items) {
        this.items = items;
    }
}
