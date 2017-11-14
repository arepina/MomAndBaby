package com.repina.anastasia.momandbaby.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.User;
import com.repina.anastasia.momandbaby.Locale;
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

		final CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);

		final Calendar calendar = Calendar.getInstance();

		Button next = (Button)findViewById(R.id.nextButton);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FirebaseDatabase database = FirebaseDatabase.getInstance();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getCurrentLocale(getApplicationContext()));
				Calendar cal = new GregorianCalendar();
				String date = dateFormat.format(cal.getTime());
				User user = new User(0, "Pasha3", cal.getTime(), "girl", "123", "not connected", 0);
				DatabaseReference databaseReference = database.getReference().child("USERS").push();
				databaseReference.setValue(user);
				String key = databaseReference.getKey();
				databaseReference = database.getReference().child("USERS1").push();
				databaseReference.setValue(user);


				DatabaseReference ref = database.getReference("momandbaby-repina/USERS");
				ref.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						User post = dataSnapshot.getValue(User.class);
						System.out.println(post);
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						System.out.println("The read failed: " + databaseError.getCode());
					}
				});
				/*DatabaseHandler handler = new UserHandler(getApplicationContext());
				String name = ((EditText)findViewById(R.id.name)).getText().toString();
				String gender;
				if(((RadioButton)findViewById(R.id.girl)).isChecked())
					gender = "girl";
				else
					gender = "boy";*/
				//String bandCode;
				//String bandStatus;
			}
		});

		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				calendar.set(year, month, dayOfMonth);
				long selectedDateInMillis = calendar.getTimeInMillis();
				formattedDate = Helper.dateFormat.format(selectedDateInMillis);
			}
		});
	}

	/**
	 * Clears the shared preferences flag which prevents the introduction from being shown twice.
	 */
	private void allowIntroductionToShowAgain() {
		final SharedPreferences sp = getSharedPreferences(DotsActivity.DISPLAY_ONCE_PREFS,
				MODE_PRIVATE);
		sp.edit().putBoolean(DotsActivity.DISPLAY_ONCE_KEY, false).apply();
	}
	
}