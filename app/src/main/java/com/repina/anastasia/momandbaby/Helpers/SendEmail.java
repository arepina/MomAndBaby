package com.repina.anastasia.momandbaby.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.Fragment.FragmentSettings;
import com.repina.anastasia.momandbaby.Processing.FileProcessing;
import com.repina.anastasia.momandbaby.Processing.StatsProcessing;
import com.repina.anastasia.momandbaby.Processing.TextProcessing;
import com.repina.anastasia.momandbaby.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.repina.anastasia.momandbaby.Processing.TextProcessing.dbNameToString;

/**
 * Send email reports
 */
public class SendEmail {

    /**
     * Create email
     *
     * @param context    context
     * @param length     duration period
     * @param from       start
     * @param to         end
     * @param whoFlag    mom or baby
     * @param activity   activity
     * @param callingKey activity calling key
     */
    public static void createEmail(final Context context, int length, Calendar from, Calendar to,
                                   boolean whoFlag, FragmentActivity activity, String callingKey) {
        // logic depends on the who isActivityAlreadyCreated
        if (whoFlag) // baby
            createBabyEmail(context, length, from, to);
        else //mom
            createMomEmail(activity, from, to, callingKey);
    }

    /**
     * Create mom report
     *
     * @param activity   activity
     * @param from       start
     * @param to         end
     * @param callingKey activity calling key
     */
    private static void createMomEmail(FragmentActivity activity, Calendar from, Calendar to, String callingKey) {
        int days = daysBetween(from, to);
        StatsProcessing.getMomStats(to, days, activity, 0, callingKey); // custom length, all types
    }

    /**
     * Create baby report
     *
     * @param context context
     * @param length  duration period
     * @param from    start
     * @param to      end
     */
    private static void createBabyEmail(final Context context, int length, Calendar from, Calendar to) {
        Calendar calendar = Calendar.getInstance();
        String start = "";
        String end = FormattedDate.getFormattedDate(calendar);

        switch (length) {
            case 0: {
                start = end;
                break;
            }
            case 1: {
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                start = FormattedDate.getFormattedDate(calendar);
                break;
            }
            case 2: {
                calendar.add(Calendar.DAY_OF_MONTH, -31);
                start = FormattedDate.getFormattedDate(calendar);
                break;
            }
            case 3: {
                long days = daysBetween(from, to);
                if (days == 0) {
                    start = FormattedDate.getFormattedDate(from);
                    end = FormattedDate.getFormattedDate(from);
                } else {
                    start = FormattedDate.getFormattedDate(from);
                    calendar.setTime(from.getTime());
                    calendar.add(Calendar.DAY_OF_MONTH, (int) days);
                    end = FormattedDate.getFormattedDate(calendar);
                }
                break;
            }
        }

        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();

        final DatabaseReference databaseReference = database.getReference();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
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
                                                if(singleSnapshot.getKey().equals(DatabaseNames.TEETH)) // form teeth part of report
                                                {
                                                    Object s = value.get("whenHave");
                                                    StringBuilder reportStr = new StringBuilder();
                                                    reportStr.append(context.getString(R.string.baby_has)).append(" ");
                                                    Object[] whenHave = ((ArrayList) s).toArray();
                                                    for(int i = 0; i < whenHave.length; i++)
                                                    {
                                                        if(Integer.parseInt(whenHave[i].toString()) != -1)
                                                            reportStr.append("№").append(i + 1).append(": ")
                                                                    .append(whenHave[i]).append(" ")
                                                                    .append(context.getString(R.string.month)).append("; ");
                                                    }
                                                    reportStr = new StringBuilder(reportStr.substring(0, reportStr.length() - 2));
                                                    String dbName = singleSnapshot.getKey();
                                                    String lang = Locale.getDefault().getDisplayLanguage();
                                                    if (lang.toLowerCase().equals(context.getString(R.string.russian))) {
                                                        dbName = dbNameToString(singleSnapshot.getKey());
                                                    }
                                                    report.append(dbName).append(" ").append(date).append(" ").append(reportStr).append("\n");
                                                }else // all the others parts
                                                    report.append(TextProcessing.cleanData(value, singleSnapshot, context));
                                            }
                                        } catch (ParseException ignored) {
                                        }
                                    }
                                }
                            }
                            if (report.length() == 0)
                                NotificationsShow.showToast(context, context.getString(R.string.no_data));
                            else
                                FileProcessing.sendFile(report.toString(), context, finalStart, finalEnd); // send baby report
                            if (FragmentSettings.dialog != null)
                                FragmentSettings.dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Get
     *
     * @param startDate start
     * @param endDate   end
     * @param unit      time unit
     * @return time unit in milliseconds
     */
    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    /**
     * Get days difference
     *
     * @param startDate start
     * @param endDate   end
     * @return days difference
     */
    private static int daysBetween(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }
}
