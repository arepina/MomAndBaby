package com.repina.anastasia.momandbaby.Adapters;


public class ListViewItem {
    private String name;
    private int value; // 0 checkbox disable, 1  checkbox enable

    public ListViewItem(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

}