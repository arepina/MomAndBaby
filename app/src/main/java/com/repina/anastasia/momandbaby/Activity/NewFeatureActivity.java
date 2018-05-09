package com.repina.anastasia.momandbaby.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.Other;
import com.repina.anastasia.momandbaby.Fragment.FragmentSettings;
import com.repina.anastasia.momandbaby.Helpers.GoogleCalendar;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.SendEmail;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
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
import java.util.Objects;

import static com.repina.anastasia.momandbaby.Activity.TeethActivity.getMonthBetween;
import static com.repina.anastasia.momandbaby.Helpers.FormattedDate.getFormattedDate;

/**
 * New feature
 */
public class NewFeatureActivity extends AppCompatActivity {

    private String[] features;
    private String featureName;

    private String fullDate;
    private Calendar dateAndTime;

    private EditText date;
    private EditText dataValue1;
    private EditText dataValue2;
    private EditText dataValue3;
    private TimePicker picker;
    private TimePicker pickerFrom;
    private TimePicker pickerTo;
    private RatingBar ratingBar;
    private Spinner vaccinationsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feature);

        featureName = Objects.requireNonNull(getIntent().getExtras()).getString("data");
        features = getResources().getStringArray(R.array.parametersBaby);

        dataValue1 = findViewById(R.id.dataValue1);
        dataValue2 = findViewById(R.id.dataValue2);
        dataValue3 = findViewById(R.id.dataValue3);
        picker = findViewById(R.id.dataValue4);
        pickerFrom = findViewById(R.id.fromPicker);
        pickerTo = findViewById(R.id.toPicker);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);
        vaccinationsData = findViewById(R.id.vaccinationsData);

        dateAndTime = Calendar.getInstance();

        date = findViewById(R.id.date);
        date.setText(getFormattedDate(dateAndTime));
        fullDate = getFormattedDate(dateAndTime);
        picker.setIs24HourView(true);
        picker.setHour(0);
        picker.setMinute(0);
        picker.setIs24HourView(true);
        pickerFrom.setHour(0);
        pickerFrom.setMinute(0);
        pickerFrom.setIs24HourView(true);
        pickerTo.setHour(0);
        pickerTo.setMinute(1);
        pickerTo.setIs24HourView(true);
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
                                date.setText(getFormattedDate(dateAndTime));
                                fullDate = getFormattedDate(dateAndTime);
                            }
                        }, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button addData = findViewById(R.id.addData);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {
                    addNewValueToFirebase();
                    finish();//back to choosing
                }
            }
        });

        changeLayout(featureName);
    }

    /**
     * Add new value to Firebase
     */
    private void addNewValueToFirebase() {
        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();
        DatabaseReference databaseReference;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String babyId = sp.getString(SharedConstants.BABY_ID_KEY, "");
        String currentDate = fullDate;
        if (featureName.equals(features[0])) {
            if (dataValue1.getText().length() != 0) {
                Metrics m = new Metrics(babyId, 0, Double.parseDouble(dataValue1.getText().toString()), currentDate); // no weight
                databaseReference = database.getReference().child(DatabaseNames.METRICS);
                checkIMT(0, Double.parseDouble(dataValue1.getText().toString()), database);
                databaseReference.push().setValue(m);
            } else
                NotificationsShow.showToast(getApplicationContext(), getString(R.string.add_any_data));
        }
        if (featureName.equals(features[1])) {
            if (dataValue1.getText().length() != 0) {
                Metrics m = new Metrics(babyId, Double.parseDouble(dataValue1.getText().toString()), 0, currentDate); // no height
                databaseReference = database.getReference().child(DatabaseNames.METRICS);
                checkIMT(Double.parseDouble(dataValue1.getText().toString()), 0, database);
                databaseReference.push().setValue(m);
            } else
                NotificationsShow.showToast(getApplicationContext(), getString(R.string.add_any_data));
        }
        if (featureName.equals(features[2])) {
            Stool s = new Stool(babyId, currentDate, dataValue3.getText().toString().replace("\n", " "), ratingBar.getNumStars());
            databaseReference = database.getReference().child(DatabaseNames.STOOL);
            databaseReference.push().setValue(s);
        }
        if (featureName.equals(features[3])) {
            String vaccinationName = vaccinationsData.getSelectedItem().toString();
            Vaccination v = new Vaccination(babyId, currentDate, vaccinationName, dataValue2.getText().toString().replace("\n", " "));
            databaseReference = database.getReference().child(DatabaseNames.VACCINATION);
            databaseReference.push().setValue(v);
        }
        if (featureName.equals(features[4])) {
            if (dataValue1.getText().length() != 0) {
                String symptoms = dataValue2.getText().toString().replace("\n", " ");
                String pills = dataValue3.getText().toString().replace("\n", " ");
                double temperature = Double.parseDouble(dataValue1.getText().toString());
                Illness i = new Illness(babyId, currentDate, symptoms, pills, temperature);
                databaseReference = database.getReference().child(DatabaseNames.ILLNESS);
                databaseReference.push().setValue(i);
            } else
                NotificationsShow.showToast(getApplicationContext(), getString(R.string.add_any_data));
        }
        if (featureName.equals(features[5])) {
            Food f = new Food(babyId, currentDate, dataValue3.getText().toString().replace("\n", " "), ratingBar.getNumStars());
            databaseReference = database.getReference().child(DatabaseNames.FOOD);
            databaseReference.push().setValue(f);
        }
        if (featureName.equals(features[6])) {
            double length = picker.getHour() * 60 + picker.getMinute();
            if (length != 0) {
                //double length = Double.parseDouble(dataValue1.getText().toString());
                Outdoor o = new Outdoor(babyId, currentDate, length);
                databaseReference = database.getReference().child(DatabaseNames.OUTDOOR);
                databaseReference.push().setValue(o);
            } else
                NotificationsShow.showToast(getApplicationContext(), getString(R.string.add_any_data));
        }
        if (featureName.equals(features[7])) {
            double length = picker.getHour() * 60 + picker.getMinute();
            if (length != 0) {
                //double length = Double.parseDouble(dataValue1.getText().toString());
                Sleep s = new Sleep(babyId, currentDate, length);
                databaseReference = database.getReference().child(DatabaseNames.SLEEP);
                databaseReference.push().setValue(s);
            } else
                NotificationsShow.showToast(getApplicationContext(), getString(R.string.add_any_data));
        }
        if (featureName.equals(features[8])) {
            if (dataValue1.getText().length() != 0) {
                String desc = dataValue1.getText().toString().replace("\n", " ");
                TimePicker fromPicker = findViewById(R.id.fromPicker);
                int hour = fromPicker.getHour();
                int minute = fromPicker.getMinute();
                TimePicker toPicker = findViewById(R.id.toPicker);
                int hourTo = toPicker.getHour();
                int minuteTo = toPicker.getMinute();
                if (hour > hourTo || (hour == hourTo && minute > minuteTo))
                    NotificationsShow.showToast(getApplicationContext(), getString(R.string.incorrect_dates));
                else {
                    Other o = new Other(babyId, currentDate, desc);
                    databaseReference = database.getReference().child(DatabaseNames.OTHER);
                    databaseReference.push().setValue(o);
                    Calendar tempdateFrom = Calendar.getInstance();
                    tempdateFrom.setTime(dateAndTime.getTime());
                    tempdateFrom.set(Calendar.HOUR_OF_DAY, hour);
                    tempdateFrom.set(Calendar.MINUTE, minute);
                    Calendar tempdateTo = Calendar.getInstance();
                    tempdateTo.setTime(dateAndTime.getTime());
                    tempdateTo.set(Calendar.HOUR_OF_DAY, hourTo);
                    tempdateTo.set(Calendar.MINUTE, minuteTo);
                    long begin = tempdateFrom.getTimeInMillis();
                    long end = tempdateTo.getTimeInMillis();
                    addNewValueToGoogleCalendar(desc, begin, end);
                }
            } else
                NotificationsShow.showToast(getApplicationContext(), getString(R.string.add_any_data));
        }
    }

    private void checkIMT(final double weight, final double height, FirebaseDatabase database) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String gender = sp.getString(SharedConstants.BABY_GENDER_KEY, "");
        String babyID = sp.getString(SharedConstants.BABY_ID_KEY, "");
        final DatabaseReference databaseReference = database.getReference().child(DatabaseNames.METRICS);
        databaseReference.orderByChild("babyId")
                .equalTo(babyID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot latest = null;
                            DataSnapshot beforeLatestWeight = null, beforeLatestHeight = null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (latest != null) {
                                    if (latest.getValue(Metrics.class).getHeight() == 0)
                                        beforeLatestWeight = latest;
                                    else
                                        beforeLatestHeight = latest;
                                }
                                latest = snapshot;
                            }
                            if (beforeLatestHeight != null & height == 0 || beforeLatestWeight != null & weight == 0) // more then 1 entry in DB
                            {
                                double imt;
                                if ((int) weight == 0) {
                                    imt = beforeLatestWeight.getValue(Metrics.class).getWeight() / Math.pow(height / 100, 2);
                                } else {
                                    imt = weight / Math.pow(beforeLatestHeight.getValue(Metrics.class).getHeight() / 100, 2);
                                }
                                int months = getMonthBetween(getApplicationContext());
                                if (gender.equals(getString(R.string.boy_eng))) {
                                    String[] imtBoy = getResources().getStringArray(R.array.imtBoy);
                                    double min = Double.parseDouble(imtBoy[months - 1]) - 1.5;
                                    double max = Double.parseDouble(imtBoy[months - 1]) + 1.5;
                                    if (min > imt || imt > max) {
                                        NotificationsShow.showToast(getApplicationContext(), R.string.bad_imt);
                                    }
                                } else {
                                    String[] imtGirl = getResources().getStringArray(R.array.imtGirl);
                                    double min = Double.parseDouble(imtGirl[months - 1]) - 1.5;
                                    double max = Double.parseDouble(imtGirl[months - 1]) + 1.5;
                                    if (min > imt || imt > max) {
                                        NotificationsShow.showToast(getApplicationContext(), R.string.bad_imt);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        NotificationsShow.showToast(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }

    /**
     * Change the layout depends on the feature type
     *
     * @param data feature type
     */
    private void changeLayout(String data) {
        TextView dataName1 = findViewById(R.id.dataName1);
        TextView dataName2 = findViewById(R.id.dataName2);
        TextView dataName3 = findViewById(R.id.dataName3);
        if (data.equals(features[0])) {
            dataName1.setText(String.format(getString(R.string.add_new_data), getString(R.string.height)));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[1])) {
            dataName1.setText(String.format(getString(R.string.add_new_data), getString(R.string.weight)));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[2])) {
            dataName2.setText(String.format(getString(R.string.rateValue), getString(R.string.diapers)));
            dataName2.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            dataName3.setText(R.string.сomment);
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
            dataName1.setText(String.format(getString(R.string.add_new_data), getString(R.string.temperature)));
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
            dataName2.setText(String.format(getString(R.string.rateValue), getString(R.string.food)));
            dataName2.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            dataName3.setText(R.string.сomment);
            dataName3.setVisibility(View.VISIBLE);
            dataValue3.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[6])) {
            dataName1.setText(String.format(getString(R.string.add_new_data), getString(R.string.outdoor_duration)));
            dataName1.setVisibility(View.VISIBLE);
            picker.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[7])) {
            dataName1.setText(String.format(getString(R.string.add_new_data), getString(R.string.sleep_duration)));
            dataName1.setVisibility(View.VISIBLE);
            picker.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[8])) {
            dataName1.setText(getString(R.string.add_new_other));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
            View timepicker = findViewById(R.id.timepicker);
            timepicker.setVisibility(View.VISIBLE);
            dataValue1.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    private void addNewValueToGoogleCalendar(String desc, long begin, long end) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    getApplicationContext().checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 0);
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, 0);
                else {
                    GoogleCalendar.insertEvent(getApplicationContext(), "1", "Mom&Baby", desc, begin, end);
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}

