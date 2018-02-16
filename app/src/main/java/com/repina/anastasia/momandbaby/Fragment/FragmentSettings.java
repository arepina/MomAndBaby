package com.repina.anastasia.momandbaby.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.repina.anastasia.momandbaby.Activity.AppInfoActivity;
import com.repina.anastasia.momandbaby.Activity.SignupActivity;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.GoogleFitService;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.SendEmail;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.Processing.TextProcessing;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static com.repina.anastasia.momandbaby.Fragment.FragmentMom.AUTH_PENDING;
import static com.repina.anastasia.momandbaby.Fragment.FragmentMom.REQUEST_OAUTH;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_CONNECTION_MESSAGE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_NOTIFY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_AGGREGATED;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.SERVICE_REQUEST_TYPE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TYPE_REQUEST_CONNECTION;

/**
 * Settings fragment
 */
public class FragmentSettings extends Fragment {

    private ConnectionResult mFitResultResolution;
    private boolean authInProgress = false;
    private Calendar from;
    private Calendar to;

    public static boolean isActivityAlreadyCreated = false;
    public static ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initSettings(inflater, container);
    }

    //region Init layout

    /**
     * Initialise settings layout
     *
     * @param inflater  LayoutInflater
     * @param container ViewGroup
     * @return View
     */
    private View initSettings(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.INVISIBLE);

        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.fit_data_load));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        Button exit = (Button) v.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = v.getContext().getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SharedConstants.MOM_ID_KEY, null);
                editor.putString(SharedConstants.MOM_NAME_KEY, null);
                editor.apply();

                Intent nextActivity = new Intent(v.getContext(), SignupActivity.class);
                startActivity(nextActivity);
                getActivity().finish();
            }
        });

        Button feedback = (Button) v.findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Mom&Baby");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                intent.setData(Uri.parse("mailto:" + getString(R.string.my_email)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Button appInfo = (Button) v.findViewById(R.id.appInfo);
        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(v.getContext(), AppInfoActivity.class);
                startActivity(nextActivity);
            }
        });

        Button sendReportBaby = (Button) v.findViewById(R.id.send_report_baby);
        sendReportBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    else
                        showAlertDialog(true);
                }
            }
        });

        Button sendReportMom = (Button) v.findViewById(R.id.send_report_mom);
        sendReportMom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getContext()))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    else
                        showAlertDialog(false);
            }
        });


        if (!isActivityAlreadyCreated) { // we do need to establish the connection only once
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFitStatusReceiver, new IntentFilter(FIT_NOTIFY_INTENT));
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFitDataReceiver, new IntentFilter(HISTORY_INTENT));
        }
        requestFitConnection();

        isActivityAlreadyCreated = true;

        return v;
    }

    /**
     * Show alert dialog
     *
     * @param whoFlag mom or baby
     */
    public void showAlertDialog(final boolean whoFlag) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        builder.setView(view);
        final AlertDialog alert = builder.create();
        Button day = (Button) view.findViewById(R.id.day);
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = Calendar.getInstance();
                to = Calendar.getInstance();
                to.setTime(from.getTime());
                alert.cancel();
                dialog.show();
                SendEmail.createEmail(getActivity().getApplicationContext(), 0, from, to,
                        whoFlag, getActivity(), FragmentSettings.class.toString());
            }
        });
        Button week = (Button) view.findViewById(R.id.week);
        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = Calendar.getInstance();
                to = Calendar.getInstance();
                to.setTime(from.getTime());
                from.add(Calendar.WEEK_OF_YEAR, -1);
                alert.cancel();
                dialog.show();
                SendEmail.createEmail(getActivity().getApplicationContext(), 1, from, to,
                        whoFlag, getActivity(), FragmentSettings.class.toString());
            }
        });
        Button month = (Button) view.findViewById(R.id.month);
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = Calendar.getInstance();
                to = Calendar.getInstance();
                to.setTime(from.getTime());
                from.add(Calendar.MONTH, -1);
                alert.cancel();
                dialog.show();
                SendEmail.createEmail(getActivity().getApplicationContext(), 2, from, to,
                        whoFlag, getActivity(), FragmentSettings.class.toString());
            }
        });
        Button custom = (Button) view.findViewById(R.id.custom);
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder calendarBuilder = new AlertDialog.Builder(getActivity());
                final View view = getActivity().getLayoutInflater().inflate(R.layout.custom_calendar, null);
                final AlertDialog calendarAlert = calendarBuilder.setView(view).create();
                calendarAlert.show();
                Button submit = (Button) view.findViewById(R.id.submit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePicker fromPicker = (DatePicker) view.findViewById(R.id.from);
                        from = Calendar.getInstance();
                        from.set(Calendar.YEAR, fromPicker.getYear());
                        from.set(Calendar.MONTH, fromPicker.getMonth());
                        from.set(Calendar.DATE, fromPicker.getDayOfMonth());
                        DatePicker toPicker = (DatePicker) view.findViewById(R.id.to);
                        to = Calendar.getInstance();
                        to.set(Calendar.YEAR, toPicker.getYear());
                        to.set(Calendar.MONTH, toPicker.getMonth());
                        to.set(Calendar.DATE, toPicker.getDayOfMonth());
                        dialog.show();
                        if (from.after(to) || Calendar.getInstance().before(to)) {
                            NotificationsShow.showToast(getActivity().getApplicationContext(), getString(R.string.incorrect_dates));
                            dialog.dismiss();
                        } else {
                            calendarAlert.cancel();
                            alert.cancel();
                            SendEmail.createEmail(getActivity().getApplicationContext(), 3, from, to,
                                    whoFlag, getActivity(), FragmentSettings.class.toString());
                        }
                    }
                });
            }
        });
        alert.show();
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
                Log.d(FragmentMom.TAG, "Fit connection failed - opening connect screen");
                fitHandleFailedConnection(result);
            }
            if (intent.hasExtra(FIT_EXTRA_CONNECTION_MESSAGE)) {
                Log.d(FragmentMom.TAG, "Fit connection successful - closing connect screen if it's open");
            }
        }
    };

    /**
     * Broadcast service data receiver
     */
    private BroadcastReceiver mFitDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra(HISTORY_EXTRA_AGGREGATED)) {
                ArrayList<Pair<DataType, Pair<String, String>>> sumData = (ArrayList<Pair<DataType, Pair<String, String>>>) intent.getSerializableExtra(HISTORY_EXTRA_AGGREGATED);
                String fromStr = FormattedDate.getFormattedDate(from);
                String toStr = FormattedDate.getFormattedDate(to);
                TextProcessing.formMomReport(sumData, context, fromStr, toStr);
            }
            if (dialog != null)
                dialog.dismiss();
        }
    };

    /**
     * Connection errors handler
     *
     * @param result connection result
     */
    private void fitHandleFailedConnection(ConnectionResult result) {
        Log.i(FragmentMom.TAG, "Activity Thread Google Fit Connection failed. Cause: " + result.toString());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(), 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an authorization dialog is displayed to the user.
        if (!authInProgress) {
            if (result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
                try {
                    Log.d(FragmentMom.TAG, "Google Fit connection failed with OAuth failure.  Trying to ask for consent (again)");
                    result.startResolutionForResult(getActivity(), REQUEST_OAUTH);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(FragmentMom.TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            } else {
                Log.i(FragmentMom.TAG, "Activity Thread Google Fit Attempting to resolve failed connection");
                mFitResultResolution = result;
            }
        }
    }

    /**
     * Save the state of an instance
     *
     * @param outState state
     */
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
                Log.d(FragmentMom.TAG, "Fit auth completed. Asking for reconnect");
                requestFitConnection();
            } else {
                try {
                    authInProgress = true;
                    mFitResultResolution.startResolutionForResult(getActivity(), REQUEST_OAUTH);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(FragmentMom.TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null)
            dialog.dismiss();
    }

    //endregion
}
