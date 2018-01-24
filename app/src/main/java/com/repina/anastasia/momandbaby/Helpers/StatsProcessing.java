package com.repina.anastasia.momandbaby.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class StatsProcessing {

    public static void getBabyStatsForOneDay(final GridItemArrayAdapter adapter, final Calendar dateAndTime, final Context context, final ListView listViewBaby) {

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
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BABY)) {
                                    HashMap<String, HashMap<String, String>> items =
                                            (HashMap<String, HashMap<String, String>>) singleSnapshot.getValue();
                                    for (Map.Entry<String, HashMap<String, String>> entry : items.entrySet()) {
                                        HashMap<String, String> value = entry.getValue();
                                        String date = value.get("date");
                                        if (date.substring(0, 10).equals(FormattedDate.getFormattedDateWithoutTime(dateAndTime))
                                                & value.get("babyId").equals(babyID)) {
                                            int imageId = getImageId(singleSnapshot.getKey(), value);
                                            GridItem it = new GridItem(imageId, formDescription(value), entry.getKey(), singleSnapshot.getKey());
                                            adapter.add(it);
                                        }
                                    }
                                }
                            }
                            if(adapter.getCount() == 0)
                            {
                                GridItem it = new GridItem(R.mipmap.cross, context.getResources().getString(R.string.no_data_today), null, null);
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
                    line += Translator.translateWord(entry.getKey()) + ": " + val;
                    if (!"\n".equals(String.valueOf(line.charAt(line.length() - 1))))
                        line += "\n";
                }
            } catch (NumberFormatException e) { // not a number
                line += Translator.translateWord(entry.getKey()) + ": " + val;
                if (!"\n".equals(String.valueOf(line.charAt(line.length() - 1))))
                    line += "\n";
            }
        }
        line = line.substring(0, line.length() - 1);
        return line;

    }

    public static void getMomStatsForOneDay(GoogleFit googleFit, final GridItemArrayAdapter adapter, final Calendar dateAndTime, FragmentActivity activity, ListView listViewMom, boolean isEmail) {
        Calendar today = Calendar.getInstance();
        Calendar dateClone = Calendar.getInstance();
        dateClone.setTime(dateAndTime.getTime());
        if (today.get(Calendar.DAY_OF_YEAR) == (dateClone.get(Calendar.DAY_OF_YEAR)) && today.get(Calendar.YEAR) == (dateClone.get(Calendar.YEAR)))
            googleFit.getOneDayData(dateClone, activity, adapter, listViewMom, isEmail);
        else { // not today
            dateClone.set(Calendar.HOUR_OF_DAY, 0);
            dateClone.set(Calendar.MINUTE, 0);
            dateClone.set(Calendar.SECOND, 0);
            Calendar extra = Calendar.getInstance();
            extra.setTime(dateClone.getTime());
            extra.add(Calendar.MINUTE, 1439);
            extra.add(Calendar.SECOND, 59);
            //one second is not in the review
            googleFit.getPeriodData(dateClone, extra, activity, adapter, listViewMom, isEmail);
        }
    }

    static void getMomStatsForPeriod(GoogleFit googleFit, final GridItemArrayAdapter adapter, final Calendar endDate, FragmentActivity activity, ListView listViewMom, int length, boolean isEmail) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(endDate.getTime());
        if(length == 7) // - 7 days
            startDate.add(Calendar.WEEK_OF_YEAR, -1);
        else
            startDate.add(Calendar.MONTH, -1); // - 1 month
        googleFit.getPeriodData(startDate, endDate, activity, adapter, listViewMom, isEmail);
    }

    private static int getImageId(String name, HashMap<String, String> value) {
        if (name.equals(DatabaseNames.METRICS))
        {
            if("0".equals(String.valueOf(value.get("weight"))))
                return R.mipmap.height;
            else
                return R.mipmap.weight;
        }
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
