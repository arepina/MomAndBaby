package com.repina.anastasia.momandbaby.Classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class SendEmail {

    public static void createEmail(final Context context, int length, boolean whoFlag) {

        //todo add mom logic depends on who flag

        Calendar calendar = Calendar.getInstance();
        final String start = FormattedDate.getFormattedDateWithoutTime(calendar);
        String end = "";

        switch (length) {
            case 0: {
                end = start;
                break;
            }
            case 1: {
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                end = FormattedDate.getFormattedDateWithoutTime(calendar);
                break;
            }
            case 2: {
                calendar.add(Calendar.DAY_OF_MONTH, 31);
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
                                            Date startDate = FormattedDate.stringToDate(start);
                                            Date endDate = FormattedDate.stringToDate(finalEnd);
                                            Date current = FormattedDate.stringToDate(date);
                                            long daysStartDif = getUnitBetweenDates(startDate, current, TimeUnit.DAYS);
                                            long daysEndDif = getUnitBetweenDates(current, endDate, TimeUnit.DAYS);
                                            if (value.get("babyId").equals(babyID)
                                                    &
                                                    ((current.before(endDate) & (startDate.before(current) || daysStartDif == 0)) ||
                                                    (current.before(endDate) || daysEndDif == 0) & startDate.before(current)) ||
                                                    (daysEndDif == 0 & daysStartDif == 0)){
                                                //todo make the report more read-friendly
                                                report += value.toString().replace("{", "").replace("}", "") + "\n";
                                            }
                                        } catch (ParseException ignored) {
                                        }
                                    }
                                }
                            }
                            //todo fix the bug
                            try {
                                String fileName = context.getString(R.string.report_from) + " " + start + " " + context.getString(R.string.report_to) + " " + finalEnd + ".txt";
                                File root = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.reports));
                                if (!root.exists())
                                    root.mkdirs();
                                File gpxfile = new File(root, fileName);
                                FileWriter writer = new FileWriter(gpxfile);
                                writer.append(report);
                                writer.flush();
                                writer.close();
                                Uri path = Uri.fromFile(gpxfile);

                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                SharedPreferences sp = context.getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
                                String email = sp.getString(SharedConstants.MOM_EMAIL, "");
                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                                i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.report_from) + " " + start + " " + context.getString(R.string.report_to) + " " + finalEnd);
                                i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text));
                                i.putExtra(Intent.EXTRA_STREAM, path);
                                context.startActivity(Intent.createChooser(i, context.getString(R.string.report_sending)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
}
