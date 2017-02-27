package com.cardvlaue.sys.data;

public class IndustryItemResponse {

    private String id;

    private String title;

    private String amount;

    private String typeId;

    public String getId() {
        EventConst.INSTANCE.getINDUSTRY_SELECT();
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

}
