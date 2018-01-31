package com.repina.anastasia.momandbaby.Helpers.Processing;

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
import com.repina.anastasia.momandbaby.Async.ViewPeriodTask;
import com.repina.anastasia.momandbaby.Async.ViewTodayTask;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
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
                                            int imageId = ImageProcessing.getImageId(singleSnapshot.getKey(), value);
                                            String imageName = ImageProcessing.getImageName(singleSnapshot.getKey(), value);
                                            GridItem it = new GridItem(imageId, imageName,
                                                    TextProcessing.formBabyDescription(value), entry.getKey(),
                                                    singleSnapshot.getKey());
                                            adapter.add(it);
                                        }
                                    }
                                }
                            }
                            if (adapter.getCount() == 0) {
                                GridItem it = new GridItem(R.mipmap.cross, "R.mipmap.cross", context.getResources().getString(R.string.no_data_today), null, null);
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

    public static void getMomStatsForOneDay(final GridItemArrayAdapter adapter, final Calendar dateAndTime, FragmentActivity activity, ListView listViewMom, boolean isEmail) {
        Calendar today = Calendar.getInstance();
        Calendar dateClone = Calendar.getInstance();
        dateClone.setTime(dateAndTime.getTime());
        if (today.get(Calendar.DAY_OF_YEAR) == (dateClone.get(Calendar.DAY_OF_YEAR)) && today.get(Calendar.YEAR) == (dateClone.get(Calendar.YEAR)))
            getOneDayData(dateClone, adapter, listViewMom, isEmail, activity);
        else { // not today
            dateClone.set(Calendar.HOUR_OF_DAY, 0);
            dateClone.set(Calendar.MINUTE, 0);
            dateClone.set(Calendar.SECOND, 0);
            Calendar extra = Calendar.getInstance();
            extra.setTime(dateClone.getTime());
            extra.add(Calendar.MINUTE, 1439);
            extra.add(Calendar.SECOND, 59);
            //one second is not in the review
            getPeriodData(dateClone, extra, adapter, listViewMom, isEmail, false, null, activity);
        }
    }

    public static void getMomStatsForPeriod(final GridItemArrayAdapter adapter,
                                            final Calendar endDate, FragmentActivity activity,
                                            ListView listViewMom, long length,
                                            boolean isEmail, boolean isChart, String selectedItemName) {
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(endDate.getTime());
        if (length == 7) // - 7 days
            startDate.add(Calendar.WEEK_OF_YEAR, -1);
        else if(length == 31) // - 1 month
            startDate.add(Calendar.MONTH, -1);
        else
            startDate.add(Calendar.DAY_OF_YEAR, (int) -length); // custom
        getPeriodData(startDate, endDate, adapter, listViewMom, isEmail, isChart, selectedItemName, activity);
    }

    private static void getPeriodData(Calendar startDate, Calendar endDate,
                                      GridItemArrayAdapter adapter, ListView listView,
                                      boolean isEmail, boolean isChart, String selectedItemName, FragmentActivity activity) {
        String start = FormattedDate.getFormattedDateWithoutTime(startDate);
        String end = FormattedDate.getFormattedDateWithoutTime(endDate);
        new ViewPeriodTask(isEmail, isChart, selectedItemName, activity, listView, adapter, start, end).execute(startDate, endDate);
    }

    private static void getOneDayData(Calendar date, GridItemArrayAdapter adapter,
                               ListView listView, boolean isEmail, FragmentActivity activity) {
        String start = FormattedDate.getFormattedDateWithoutTime(date);
        String end = FormattedDate.getFormattedDateWithoutTime(date);
        new ViewTodayTask(isEmail, activity, listView, adapter, start, end).execute(date);
    }
}
