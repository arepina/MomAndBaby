package com.repina.anastasia.momandbaby.Fragment;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.GoogleFitService;
import com.repina.anastasia.momandbaby.Processing.StatsProcessing;
import com.repina.anastasia.momandbaby.R;

import java.util.Calendar;

import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_CONNECTION_MESSAGE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_NOTIFY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_DATE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_CALORIES_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_NUTRITION_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_SLEEP_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_STEPS_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_WEIGHT_TODAY;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.SERVICE_REQUEST_TYPE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TYPE_REQUEST_CONNECTION;

/**
 * Mom fragment
 */
public class FragmentMom extends Fragment {

    public static GridItemArrayAdapter momArrayAdapter;
    private ListView listViewMom;
    private FloatingActionButton fab;
    private Calendar calendar;
    public static ProgressDialog dialog;

    public final static String TAG = "GoogleFitService";
    private ConnectionResult mFitResultResolution;
    public static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    public static final int REQUEST_OAUTH = 1431;

    public static boolean isActivityAlreadyCreated = false;
    public static boolean google_fit_connected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        return initMom(inflater, container);
    }

    //region Init layout

    /**
     * Initialise mom layout
     *
     * @param inflater  LayoutInflater
     * @param container ViewGroup
     * @return View
     */
    private View initMom(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_mom, container, false);

        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.fit_data_load));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        fab = getActivity().findViewById(R.id.floatingActionButton);
        fab.setImageResource(R.mipmap.google_fit);
        fab.setVisibility(View.VISIBLE);
        fab.setEnabled(true);

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

        listViewMom = v.findViewById(R.id.listViewMom);
        momArrayAdapter = new GridItemArrayAdapter(getActivity().getApplicationContext(), R.layout.custom_row);
        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.need_to_sync), null, null);
        momArrayAdapter.add(item);
        listViewMom.setAdapter(momArrayAdapter);

        initTodayAndTomorrow(v);

        if (!isActivityAlreadyCreated) { // we do need to establish the connection only once
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFitStatusReceiver, new IntentFilter(FIT_NOTIFY_INTENT));
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFitDataReceiver, new IntentFilter(HISTORY_INTENT));
        }
        if (ConnectionDetector.isConnected(getContext()))
            requestFitConnection();

        isActivityAlreadyCreated = true;

        return v;
    }

    /**
     * Initialise yesterday and tomorrow buttons
     *
     * @param v View
     */
    private void initTodayAndTomorrow(View v) {
        final TextView headerDate = v.findViewById(R.id.headerMom);

        TextView yesterday = v.findViewById(R.id.yesterdayMom);
        yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    momArrayAdapter.clear();
                    calendar.add(Calendar.DATE, -1);
                    Calendar today = Calendar.getInstance();
                    boolean isToday = false;
                    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                            && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                        headerDate.setText(R.string.today);
                        isToday = true;
                    } else {
                        String date = FormattedDate.getTextDate(calendar);
                        headerDate.setText(date);
                    }
                    if (Calendar.getInstance().after(calendar) || isToday)
                        StatsProcessing.getMomStats(calendar, 0, getActivity(), 0, FragmentMom.class.toString());
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.google_fit_no_data), null, null);
                        momArrayAdapter.add(item);
                        listViewMom.setAdapter(momArrayAdapter);
                    }
                }
            }
        });

        TextView tomorrow = v.findViewById(R.id.tomorrowMom);
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    momArrayAdapter.clear();
                    calendar.add(Calendar.DATE, 1);
                    Calendar today = Calendar.getInstance();
                    boolean isToday = false;
                    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                            && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                        headerDate.setText(R.string.today);
                        isToday = true;
                    } else {
                        String date = FormattedDate.getTextDate(calendar);
                        headerDate.setText(date);
                    }
                    if (today.after(calendar) || isToday)
                        StatsProcessing.getMomStats(calendar, 0, getActivity(), 0, FragmentMom.class.toString());
                    else {
                        GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.google_fit_no_data), null, null);
                        momArrayAdapter.add(item);
                        listViewMom.setAdapter(momArrayAdapter);
                    }
                }
            }
        });
    }

    //endregion

    //region Fit service connection

    /**
     * Request connection to GoogleFit
     */
    private void requestFitConnection() {
        Intent service = new Intent(getContext(), GoogleFitService.class);
        service.putExtra(SERVICE_REQUEST_TYPE, TYPE_REQUEST_CONNECTION);
        getActivity().startService(service);
        // Load today data for mom from google fit
        dialog.show();
        momArrayAdapter.clear();
        StatsProcessing.getMomStats(calendar, 0, getActivity(), 0, FragmentMom.class.toString()); // same day, all types
    }

    /**
     * Broadcast service status receiver
     */
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
                if (momArrayAdapter.getCount() == 0) { // do not need duplicates
                    GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.need_to_sync), null, null);
                    momArrayAdapter.add(item);
                    listViewMom.setAdapter(momArrayAdapter);
                }
                fitHandleFailedConnection(result);
            }
            if (intent.hasExtra(FIT_EXTRA_CONNECTION_MESSAGE)) {
                Log.d(TAG, "Fit connection successful - closing connect screen if it's open");
                fab.setEnabled(false);
                google_fit_connected = true;
            }
        }
    };

    /**
     * Broadcast service data receiver
     */
    private BroadcastReceiver mFitDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // Get extra data included in the Intent
                String date = intent.getStringExtra(HISTORY_DATE);
                boolean hasAnyData = false;
                if (intent.hasExtra(HISTORY_EXTRA_STEPS_TODAY)) {
                    final int totalSteps = intent.getIntExtra(HISTORY_EXTRA_STEPS_TODAY, 0);
                    if (totalSteps != 0) {
                        GridItem item = new GridItem(R.mipmap.steps, "R.mipmap.steps", String.valueOf(totalSteps), date);
                        momArrayAdapter.add(item);
                        hasAnyData = true;
                    }
                }
                if (intent.hasExtra(HISTORY_EXTRA_CALORIES_TODAY)) {
                    final int totalCalories = (int) intent.getDoubleExtra(HISTORY_EXTRA_CALORIES_TODAY, 0);
                    if (totalCalories != 0) {
                        GridItem item = new GridItem(R.mipmap.calories, "R.mipmap.calories", String.valueOf(totalCalories), date);
                        momArrayAdapter.add(item);
                        hasAnyData = true;
                    }
                }
                if (intent.hasExtra(HISTORY_EXTRA_WEIGHT_TODAY)) {
                    final int totalWeight = (int) intent.getDoubleExtra(HISTORY_EXTRA_WEIGHT_TODAY, 0);
                    if (totalWeight != 0) {
                        GridItem item = new GridItem(R.mipmap.weight, "R.mipmap.weight", String.valueOf(totalWeight), date);
                        momArrayAdapter.add(item);
                        hasAnyData = true;
                    }
                }
                if (intent.hasExtra(HISTORY_EXTRA_NUTRITION_TODAY)) {
                    final String totalNutrition = intent.getStringExtra(HISTORY_EXTRA_NUTRITION_TODAY);
                    if (totalNutrition.length() != 0) {
                        GridItem item = new GridItem(R.mipmap.nutrition, "R.mipmap.nutrition", String.valueOf(totalNutrition), date);
                        momArrayAdapter.add(item);
                        hasAnyData = true;
                    }
                }
                if (intent.hasExtra(HISTORY_EXTRA_SLEEP_TODAY)) {
                    final String totalSleep = intent.getStringExtra(HISTORY_EXTRA_SLEEP_TODAY);
                    String temp = totalSleep.replace(getString(R.string.sleep_str), "").replace(getString(R.string.hours_str), "").replace(",", ".");
                    double hours = Double.parseDouble(temp);
                    if (totalSleep.length() != 0 && hours != 0) {
                        GridItem item = new GridItem(R.mipmap.sleep, "R.mipmap.sleep", String.valueOf(totalSleep), date);
                        momArrayAdapter.add(item);
                        hasAnyData = true;
                    }
                }
                GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", getResources().getString(R.string.no_google_fit_data), null, null);
                if (!hasAnyData && !momArrayAdapter.hasEmptyItem())
                    momArrayAdapter.add(item);
                if (hasAnyData)
                    momArrayAdapter.removeEmptyItem();
                listViewMom.setAdapter(momArrayAdapter);
            } catch (Exception ignored) {
            }
        }
    };

    /**
     * Connection errors handler
     *
     * @param result connection result
     */
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
        if (dialog != null)
            dialog.dismiss();
    }

    /**
     * Save the state of an instance
     *
     * @param outState state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fitActivityResult(requestCode, resultCode);
        if (dialog != null)
            dialog.dismiss();
    }

    /**
     * Process the result of fit activity connection
     *
     * @param requestCode code
     * @param resultCode  result
     */
    private void fitActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "Fit auth completed. Asking for reconnect");
                requestFitConnection();
            } else {
                try {
                    if (mFitResultResolution != null) {
                        authInProgress = true;
                        mFitResultResolution.startResolutionForResult(getActivity(), REQUEST_OAUTH);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (dialog != null)
            dialog.dismiss();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mFitStatusReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mFitDataReceiver);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null)
            dialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (authInProgress) {
            requestFitConnection();
            authInProgress = false;
            fab.setEnabled(false);
        }
    }


    //endregion
}