package com.repina.anastasia.momandbaby.Activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.repina.anastasia.momandbaby.Classes.FirebaseConnection;
import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.Food;
import com.repina.anastasia.momandbaby.DataBase.Illness;
import com.repina.anastasia.momandbaby.DataBase.Metrics;
import com.repina.anastasia.momandbaby.DataBase.Outdoor;
import com.repina.anastasia.momandbaby.DataBase.Sleep;
import com.repina.anastasia.momandbaby.DataBase.Stool;
import com.repina.anastasia.momandbaby.DataBase.Vaccination;
import com.repina.anastasia.momandbaby.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NewDataActivity extends AppCompatActivity {

    private String[] features;
    private String data;

    private EditText date;
    private EditText dataValue1;
    private EditText dataValue2;
    private EditText dataValue3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_data);
        data = getIntent().getExtras().getString("data");
        features = getResources().getStringArray(R.array.parameters);

        dataValue1 = (EditText) findViewById(R.id.dataValue1);
        dataValue2 = (EditText) findViewById(R.id.dataValue2);
        dataValue3 = (EditText) findViewById(R.id.dataValue3);

        date = (EditText) findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar dateAndTime = Calendar.getInstance();

                new DatePickerDialog(NewDataActivity.this, R.style.Theme_AppCompat_DayNight_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateAndTime.set(Calendar.YEAR, year);
                                dateAndTime.set(Calendar.MONTH, monthOfYear);
                                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                SimpleDateFormat sd = new SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                                final Date startDate = dateAndTime.getTime();
                                String formattedDate = sd.format(startDate);
                                date.setText(formattedDate);
                            }
                        }, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button addData = (Button) findViewById(R.id.addData);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewValueToFirebase();
                finish();
            }
        });

        changeLayout(data);
    }

    private void addNewValueToFirebase() {
        FirebaseDatabase database = FirebaseConnection.getDatabase();
        DatabaseReference databaseReference;
        SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        String babyId = sp.getString(SharedConstants.BABY_ID_KEY, null);
        if (data.equals(features[0])) {
            Metrics m = new Metrics(babyId, 0, Double.parseDouble(dataValue1.getText().toString()), date.getText().toString()); // no weight
            databaseReference = database.getReference().child(DatabaseNames.METRICS);
            databaseReference.push().setValue(m);
        }
        if (data.equals(features[1])) {
            Metrics m = new Metrics(babyId, Double.parseDouble(dataValue1.getText().toString()), 0, date.getText().toString()); // no height
            databaseReference = database.getReference().child(DatabaseNames.METRICS);
            databaseReference.push().setValue(m);
        }
        if (data.equals(features[2])) {
            Stool s = new Stool();
            databaseReference = database.getReference().child(DatabaseNames.STOOL);
            databaseReference.push().setValue(s);
        }
        if (data.equals(features[3])) {
            Vaccination v = new Vaccination();
            databaseReference = database.getReference().child(DatabaseNames.VACCINATION);
            databaseReference.push().setValue(v);
        }
        if (data.equals(features[4])) {
            Illness i = new Illness();
            databaseReference = database.getReference().child(DatabaseNames.ILLNESS);
            databaseReference.push().setValue(i);
        }
        if (data.equals(features[5])) {
            Food f = new Food();
            databaseReference = database.getReference().child(DatabaseNames.FOOD);
            databaseReference.push().setValue(f);
        }
        if (data.equals(features[6])) {
            Outdoor o = new Outdoor();
            databaseReference = database.getReference().child(DatabaseNames.OUTDOOR);
            databaseReference.push().setValue(o);
        }
        if (data.equals(features[7])) {
            Sleep s = new Sleep();
            databaseReference = database.getReference().child(DatabaseNames.SLEEP);
            databaseReference.push().setValue(s);
        }
    }

    private void changeLayout(String data) {
        TextView dataName1 = (TextView) findViewById(R.id.dataName1);
        TextView dataName2 = (TextView) findViewById(R.id.dataName2);
        TextView dataName3 = (TextView) findViewById(R.id.dataName3);
        LinearLayout rateData = (LinearLayout) findViewById(R.id.rateData);
        Spinner vaccinationsData = (Spinner) findViewById(R.id.vaccinationsData);
        if (data.equals(features[0])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.height));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[1])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.weight));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[2])) {
            dataName2.setText(getString(R.string.rateValue) + " " + getString(R.string.diapers));
            dataName2.setVisibility(View.VISIBLE);
            rateData.setVisibility(View.VISIBLE);
            dataName3.setText(R.string.Comment);
            dataName3.setVisibility(View.VISIBLE);
            dataValue3.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[3])) {
            dataName1.setText(getString(R.string.vaccination));
            dataName1.setVisibility(View.VISIBLE);
            ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.vaccinations, android.R.layout.simple_spinner_item);
            vaccinationsData.setAdapter(adapter);
            vaccinationsData.setSelection(0);
            vaccinationsData.setVisibility(View.VISIBLE);
            dataName2.setText(getString(R.string.add_vaccination));
            dataName2.setVisibility(View.VISIBLE);
            dataValue2.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[4])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.temperature));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
            dataName2.setText(getString(R.string.add_symptomes));
            dataName2.setVisibility(View.VISIBLE);
            dataValue2.setVisibility(View.VISIBLE);
            dataName3.setText(getString(R.string.add_pills));
            dataName3.setVisibility(View.VISIBLE);
            dataValue3.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[5])) {
            dataName2.setText(getString(R.string.rateValue) + getString(R.string.food));
            dataName2.setVisibility(View.VISIBLE);
            rateData.setVisibility(View.VISIBLE);
            dataName3.setText(R.string.Comment);
            dataName3.setVisibility(View.VISIBLE);
            dataValue3.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[6])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.outdoor_duration));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[7])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.sleep_duration));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
        }
    }
}

