package com.repina.anastasia.momandbaby.Classes;

import android.content.res.Resources;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Adapter.Item;
import com.repina.anastasia.momandbaby.Adapter.ItemArrayAdapter;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FirebaseData {

    public static void updateTodayBaby(final Resources resources, final ItemArrayAdapter adapter) {

        FirebaseDatabase database = FirebaseConnection.getDatabase();

        final DatabaseReference databaseReference = database.getReference();

        final Calendar dateAndTime = Calendar.getInstance();

        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Item> today = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                if (!singleSnapshot.getKey().equals(DatabaseNames.USER)
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BAND)
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BANDDATA)) {
                                    HashMap<String, HashMap<String, String>> items =
                                            (HashMap<String, HashMap<String, String>>) singleSnapshot.getValue();
                                    for (Map.Entry<String, HashMap<String, String>> entry : items.entrySet()) {
                                        HashMap<String, String> value = entry.getValue();
                                        String date = value.get("date");
                                        if (date.substring(0, 10).equals(FormattedDate.getFormattedDateWithoutTime(dateAndTime))) {
                                            int imageId = getImageId(singleSnapshot.getKey(), resources);
                                            Item it = new Item(imageId, date.substring(10, date.length()));
                                            adapter.add(it);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static ArrayList<Item> updateTodayMom() {
        return new ArrayList<>();
    }

    private static int getImageId(String name, Resources resources) {
        //todo think about height and weight icon
        if (name.equals(DatabaseNames.METRICS))
            return R.mipmap.height;
        if (name.equals(DatabaseNames.STOOL))
            return R.mipmap.diapers;
        if (name.equals(DatabaseNames.VACCINATION))
            return R.mipmap.vaccination;
        if (name.equals(DatabaseNames.ILLNESS))
            return R.mipmap.illness;
        if (name.equals(DatabaseNames.FOOD))
            return R.mipmap.food;
        if (name.equals(DatabaseNames.OUTDOOR))
            return R.mipmap.outdoor;
        if (name.equals(DatabaseNames.SLEEP))
            return R.mipmap.sleep;
        return -1;
    }
}
