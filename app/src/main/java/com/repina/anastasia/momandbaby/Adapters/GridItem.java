package com.repina.anastasia.momandbaby.Adapters;


public class GridItem {
    private int itemImg;
    private String itemDesc;
    private String date;

    public GridItem(int imgResId, String itemDesc) {
        this.itemImg = imgResId;
        this.itemDesc = itemDesc;
    }

    public GridItem(int imgResId, String itemDesc, String date) {
        this.itemImg = imgResId;
        this.itemDesc = itemDesc;
        this.date = date;
    }

    public int getItemImg() {
        return itemImg;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public String getItemDate(){return date;}
}
