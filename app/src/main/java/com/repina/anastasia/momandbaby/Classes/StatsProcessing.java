package com.repina.anastasia.momandbaby.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.renderscript.Sampler;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Adapter.Item;
import com.repina.anastasia.momandbaby.Adapter.ItemArrayAdapter;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class StatsProcessing {

    public static void getBabyStats(final ItemArrayAdapter adapter, final Calendar dateAndTime, Context context, final ListView listViewBaby) {

        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();

        final DatabaseReference databaseReference = database.getReference();

        SharedPreferences sp = context.getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        final String babyID = sp.getString(SharedConstants.BABY_ID_KEY, "");

        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                if (!singleSnapshot.getKey().equals(DatabaseNames.USER)
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BAND)
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BANDDATA)
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BABY)) {
                                    HashMap<String, HashMap<String, String>> items =
                                            (HashMap<String, HashMap<String, String>>) singleSnapshot.getValue();
                                    for (Map.Entry<String, HashMap<String, String>> entry : items.entrySet()) {
                                        HashMap<String, String> value = entry.getValue();
                                        String date = value.get("date");
                                        if (date.substring(0, 10).equals(FormattedDate.getFormattedDateWithoutTime(dateAndTime))
                                                & value.get("babyId").equals(babyID)) {
                                            int imageId = getImageId(singleSnapshot.getKey());
                                            Item it = new Item(imageId, date);
                                            adapter.add(it);
                                        }
                                    }
                                }
                            }
                            listViewBaby.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void getMomStatsForOneDay(GoogleFit googleFit, final ItemArrayAdapter adapter, final Calendar dateAndTime, FragmentActivity activity, ListView listViewMom) {
        googleFit.getOneDayData(dateAndTime, activity, adapter, listViewMom);
    }

    public static void getMomStatsForOneWeek(GoogleFit googleFit, final ItemArrayAdapter adapter, final Calendar endDate, FragmentActivity activity,  ListView listViewMom) {
        Calendar startDate = endDate;
        startDate.add(Calendar.WEEK_OF_YEAR, -1);
        googleFit.getWeekData(startDate, endDate, activity, adapter, listViewMom);
    }

    private static int getImageId(String name) {
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
