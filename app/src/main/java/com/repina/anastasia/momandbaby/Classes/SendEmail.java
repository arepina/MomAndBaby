package com.repina.anastasia.momandbaby.Classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SendEmail {

    public static void sendEmail(Context context, int length) {
        try {
            String fileName = "testFileName.txt";
            File root = new File(Environment.getExternalStorageDirectory(), "reportsDir");
            if (!root.exists()) root.mkdirs();
            File gpxfile = new File(root, fileName);
            FileWriter writer = new FileWriter(gpxfile);

            String text = "";

            writer.append(text);
            writer.flush();
            writer.close();

            Uri path = Uri.fromFile(gpxfile);

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"anastasiya.repina2012@yandex.ru"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Test subject");
            i.putExtra(Intent.EXTRA_TEXT, "This is the body of the email");
            i.putExtra(Intent.EXTRA_STREAM, path);

            context.startActivity(Intent.createChooser(i, "Send email..."));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
