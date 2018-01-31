package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TabHost;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Fragment.FragmentAnalytics;
import com.repina.anastasia.momandbaby.Fragment.FragmentBaby;
import com.repina.anastasia.momandbaby.Fragment.FragmentMom;
import com.repina.anastasia.momandbaby.Fragment.FragmentSettings;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.SendEmail;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class TabsActivity extends AppCompatActivity {

    public static GoogleApiClient mClient;
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        if (ConnectionDetector.isConnected(getApplicationContext())) { // the app will not work without internet
            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getApplicationContext()))) {
                GoogleSignIn.requestPermissions(
                        TabsActivity.this,
                        GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                        GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
            } else
                buildFitnessClient();

            SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
            String babyID = sp.getString(SharedConstants.BABY_ID_KEY, "");

            if (babyID.length() == 0) { // the account was created, but the baby info was not entered
                Intent nextActivity = new Intent(this, BabyInfoActivity.class);
                startActivity(nextActivity);
                finish();
            } else {
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
            NotificationsShow.showToast(getApplicationContext(), birthdayText);
        }
    }

    private void buildFitnessClient() {
        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(TabsActivity.this)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.SESSIONS_API)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    .addScope(Fitness.SCOPE_LOCATION_READ)
                    .addScope(Fitness.SCOPE_ACTIVITY_READ)
                    .addScope(Fitness.SCOPE_BODY_READ)
                    .addScope(Fitness.SCOPE_NUTRITION_READ)
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Log.i(TAG, "Connected!!!");
                                    // Now you can make calls to the Fitness APIs.
                                    //new VerifyDataTask().execute(null, null, null);
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.i(TAG,
                                                "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .enableAutoManage(TabsActivity.this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.i(TAG, "Google Play services connection failed. Cause: " +
                                    result.toString());
                        }
                    })
                    .build();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClient();
    }

    @Override
    public void onPause() {
        super.onPause();
        mClient.stopAutoManage(TabsActivity.this);
        mClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mClient != null && mClient.isConnected()) {
            mClient.stopAutoManage(TabsActivity.this);
            mClient.disconnect();
        }
    }
}
