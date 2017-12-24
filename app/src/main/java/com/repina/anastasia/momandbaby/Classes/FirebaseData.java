package com.repina.anastasia.momandbaby.Classes;

import android.content.res.Resources;

import com.repina.anastasia.momandbaby.Adapters.Item;
import com.repina.anastasia.momandbaby.Adapters.ItemArrayAdapter;

import java.util.ArrayList;

public class FirebaseData {

    public static ArrayList<String[]> getTodayBaby() {
        return null;
    }

    public static ArrayList<String[]> getTodayMom() {
        return null;
    }

    public static void addValues(ArrayList<String[]> itemList, ItemArrayAdapter adapter, Resources resources) {
        for (String[] itemData : itemList) {
            String itemImg = itemData[0];
            String itemDesc = itemData[1];
            int fruitImgResId = resources.getIdentifier(itemImg, "drawable", "com.repina.anastasia.momandbaby");

            Item item = new Item(fruitImgResId, itemDesc);
            adapter.add(item);
        }
    }

}
