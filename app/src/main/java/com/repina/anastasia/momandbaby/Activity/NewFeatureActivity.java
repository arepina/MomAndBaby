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
import android.widget.RatingBar;
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

import java.util.Calendar;

import static com.repina.anastasia.momandbaby.Classes.FormattedDate.getFormattedDateWithTime;
import static com.repina.anastasia.momandbaby.Classes.FormattedDate.getFormattedDateWithoutTime;

public class NewFeatureActivity extends AppCompatActivity {

    private String[] features;
    private String featureName;

    private String fullDate;

    private EditText date;
    private EditText dataValue1;
    private EditText dataValue2;
    private EditText dataValue3;
    private RatingBar ratingBar;
    private Spinner vaccinationsData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feature);
        featureName = getIntent().getExtras().getString("data");
        features = getResources().getStringArray(R.array.parameters);

        dataValue1 = (EditText) findViewById(R.id.dataValue1);
        dataValue2 = (EditText) findViewById(R.id.dataValue2);
        dataValue3 = (EditText) findViewById(R.id.dataValue3);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        vaccinationsData = (Spinner) findViewById(R.id.vaccinationsData);

        final Calendar dateAndTime = Calendar.getInstance();

        date = (EditText) findViewById(R.id.date);
        date.setText(getFormattedDateWithoutTime(dateAndTime));
        fullDate = getFormattedDateWithTime(dateAndTime);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewFeatureActivity.this, R.style.Theme_AppCompat_DayNight_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateAndTime.set(Calendar.YEAR, year);
                                dateAndTime.set(Calendar.MONTH, monthOfYear);
                                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                date.setText(getFormattedDateWithoutTime(dateAndTime));
                                fullDate = getFormattedDateWithTime(dateAndTime);
                            }
                        }, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button addData = (Button) findViewById(R.id.addData);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewValueToFirebase();
                finish();//back to choosing
            }
        });

        changeLayout(featureName);
    }

    private void addNewValueToFirebase() {
        FirebaseDatabase database = FirebaseConnection.getDatabase();
        DatabaseReference databaseReference;
        SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        String babyId = sp.getString(SharedConstants.BABY_ID_KEY, null);
        String currentDate = fullDate;
        if (featureName.equals(features[0])) {
            Metrics m = new Metrics(babyId, 0, Double.parseDouble(dataValue1.getText().toString()), currentDate); // no weight
            databaseReference = database.getReference().child(DatabaseNames.METRICS);
            databaseReference.push().setValue(m);
        }
        if (featureName.equals(features[1])) {
            Metrics m = new Metrics(babyId, Double.parseDouble(dataValue1.getText().toString()), 0, currentDate); // no height
            databaseReference = database.getReference().child(DatabaseNames.METRICS);
            databaseReference.push().setValue(m);
        }
        if (featureName.equals(features[2])) {
            Stool s = new Stool(babyId, currentDate, dataValue3.getText().toString(), ratingBar.getNumStars());
            databaseReference = database.getReference().child(DatabaseNames.STOOL);
            databaseReference.push().setValue(s);
        }
        if (featureName.equals(features[3])) {
            String vaccinationName = vaccinationsData.getSelectedItem().toString();
            Vaccination v = new Vaccination(babyId, currentDate, vaccinationName, dataValue2.getText().toString());
            databaseReference = database.getReference().child(DatabaseNames.VACCINATION);
            databaseReference.push().setValue(v);
        }
        if (featureName.equals(features[4])) {
            String symptoms = dataValue2.getText().toString();
            String pills = dataValue3.getText().toString();
            double temperature = Double.parseDouble(dataValue1.getText().toString());
            Illness i = new Illness(babyId, currentDate,symptoms, pills, temperature);
            databaseReference = database.getReference().child(DatabaseNames.ILLNESS);
            databaseReference.push().setValue(i);
        }
        if (featureName.equals(features[5])) {
            Food f = new Food(babyId, currentDate, dataValue3.getText().toString(), ratingBar.getNumStars());
            databaseReference = database.getReference().child(DatabaseNames.FOOD);
            databaseReference.push().setValue(f);
        }
        if (featureName.equals(features[6])) {
            double length = Double.parseDouble(dataValue1.getText().toString());
            Outdoor o = new Outdoor(babyId, currentDate, length);
            databaseReference = database.getReference().child(DatabaseNames.OUTDOOR);
            databaseReference.push().setValue(o);
        }
        if (featureName.equals(features[7])) {
            double length = Double.parseDouble(dataValue1.getText().toString());
            Sleep s = new Sleep(babyId, currentDate, length);
            databaseReference = database.getReference().child(DatabaseNames.SLEEP);
            databaseReference.push().setValue(s);
        }
    }

    private void changeLayout(String data) {
        TextView dataName1 = (TextView) findViewById(R.id.dataName1);
        TextView dataName2 = (TextView) findViewById(R.id.dataName2);
        TextView dataName3 = (TextView) findViewById(R.id.dataName3);
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
            ratingBar.setVisibility(View.VISIBLE);
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
            ratingBar.setVisibility(View.VISIBLE);
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
