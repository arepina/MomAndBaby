package com.repina.anastasia.momandbaby.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

public class MainActivity123 extends AppCompatActivity {


    Button btnCreateFile, btnSendFile;
    String FILENAME = "TestFile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main123);

        btnCreateFile = (Button) findViewById(R.id.btnCreateFile);
        btnSendFile = (Button) findViewById(R.id.button2);

        btnCreateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createTestFile(getBaseContext());
            }
        });

        btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File gpxfile = getFile();
                Uri path = Uri.fromFile(gpxfile);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                Context context = v.getContext();
                String email = "prostorepa@yandex.ru";
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.report_from) + " " + 0 + " " + context.getString(R.string.report_to) + " " + 1);
                i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text));
                i.putExtra(Intent.EXTRA_STREAM, path);
                context.startActivity(Intent.createChooser(i, context.getString(R.string.report_sending)));
            }
        });
    }

    /*
    * Create Test file in internal storage.
    * */
    private void createTestFile(Context c) {
        try {
            FileOutputStream fos = c.openFileOutput(FILENAME, Context.MODE_APPEND);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject("Your content");
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
   * getFile function
   * Return File if file exists.
   * */
    private File getFile() {
        return new File(getFilesDir() + "/" + FILENAME);
    }
}