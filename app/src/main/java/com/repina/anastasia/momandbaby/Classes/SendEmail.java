package com.repina.anastasia.momandbaby.Classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Adapter.Item;
import com.repina.anastasia.momandbaby.Adapter.ItemArrayAdapter;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.repina.anastasia.momandbaby.Activity.TabsActivity.googleFit;

public class SendEmail {

    public static void createEmail(final Context context, int length, boolean whoFlag, FragmentActivity activity, ItemArrayAdapter adapter) {
        // logic depends on the who flag
        if (whoFlag) // baby
            createBabyEmail(context, length);
        else //mom
            createMomEmail(length, adapter, activity);
    }

    private static void createMomEmail(int length, ItemArrayAdapter adapter, FragmentActivity activity) {
        Calendar today = Calendar.getInstance();
        switch (length) {
            case 0: {
                StatsProcessing.getMomStatsForOneDay(googleFit, adapter, today, activity, null, true);
                break;
            }
            case 1: {
                //
                break;
            }
            case 2: {
                //
                break;
            }
        }
    }

    private static void createBabyEmail(final Context context, int length) {
        Calendar calendar = Calendar.getInstance();
        final String start = FormattedDate.getFormattedDateWithoutTime(calendar);
        String end = "";

        switch (length) {
            case 0: {
                end = start;
                break;
            }
            case 1: {
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                end = FormattedDate.getFormattedDateWithoutTime(calendar);
                break;
            }
            case 2: {
                calendar.add(Calendar.DAY_OF_MONTH, -31);
                end = FormattedDate.getFormattedDateWithoutTime(calendar);
                break;
            }
        }

        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();

        final DatabaseReference databaseReference = database.getReference();

        SharedPreferences sp = context.getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        final String babyID = sp.getString(SharedConstants.BABY_ID_KEY, "");

        final String finalEnd = end;
        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String report = "";
                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                if (!singleSnapshot.getKey().equals(DatabaseNames.USER)
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BAND)
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BANDDATA)
                                        & !singleSnapshot.getKey().equals(DatabaseNames.BABY)) {
                                    HashMap<String, HashMap<String, String>> items =
                                            (HashMap<String, HashMap<String, String>>) singleSnapshot.getValue();
                                    for (Map.Entry<String, HashMap<String, String>> entry : items.entrySet()) {
                                        HashMap<String, String> value = entry.getValue();
                                        String date = value.get("date").substring(0, 10);
                                        try {
                                            Date endDate = FormattedDate.stringToDate(start);
                                            Date startDate = FormattedDate.stringToDate(finalEnd);
                                            Date current = FormattedDate.stringToDate(date);
                                            long daysStartDif = Math.abs(getUnitBetweenDates(startDate, current, TimeUnit.DAYS));
                                            long daysEndDif = Math.abs(getUnitBetweenDates(current, endDate, TimeUnit.DAYS));
                                            if (value.get("babyId").equals(babyID)
                                                    &
                                                    ((current.before(endDate) & (startDate.before(current) || daysStartDif == 0)) ||
                                                            (current.before(endDate) || daysEndDif == 0) & startDate.before(current)) ||
                                                    (daysEndDif == 0 & daysStartDif == 0)) {
                                                report += cleanData(value, singleSnapshot);
                                            }
                                        } catch (ParseException ignored) {
                                        }
                                    }
                                }
                            }
                            if (report.length() == 0)
                                ToastShow.show(context, context.getString(R.string.no_data));
                            else
                                sendFile(report, context, start, finalEnd);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    static void formMomsReport(ItemArrayAdapter adapter, Context context, String start, String end) {
        String report = "";
        for (int i = 0; i < adapter.getCount(); i++) {
            Item it = adapter.getItem(i);
            report += imageToString(it.getItemImg()) + " " + it.getItemDate() + " " + it.getItemDesc() + "\n";
        }
        if (report.length() == 0)
            ToastShow.show(context, context.getString(R.string.no_data));
        else
            sendFile(report, context, start, end);
    }

    private static String imageToString(int imageIntPath) {
        //todo add all icons
        switch (imageIntPath) {
            case 2130903042: {
                return "Калории";
            }
            case 2130903058:{
                return "Вес";
            }
            case 2130903055:{
                return "Шаги";
            }
            default:
                return "";
        }
    }

    private static String cleanData(HashMap<String, String> value, DataSnapshot singleSnapshot) {
        //todo add translation
        ArrayList<String> values = new ArrayList<>(Arrays.asList(value.toString().replace("{", "").replace("}", "").split(" ")));
        //remove babyID data
        values.remove(values.size() - 1);
        int dateIndex = 0;
        //solve height and weight problem
        for (int i = 0; i < values.size(); i++) {
            String item = values.get(i);
            if (item.contains("weight")) {
                String[] weightArr = item.split("=");
                if (Double.parseDouble(weightArr[1].replace(",", "")) == 0) {
                    values.remove(i);
                    i--;
                }
            }
            if (item.contains("height")) {
                String[] heightArr = item.split("=");
                if (Double.parseDouble(heightArr[1].replace(",", "")) == 0) {
                    values.remove(i);
                    i--;
                }
            }
            if (item.contains("date"))
                dateIndex = i;
        }
        String dateValue = values.get(dateIndex);
        values.remove(dateIndex);
        values.set(values.size() - 1, values.get(values.size() - 1).replace(",", ""));//remove the last comma
        return singleSnapshot.getKey() + " " + dateValue + " " + TextUtils.join(" ", values) + "\n";
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    private static void sendFile(String report, Context context, String start, String finalEnd) {
        String fileName = context.getString(R.string.report_from) + " " + start + " " + context.getString(R.string.report_to) + " " + finalEnd + ".txt";
        try {
            File f = createFile(fileName, report);
            if (f != null) {
                Uri path = Uri.fromFile(f);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                SharedPreferences sp = context.getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
                String email = sp.getString(SharedConstants.MOM_EMAIL, "");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.report_from) + " " + start + " " + context.getString(R.string.report_to) + " " + finalEnd);
                i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text));
                i.putExtra(Intent.EXTRA_STREAM, path);
                context.startActivity(Intent.createChooser(i, context.getString(R.string.report_sending)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else
                ToastShow.show(context, context.getString(R.string.report_error));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File createFile(String name, String content) throws IOException {
        // get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            File folder = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                Log.d("Success", "Open");
                // create the file in which we will write the contents
                File file = new File(folder, name);
                FileOutputStream os = new FileOutputStream(file);
                os.write(content.getBytes());
                os.close();
                return file;
            } else
                Log.d("Failed", "Open");
        }
        return null;
    }
}
