package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Fragment.FragmentAnalytics;
import com.repina.anastasia.momandbaby.Fragment.FragmentBaby;
import com.repina.anastasia.momandbaby.Fragment.FragmentMom;
import com.repina.anastasia.momandbaby.Fragment.FragmentSettings;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

/**
 * Tabs
 */
public class TabsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        if (ConnectionDetector.isConnected(getApplicationContext())) { // the app will not work without internet

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String babyID = sp.getString(SharedConstants.BABY_ID_KEY, "");

            if (babyID.length() == 0) { // the account was created, but the baby info was not entered
                Intent nextActivity = new Intent(this, BabyInfoActivity.class);
                startActivity(nextActivity);
                finish();
            } else {
                buildTabs();
                checkForBirthday();
            }
        } else
            finish();
    }

    /**
     * Build tabs
     */
    private void buildTabs() {
        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("Analytics").setIndicator("", null),
                FragmentAnalytics.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Baby").setIndicator("", null),
                FragmentBaby.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Mom").setIndicator("", null),
                FragmentMom.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("Settings").setIndicator("", null),
                FragmentSettings.class, null);

        setTabIcon(mTabHost, 0, R.mipmap.analytics); //for Tab 1
        setTabIcon(mTabHost, 1, R.mipmap.baby); //for Tab 2
        setTabIcon(mTabHost, 2, R.mipmap.mother); //for Tab 3
        setTabIcon(mTabHost, 3, R.mipmap.settings); //for Tab 4

        mTabHost.setCurrentTab(1);
    }

    /**
     * Set tabs icons
     *
     * @param tabHost      host tab
     * @param tabIndex     index of tab
     * @param iconResource icon source
     */
    private void setTabIcon(TabHost tabHost, int tabIndex, int iconResource) {
        ImageView tabImageView = (ImageView) tabHost.getTabWidget().getChildTabViewAt(tabIndex).findViewById(android.R.id.icon);
        tabImageView.setVisibility(View.VISIBLE);
        tabImageView.setImageResource(iconResource);
    }

    /**
     * Check if baby's birthday is today
     */
    private void checkForBirthday() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String birthday = sp.getString(SharedConstants.BABY_BIRTHDAY, "");
        Calendar today = Calendar.getInstance();
        String formattedTodayDate = FormattedDate.getFormattedDate(today);
        if (birthday.equals(formattedTodayDate)) {
            String birthdayText = getResources().getString(R.string.happy_birthday);
            String name = sp.getString(SharedConstants.BABY_NAME_KEY, "");
            String gender = sp.getString(SharedConstants.BABY_GENDER_KEY, "");
            if (gender.equals(getString(R.string.boy_eng))) birthdayText += getResources().getString(R.string.mr);
            else birthdayText += getResources().getString(R.string.mrs);
            birthdayText += " " + name + "!";
            NotificationsShow.showToast(getApplicationContext(), birthdayText);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
