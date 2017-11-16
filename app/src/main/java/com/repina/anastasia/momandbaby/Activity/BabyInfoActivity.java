package com.repina.anastasia.momandbaby.Activity;

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
import com.repina.anastasia.momandbaby.DataBase.User;
import com.repina.anastasia.momandbaby.FirebaseConnection;
import com.repina.anastasia.momandbaby.Locale;
import com.repina.anastasia.momandbaby.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BabyInfoActivity extends AppCompatActivity {

	private String formattedDate;
	private Calendar calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_info);

		CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);

		calendar = new GregorianCalendar();

		Button next = (Button)findViewById(R.id.nextButton);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//todo check for not entered data
				FirebaseConnection connection = new FirebaseConnection();
				final FirebaseDatabase database = connection.getDatabase();

				String name = ((EditText)findViewById(R.id.name)).getText().toString();
				String gender;
				if(((RadioButton)findViewById(R.id.girl)).isChecked())
					gender = "girl";
				else
					gender = "boy";

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getCurrentLocale(getApplicationContext()));
				String date = dateFormat.format(calendar.getTime());
				User user = new User(name, date, gender);
				DatabaseReference databaseReference = database.getReference().child("USERS");
				databaseReference.push().setValue(user);
				String key = databaseReference.getKey();

				/*ValueEventListener valueEventListener = new ValueEventListener()
				{
					@Override
					public void onDataChange(DataSnapshot dataSnapshot)
					{
						for (DataSnapshot snapshot : dataSnapshot.getChildren())
						{
							User u = snapshot.getValue(User.class);
							u.setId(snapshot.getKey());
						}
					}

					@Override
					public void onCancelled(DatabaseError databaseError)
					{
						System.out.println(databaseError.getMessage());
					}
				};
				databaseReference.addValueEventListener(valueEventListener);*/
			}
		});

		calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
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
		final SharedPreferences sp = getSharedPreferences(DotsActivity.DISPLAY_ONCE_PREFS,
				MODE_PRIVATE);
		sp.edit().putBoolean(DotsActivity.DISPLAY_ONCE_KEY, false).apply();
	}
	
}