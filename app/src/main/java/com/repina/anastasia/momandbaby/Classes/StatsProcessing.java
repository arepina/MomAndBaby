package com.repina.anastasia.momandbaby.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class StatsProcessing {

    public static void getBabyStats(final ItemArrayAdapter adapter, final Calendar dateAndTime, final Context context, final ListView listViewBaby) {

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
                                            Item it = new Item(imageId, formDescription(value));
                                            adapter.add(it);
                                        }
                                    }
                                }
                            }
                            if(adapter.getCount() == 0)
                            {
                                Item it = new Item(R.mipmap.cross, context.getResources().getString(R.string.no_data_today));
                                adapter.add(it);
                            }
                            listViewBaby.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private static String formDescription(HashMap<String, String> value) {
        String line = "";
        value.remove("babyId");
        value.remove("date");
        for (Map.Entry<String, String> entry : value.entrySet()) {
            String val = String.valueOf(entry.getValue());
            try {
                double number = Double.parseDouble(val);
                if (number != 0) {
                    line += Translator.translate(entry.getKey()) + ": " + val;
                    if (!"\n".equals(String.valueOf(line.charAt(line.length() - 1))))
                        line += "\n";
                }
            } catch (NumberFormatException e) { // not a number
                line += Translator.translate(entry.getKey()) + ": " + val;
                if (!"\n".equals(String.valueOf(line.charAt(line.length() - 1))))
                    line += "\n";
            }
        }
        line = line.substring(0, line.length() - 1);
        return line;

    }

    public static void getMomStatsForOneDay(GoogleFit googleFit, final ItemArrayAdapter adapter, final Calendar dateAndTime, FragmentActivity activity, ListView listViewMom) {
        Calendar today = Calendar.getInstance();
        Calendar dateClone = Calendar.getInstance();
        dateClone.setTime(dateAndTime.getTime());
        if (today.get(Calendar.DAY_OF_YEAR) == (dateClone.get(Calendar.DAY_OF_YEAR)) && today.get(Calendar.YEAR) == (dateClone.get(Calendar.YEAR)))
            googleFit.getOneDayData(dateClone, activity, adapter, listViewMom);
        else { // not today
            dateClone.set(Calendar.HOUR_OF_DAY, 0);
            dateClone.set(Calendar.MINUTE, 0);
            dateClone.set(Calendar.SECOND, 0);
            Calendar extra = Calendar.getInstance();
            extra.setTime(dateClone.getTime());
            extra.add(Calendar.MINUTE, 1439);
            extra.add(Calendar.SECOND, 59);
            //one second is not in the review
            googleFit.getPeriodData(dateClone, extra, activity, adapter, listViewMom);
        }
    }

    public static void getMomStatsForOneWeek(GoogleFit googleFit, final ItemArrayAdapter adapter, final Calendar endDate, FragmentActivity activity, ListView listViewMom) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(endDate.getTime());
        startDate.add(Calendar.WEEK_OF_YEAR, -1);
        googleFit.getPeriodData(startDate, endDate, activity, adapter, listViewMom);
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
