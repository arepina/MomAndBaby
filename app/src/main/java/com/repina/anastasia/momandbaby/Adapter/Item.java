package com.repina.anastasia.momandbaby.Adapter;


public class Item {
    private int itemImg;
    private String itemDesc;

    public Item(int fruitImgResId, String itemDesc) {
        itemImg = fruitImgResId;
        this.itemDesc = itemDesc;
    }

    int getItemImg() {
        return itemImg;
    }

    String getItemDesc() {
        return itemDesc;
    }
}
