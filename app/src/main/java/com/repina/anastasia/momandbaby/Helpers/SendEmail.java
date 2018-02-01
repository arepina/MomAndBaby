package com.repina.anastasia.momandbaby.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.Processing.FileProcessing;
import com.repina.anastasia.momandbaby.Processing.StatsProcessing;
import com.repina.anastasia.momandbaby.Processing.TextProcessing;
import com.repina.anastasia.momandbaby.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class SendEmail {

    public static void createEmail(final Context context, int length, Calendar from, Calendar to,
                                   boolean whoFlag, FragmentActivity activity,
                                   GridItemArrayAdapter adapter) {
        // logic depends on the who flag
        if (whoFlag) // baby
            createBabyEmail(context, length, from, to);
        else //mom
            createMomEmail(length, adapter, activity, from, to);
    }

    private static void createMomEmail(int length, GridItemArrayAdapter adapter,
                                       FragmentActivity activity, Calendar from, Calendar to) {
        Calendar today = Calendar.getInstance();
        switch (length) {
            case 0: {
                StatsProcessing.getMomStatsForOneDay(adapter, today, activity,
                        null, true); // 1 day
                break;
            }
            case 1: {
                StatsProcessing.getMomStatsForPeriod(adapter, today, activity,
                        null, 7, true, false, null); // 1 week
                break;
            }
            case 2: {
                StatsProcessing.getMomStatsForPeriod(adapter, today, activity,
                        null, 31, true, false, null); // 1 month
                break;
            }
            case 3: {
                long days = daysBetween(from, to);
                if (days == 0)
                    StatsProcessing.getMomStatsForOneDay(adapter, from, activity,
                            null, true); // 1 day
                else
                    StatsProcessing.getMomStatsForPeriod(adapter, to, activity,
                            null, days, true, false, null); // custom
                break;
            }
        }
    }

    private static void createBabyEmail(final Context context, int length, Calendar from, Calendar to) {
        Calendar calendar = Calendar.getInstance();
        String start = "";
        String end = FormattedDate.getFormattedDateWithoutTime(calendar);

        switch (length) {
            case 0: {
                start = end;
                break;
            }
            case 1: {
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                start = FormattedDate.getFormattedDateWithoutTime(calendar);
                break;
            }
            case 2: {
                calendar.add(Calendar.DAY_OF_MONTH, -31);
                start = FormattedDate.getFormattedDateWithoutTime(calendar);
                break;
            }
            case 3: {
                long days = daysBetween(from, to);
                if (days == 0) {
                    start = FormattedDate.getFormattedDateWithoutTime(from);
                    end = FormattedDate.getFormattedDateWithoutTime(from);
                } else {
                    start = FormattedDate.getFormattedDateWithoutTime(from);
                    calendar.setTime(from.getTime());
                    calendar.add(Calendar.DAY_OF_MONTH, (int) days);
                    end = FormattedDate.getFormattedDateWithoutTime(calendar);
                }
                break;
            }
        }

        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();

        final DatabaseReference databaseReference = database.getReference();

        SharedPreferences sp = context.getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        final String babyID = sp.getString(SharedConstants.BABY_ID_KEY, "");

        final String finalEnd = end;
        final String finalStart = start;
        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            StringBuilder report = new StringBuilder();
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                if (!singleSnapshot.getKey().equals(DatabaseNames.USER)
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BABY)) {
                                    HashMap<String, HashMap<String, String>> items =
                                            (HashMap<String, HashMap<String, String>>) singleSnapshot.getValue();
                                    for (Map.Entry<String, HashMap<String, String>> entry : items.entrySet()) {
                                        HashMap<String, String> value = entry.getValue();
                                        String date = value.get("date").substring(0, 10);
                                        try {
                                            Date endDate = FormattedDate.stringToDate(finalEnd);
                                            Date startDate = FormattedDate.stringToDate(finalStart);
                                            Date current = FormattedDate.stringToDate(date);
                                            long daysStartDif = Math.abs(getUnitBetweenDates(startDate, current, TimeUnit.DAYS));
                                            long daysEndDif = Math.abs(getUnitBetweenDates(current, endDate, TimeUnit.DAYS));
                                            if (value.get("babyId").equals(babyID)
                                                    &
                                                    ((current.before(endDate) & (startDate.before(current) || daysStartDif == 0)) ||
                                                            (current.before(endDate) || daysEndDif == 0) & startDate.before(current)) ||
                                                    (daysEndDif == 0 & daysStartDif == 0)) {
                                                report.append(TextProcessing.cleanData(value, singleSnapshot));
                                            }
                                        } catch (ParseException ignored) {
                                        }
                                    }
                                }
                            }
                            if (report.length() == 0)
                                NotificationsShow.showToast(context, context.getString(R.string.no_data));
                            else
                                FileProcessing.sendFile(report.toString(), context, finalStart, finalEnd);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    private static long daysBetween(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }
}
