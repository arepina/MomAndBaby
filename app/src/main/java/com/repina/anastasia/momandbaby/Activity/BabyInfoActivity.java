package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.DataBase.Baby;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Baby info
 */
public class BabyInfoActivity extends AppCompatActivity {

    private String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_info);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);

        Button next = (Button) findViewById(R.id.nextButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {
                    // Get mom's id
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String momId = sp.getString(SharedConstants.MOM_ID_KEY, "");

                    // Read entered values
                    String name = ((EditText) findViewById(R.id.name)).getText().toString();
                    String gender;
                    if (((RadioButton) findViewById(R.id.girl)).isChecked())
                        gender = getString(R.string.girl_eng);
                    else gender = getString(R.string.boy_eng);

                    // Check the values for correctness
                    if (name.length() > 0) {
                        FirebaseConnection connection = new FirebaseConnection();
                        FirebaseDatabase database = connection.getDatabase();

                        Baby baby = new Baby(momId, name, formattedDate, gender);

                        DatabaseReference databaseReference = database.getReference().child(DatabaseNames.BABY);

                        String babyId = databaseReference.push().getKey();
                        databaseReference.child(babyId).setValue(baby);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(SharedConstants.BABY_ID_KEY, babyId);
                        editor.putString(SharedConstants.BABY_NAME_KEY, name);
                        editor.putString(SharedConstants.BABY_GENDER_KEY, gender);
                        editor.putString(SharedConstants.BABY_BIRTHDAY, formattedDate);
                        editor.apply();

                        Intent nextActivity = new Intent(getApplicationContext(), TabsActivity.class);
                        startActivity(nextActivity);
                        finish();
                    } else
                        NotificationsShow.showToast(getApplicationContext(), R.string.invalid_name);
                }
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = new GregorianCalendar();
                calendar.set(year, month, dayOfMonth);
                formattedDate = FormattedDate.getFormattedDate(calendar);
            }
        });
    }

}