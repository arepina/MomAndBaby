package com.repina.anastasia.momandbaby.Fragment;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.repina.anastasia.momandbaby.Activity.TabsActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.GoogleFitService;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Processing.StatsProcessing;
import com.repina.anastasia.momandbaby.R;

import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_CONNECTION_MESSAGE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_NOTIFY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FROM;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_DATE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_CALORIES_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_NUTRITION_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_SLEEP_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_STEPS_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_WEIGHT_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.SERVICE_REQUEST_TYPE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TO;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TYPE_GET_CALORIES_TODAY_DATA;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TYPE_GET_NUTRITION_TODAY_DATA;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TYPE_GET_SLEEP_TODAY_DATA;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TYPE_GET_STEP_TODAY_DATA;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TYPE_GET_WEIGHT_TODAY_DATA;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TYPE_REQUEST_CONNECTION;

import java.util.Calendar;

public class FragmentMom extends Fragment {

    public static GridItemArrayAdapter momArrayAdapter;
    private ListView listViewMom;
    private FloatingActionButton fab;
    private Calendar calendar;

    public final static String TAG = "GoogleFitService";
    private ConnectionResult mFitResultResolution;
    public static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    public static final int REQUEST_OAUTH = 1431;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        return initMom(inflater, container);
    }

    private View initMom(LayoutInflater inflater, ViewGroup container) {
        final View v = inflater.inflate(R.layout.fragment_mom, container, false);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setImageResource(R.mipmap.google_fit);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    authInProgress = true;
                    mFitResultResolution.startResolutionForResult(getActivity(), REQUEST_OAUTH);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            }
        });

        listViewMom = (ListView) v.findViewById(R.id.listViewMom);
        momArrayAdapter = new GridItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.need_to_sync), null, null);
        momArrayAdapter.add(item);
        listViewMom.setAdapter(momArrayAdapter);

        final TextView headerDate = (TextView) v.findViewById(R.id.headerMom);

        TextView yesterday = (TextView) v.findViewById(R.id.yesterdayMom);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    momArrayAdapter.clear();
                    calendar.add(Calendar.DATE, -1);
                    Calendar today = Calendar.getInstance();
                    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                            && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
                        headerDate.setText(R.string.today);
                    else {
                        String date = FormattedDate.getTextDate(calendar);
                        headerDate.setText(date);
                    }
                    if (Calendar.getInstance().after(calendar))
                        StatsProcessing.getMomStats(calendar, 0, getActivity(), 0);
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.google_fit_no_data), null, null);
                        momArrayAdapter.add(item);
                        listViewMom.setAdapter(momArrayAdapter);
                    }
                }
            }
        });

        TextView tomorrow = (TextView) v.findViewById(R.id.tomorrowMom);
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    momArrayAdapter.clear();
                    calendar.add(Calendar.DATE, 1);
                    Calendar today = Calendar.getInstance();
                    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                            && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
                        headerDate.setText(R.string.today);
                    else {
                        String date = FormattedDate.getTextDate(calendar);
                        headerDate.setText(date);
                    }
                    if (Calendar.getInstance().after(calendar))
                        StatsProcessing.getMomStats(calendar, 0, getActivity(), 0);
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.google_fit_no_data), null, null);
                        momArrayAdapter.add(item);
                        listViewMom.setAdapter(momArrayAdapter);
                    }
                }
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFitStatusReceiver, new IntentFilter(FIT_NOTIFY_INTENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFitDataReceiver, new IntentFilter(HISTORY_INTENT));
        requestFitConnection();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(TabsActivity.dialog != null)
            TabsActivity.dialog.dismiss();
    }

    private void requestFitConnection() {
        Intent service = new Intent(getContext(), GoogleFitService.class);
        service.putExtra(SERVICE_REQUEST_TYPE, TYPE_REQUEST_CONNECTION);
        getActivity().startService(service);
        // Load today data for mom from google fit
        momArrayAdapter.clear();
        StatsProcessing.getMomStats(calendar, 0, getActivity(), 0); // same day, all types
    }

    private BroadcastReceiver mFitStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra(FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE) &&
                    intent.hasExtra(FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE)) {
                //Recreate the connection result
                int statusCode = intent.getIntExtra(FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE, 0);
                PendingIntent pendingIntent = intent.getParcelableExtra(FIT_EXTRA_NOTIFY_FAILED_INTENT);
                ConnectionResult result = new ConnectionResult(statusCode, pendingIntent);
                Log.d(TAG, "Fit connection failed - opening connect screen");
                fitHandleFailedConnection(result);
            }
            if (intent.hasExtra(FIT_EXTRA_CONNECTION_MESSAGE)) {
                Log.d(TAG, "Fit connection successful - closing connect screen if it's open");
                fitHandleConnection();
            }
        }
    };

    private BroadcastReceiver mFitDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String date = intent.getStringExtra(HISTORY_DATE);
            if (intent.hasExtra(HISTORY_EXTRA_STEPS_TODAY)) {
                final int totalSteps = intent.getIntExtra(HISTORY_EXTRA_STEPS_TODAY, 0);
                GridItem item = new GridItem(R.mipmap.steps, "R.mipmap.steps", String.valueOf(totalSteps), date);
                momArrayAdapter.add(item);
            }
            if (intent.hasExtra(HISTORY_EXTRA_CALORIES_TODAY)) {
                final int totalCalories = (int)intent.getDoubleExtra(HISTORY_EXTRA_CALORIES_TODAY, 0);
                GridItem item = new GridItem(R.mipmap.calories, "R.mipmap.calories", String.valueOf(totalCalories), date);
                momArrayAdapter.add(item);
            }
            if (intent.hasExtra(HISTORY_EXTRA_WEIGHT_TODAY)) {
                final int totalWeight = (int)intent.getDoubleExtra(HISTORY_EXTRA_WEIGHT_TODAY, 0);
                if(totalWeight != 0) {
                    GridItem item = new GridItem(R.mipmap.weight, "R.mipmap.weight", String.valueOf(totalWeight), date);
                    momArrayAdapter.add(item);
                }
            }
            if (intent.hasExtra(HISTORY_EXTRA_NUTRITION_TODAY)) {
                final String totalNutrition = intent.getStringExtra(HISTORY_EXTRA_NUTRITION_TODAY);
                if(totalNutrition.length() != 0) {
                    GridItem item = new GridItem(R.mipmap.nutrition, "R.mipmap.nutrition", String.valueOf(totalNutrition), date);
                    momArrayAdapter.add(item);
                }
            }
            if (intent.hasExtra(HISTORY_EXTRA_SLEEP_TODAY)) {
                final String totalSleep = intent.getStringExtra(HISTORY_EXTRA_SLEEP_TODAY);
                if(totalSleep.length() != 0) {
                    GridItem item = new GridItem(R.mipmap.sleep, "R.mipmap.sleep", String.valueOf(totalSleep), date);
                    momArrayAdapter.add(item);
                }
            }
            listViewMom.setAdapter(momArrayAdapter);
        }
    };

    private void fitHandleConnection() {
        Log.i(TAG, "Fit connected");
        fab.setEnabled(false);
    }

    private void fitHandleFailedConnection(ConnectionResult result) {
        Log.i(TAG, "Activity Thread Google Fit Connection failed. Cause: " + result.toString());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an authorization dialog is displayed to the user.
        if (!authInProgress) {
            if (result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
                try {
                    Log.d(TAG, "Google Fit connection failed with OAuth failure.  Trying to ask for consent (again)");
                    result.startResolutionForResult(getActivity(), REQUEST_OAUTH);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            } else {
                Log.i(TAG, "Activity Thread Google Fit Attempting to resolve failed connection");
                mFitResultResolution = result;
                fab.setEnabled(true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fitSaveInstanceState(outState);
    }

    private void fitSaveInstanceState(Bundle outState) {
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fitActivityResult(requestCode, resultCode);
    }

    private void fitActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Fit auth completed. Asking for reconnect");
                requestFitConnection();
            } else {
                try {
                    authInProgress = true;
                    mFitResultResolution.startResolutionForResult(getActivity(), REQUEST_OAUTH);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mFitStatusReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mFitDataReceiver);
        super.onDestroy();
    }
}