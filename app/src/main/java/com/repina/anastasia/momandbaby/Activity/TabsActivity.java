package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.GoogleFit;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.Helpers.ToastShow;
import com.repina.anastasia.momandbaby.Fragment.FragmentTab;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

public class TabsActivity extends FragmentActivity {

    public static GoogleFit googleFit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        if (ConnectionDetector.isConnected(getApplicationContext())) { // the app will not work without internet

            SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
            String babyID = sp.getString(SharedConstants.BABY_ID_KEY, "");

            googleFit = new GoogleFit(this);

            if (babyID.length() == 0) { // the account was created, but the baby info was not entered
                Intent nextActivity = new Intent(this, BabyInfoActivity.class);
                startActivity(nextActivity);
                finish();
            } else {
                FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
                mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

                mTabHost.addTab(
                        mTabHost.newTabSpec("Analytics").setIndicator("", null),
                        FragmentTab.class, null);
                mTabHost.addTab(
                        mTabHost.newTabSpec("Baby").setIndicator("", null),
                        FragmentTab.class, null);
                mTabHost.addTab(
                        mTabHost.newTabSpec("Mom").setIndicator("", null),
                        FragmentTab.class, null);
                mTabHost.addTab(
                        mTabHost.newTabSpec("Settings").setIndicator("", null),
                        FragmentTab.class, null);

                setTabIcon(mTabHost, 0, R.mipmap.analytics); //for Tab 1
                setTabIcon(mTabHost, 1, R.mipmap.baby); //for Tab 2
                setTabIcon(mTabHost, 2, R.mipmap.mother); //for Tab 3
                setTabIcon(mTabHost, 3, R.mipmap.settings); //for Tab 4

                mTabHost.setCurrentTab(1);

                checkForBirthday();
            }
        } else
            finish();
    }

    private void setTabIcon(TabHost tabHost, int tabIndex, int iconResource) {
        ImageView tabImageView = (ImageView) tabHost.getTabWidget().getChildTabViewAt(tabIndex).findViewById(android.R.id.icon);
        tabImageView.setVisibility(View.VISIBLE);
        tabImageView.setImageResource(iconResource);
    }

    private void checkForBirthday() {
        SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        String birthday = sp.getString(SharedConstants.BABY_BIRTHDAY, "");
        Calendar today = Calendar.getInstance();
        String formattedTodayDate = FormattedDate.getFormattedDateWithoutTime(today);
        if (birthday.equals(formattedTodayDate)) {
            String birthdayText = getResources().getString(R.string.happy_birthday);
            String name = sp.getString(SharedConstants.BABY_NAME_KEY, "");
            String gender = sp.getString(SharedConstants.BABY_GENDER_KEY, "");
            if (gender.equals("boy")) birthdayText += getResources().getString(R.string.mr);
            else birthdayText += getResources().getString(R.string.mrs);
            birthdayText += " " + name + "!";
            ToastShow.show(getApplicationContext(), birthdayText);
        }
    }
}
