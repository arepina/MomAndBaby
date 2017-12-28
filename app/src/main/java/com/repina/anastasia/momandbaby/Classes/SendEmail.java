package com.repina.anastasia.momandbaby.Classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.repina.anastasia.momandbaby.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class SendEmail {

    public static void sendEmail(Context context, int length, boolean whoFlag) {
        Calendar calendar = Calendar.getInstance();
        String start = FormattedDate.getFormattedDateWithoutTime(calendar);
        String end = "";
        String text = "";
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
        if(whoFlag)
            text = FirebaseData.getBabyStats(start, end, context);
        else
            text = FirebaseData.getMomStats(start, end, context);
        try {
            String fileName = context.getString(R.string.report_from) + start + context.getString(R.string.report_to) + end + ".txt";
            File root = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.reports));
            if (!root.exists()) root.mkdirs();
            File gpxfile = new File(root, fileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(text);
            writer.flush();
            writer.close();
            Uri path = Uri.fromFile(gpxfile);

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.my_email)});
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.report_from) + start + context.getString(R.string.report_to) + end);
            i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text));
            i.putExtra(Intent.EXTRA_STREAM, path);
            context.startActivity(Intent.createChooser(i, context.getString(R.string.report_sending)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
