package com.repina.anastasia.momandbaby.Adapter;


public class Item {
    private int itemImg;
    private String itemDesc;

    public Item(int imgResId, String itemDesc) {
        itemImg = imgResId;
        this.itemDesc = itemDesc;
    }

    public Item(int imgResId, String itemDesc, String date) {
        itemImg = imgResId;
        this.itemDesc = itemDesc + " " + date;
    }

    int getItemImg() {
        return itemImg;
    }

    String getItemDesc() {
        return itemDesc;
    }
}
