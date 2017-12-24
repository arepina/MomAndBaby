package com.repina.anastasia.momandbaby.Classes;

import android.content.res.Resources;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Adapter.Item;
import com.repina.anastasia.momandbaby.Adapter.ItemArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class FirebaseData {

    public static ArrayList<String[]> getTodayBaby() {

        ArrayList<String[]> today = new ArrayList<>();

        FirebaseDatabase database = FirebaseConnection.getDatabase();

        final DatabaseReference databaseReference = database.getReference();

        final Calendar dateAndTime = Calendar.getInstance();

        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            //todo add today data to today array list
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        return today;
    }

    public static ArrayList<String[]> getTodayMom() {
        return new ArrayList<>();
    }

    public static void addValues(ArrayList<String[]> itemList, ItemArrayAdapter adapter, Resources resources) {
        for (String[] itemData : itemList) {
            String itemImg = itemData[0];
            String itemDesc = itemData[1];
            int itemImgResId = resources.getIdentifier(itemImg, "drawable", "com.repina.anastasia.momandbaby");

            Item item = new Item(itemImgResId, itemDesc);
            adapter.add(item);
        }
    }

}
