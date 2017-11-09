package com.repina.anastasia.momandbaby.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;

import com.repina.anastasia.momandbaby.DataBase.Handlers.DatabaseHandler;
import com.repina.anastasia.momandbaby.DataBase.Handlers.UserHandler;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

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
				DatabaseHandler handler = new UserHandler(getApplicationContext());
				String name = ((EditText)findViewById(R.id.name)).getText().toString();
				String gender;
				if(((RadioButton)findViewById(R.id.girl)).isChecked())
					gender = "girl";
				else
					gender = "boy";
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