package com.repina.anastasia.momandbaby.Adapters;


import java.io.Serializable;

public class GridItem implements Serializable {
    private String key;
    private String type;
    private int itemImg;
    private String itemImgName;
    private String itemDesc;
    private String date;

    public GridItem(int imgResId, String itemImgName, String itemDesc, String key, String type) {
        this.itemImg = imgResId;
        this.itemImgName = itemImgName;
        this.itemDesc = itemDesc;
        this.key = key;
        this.type = type;
    }

    public GridItem(int imgResId, String itemImgName, String itemDesc, String date) {
        this.itemImg = imgResId;
        this.itemImgName = itemImgName;
        this.itemDesc = itemDesc;
        this.date = date;
    }

    public int getItemImg() {
        return itemImg;
    }

    public String getItemImgName() {
        return itemImgName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public String getItemDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }
}
