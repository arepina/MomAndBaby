package com.repina.anastasia.momandbaby.Processing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * File processing
 */
public class FileProcessing {

    /**
     * Send report via email
     *
     * @param report   text of report
     * @param context  context
     * @param start    start date
     * @param finalEnd end date
     */
    public static void sendFile(String report, Context context, String start, String finalEnd) {
        String fileName = context.getString(R.string.report_from) + " " + start + " " + context.getString(R.string.report_to) + " " + finalEnd + ".txt";
        try {
            File f = createFile(fileName, report);
            if (f != null) {
                Uri path = Uri.fromFile(f);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                String email = sp.getString(SharedConstants.MOM_EMAIL, "");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.report_from) + " " + start + " " + context.getString(R.string.report_to) + " " + finalEnd);
                i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text));
                i.putExtra(Intent.EXTRA_STREAM, path);
                context.startActivity(Intent.createChooser(i, context.getString(R.string.report_sending)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else
                NotificationsShow.showToast(context, context.getString(R.string.report_error));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create file
     *
     * @param name    file name
     * @param content file content
     * @return created file
     * @throws IOException
     */
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
