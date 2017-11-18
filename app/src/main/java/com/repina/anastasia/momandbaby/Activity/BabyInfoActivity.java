package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.Classes.ToastShow;
import com.repina.anastasia.momandbaby.DataBase.Baby;
import com.repina.anastasia.momandbaby.Classes.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BabyInfoActivity extends AppCompatActivity {

	private String formattedDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_info);

		CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);

		Button next = (Button)findViewById(R.id.nextButton);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Get mom's id
				SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
				String momId = sp.getString(SharedConstants.MOM_ID_KEY, "");

				// Read entered values
				String name = ((EditText)findViewById(R.id.name)).getText().toString();
				String gender;
				if(((RadioButton)findViewById(R.id.girl)).isChecked()) gender = "girl";
				else gender = "boy";

				// Check the values for correctness
				if(name.length() > 0) {
					FirebaseConnection connection = new FirebaseConnection();
					FirebaseDatabase database = connection.getDatabase();

					Baby baby = new Baby(momId, name, formattedDate, gender);

					DatabaseReference databaseReference = database.getReference().child(DatabaseNames.BABY);
					databaseReference.push().setValue(baby);
					String babyId = databaseReference.getKey();

					SharedPreferences.Editor editor = sp.edit();
					editor.putString(SharedConstants.BABY_ID_KEY, babyId);
					editor.putString(SharedConstants.BABY_NAME_KEY, name);
					editor.putString(SharedConstants.BABY_GENDER_KEY, gender);
					editor.apply();

					Intent nextActivity = new Intent(getApplicationContext(), MiBandActivity.class);
					startActivity(nextActivity);
					finish();
				}
				else
					ToastShow.show(getApplicationContext(), R.string.invalid_name);
			}
		});

		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				Calendar calendar = new GregorianCalendar();
				calendar.set(year, month, dayOfMonth);
				long selectedDateInMillis = calendar.getTimeInMillis();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
				formattedDate = dateFormat.format(selectedDateInMillis);
			}
		});
	}

	/**
	 * Clears the shared preferences flag which prevents the introduction from being shown twice.
	 */
	private void allowIntroductionToShowAgain() {
		//todo Delete or change later
		final SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
		sp.edit().putBoolean(SharedConstants.DISPLAY_ONCE_KEY, false).apply();
	}
	
}