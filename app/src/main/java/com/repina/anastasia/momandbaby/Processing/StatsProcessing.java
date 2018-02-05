package com.repina.anastasia.momandbaby.Processing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Activity.TabsActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.Fragment.FragmentMom;
import com.repina.anastasia.momandbaby.Fragment.FragmentSettings;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.GoogleFitService;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.CALLING;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FROM;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.SERVICE_REQUEST_TYPE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TO;
import static java.lang.Thread.sleep;

public class StatsProcessing {

    public static void getBabyStats(final GridItemArrayAdapter adapter, final Calendar dateAndTime, final Context context, final ListView listViewBaby) {

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
                                        if (date.substring(0, 10).equals(FormattedDate.getFormattedDate(dateAndTime))
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

    public static void getMomStats(Calendar end, int length, FragmentActivity activity, int type, String callingKey) {
        Calendar start = Calendar.getInstance();
        start.setTime(end.getTime());
        if (length == 7) // - 7 days
            start.add(Calendar.WEEK_OF_YEAR, -1);
        else if(length == 31) // - 1 month
            start.add(Calendar.MONTH, -1);
        else
            start.add(Calendar.DATE, -length); // custom
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        getPeriodData(start, end, activity, type, callingKey);
    }

    private static void getPeriodData(Calendar from, Calendar to, FragmentActivity activity, int type, String callingKey) {
        Intent service = new Intent(activity, GoogleFitService.class);
        service.putExtra(FROM, from.getTimeInMillis());
        service.putExtra(TO, to.getTimeInMillis());
        service.putExtra(CALLING, callingKey);
        if(type != 0)  // custom
        {
            // need aggregated data for a concrete type
            service.putExtra(SERVICE_REQUEST_TYPE, type);
            activity.startService(service);
        }
        else{
            if(callingKey.equals(FragmentSettings.class.toString())) // need aggregated data for all types in sum
            {
                service.putExtra(SERVICE_REQUEST_TYPE, 0);
                activity.startService(service);
            }
            else {  // need aggregated data for all types in parts
                for (int i = 2; i <= 6; i++) // indexes of local consts
                {
                    service.putExtra(SERVICE_REQUEST_TYPE, i);
                    activity.startService(service);
                }
            }
        }
    }
}
