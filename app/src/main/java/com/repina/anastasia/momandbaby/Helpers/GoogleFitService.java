package com.repina.anastasia.momandbaby.Helpers;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GoogleFitService extends IntentService{

    private static final String TAG = "GOOGLE-FIT-SERVICE";
    public static final String STEP_COUNT_TODAY = "action.STEP_COUNT_TODAY";
    public static final String STEP_COUNT_TODAY_RESULT = "action.STEP_COUNT_TODAY_RESULT";
    public static final String STEPS_PER_SECOND_COUNT = "action.STEPS_PER_SECOND_COUNT";
    public static final String STEPS_PER_SECOND_COUNT_RESULT = "action.STEPS_PER_SECOND_COUNT_RESULT";
    public static final String CALORIES_EXPENDED_TODAY = "action.CALORIES_EXPENDED_TODAY";
    public static final String CALORIES_EXPENDED_TODAY_RESULT = "action.CALORIES_EXPENDED_TODAY_RESULT";

    public static GoogleApiClient mClient;
    private OnDataPointListener mListener;

    public GoogleFitService() {
        super("GoogleFitService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "GoogleFitService called");

        // Initializing physical fitness client for all kind of fit data
        buildFitnessClient();
        // Connecting the physical fitness client
        mClient.connect();

        // Initiating recording of data
        initiateRecordingOfData(mClient);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent called");
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case STEPS_PER_SECOND_COUNT:
                        handleActionStepsPerSecond();
                        break;
                    case STEP_COUNT_TODAY:
                        handleActionStepCountToday();
                        break;
                    case CALORIES_EXPENDED_TODAY:
                        handleActionCaloriesExpendedToday();
                }
            }
        }
    }

    private void buildFitnessClient() {
        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.SESSIONS_API)
                    .addApi(Fitness.SENSORS_API)
                    .addApi(Fitness.RECORDING_API)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                    .addScope(new Scope(Scopes.FITNESS_BODY_READ))
                    .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ))
                    .addScope(Fitness.SCOPE_LOCATION_READ)
                    .addScope(Fitness.SCOPE_ACTIVITY_READ)
                    .addScope(Fitness.SCOPE_BODY_READ)
                    .addScope(Fitness.SCOPE_NUTRITION_READ)
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Log.i(TAG, "Connected!!!");
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .addOnConnectionFailedListener(
                            new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(ConnectionResult result) {
                                    Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                }
                            }
                    )
                    .build();
            mClient.connect();
            subscribe(mClient);
        }
    }

    private void subscribe(GoogleApiClient mClient) {
        // Initiating Step Count Delta
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected. TYPE_STEP_COUNT_DELTA");
                            } else {
                                Log.i(TAG, "Successfully subscribed! to TYPE_STEP_COUNT_DELTA");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing. TYPE_STEP_COUNT_DELTA");
                        }
                    }
                });

        // Initiating TYPE_CALORIES_EXPENDED
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_CALORIES_EXPENDED)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected. TYPE_CALORIES_EXPENDED");
                            } else {
                                Log.i(TAG, "Successfully subscribed! to TYPE_CALORIES_EXPENDED");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing. TYPE_CALORIES_EXPENDED");
                        }
                    }
                });

        // Initiating TYPE_WEIGHT
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_WEIGHT)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected. TYPE_CALORIES_EXPENDED");
                            } else {
                                Log.i(TAG, "Successfully subscribed! to TYPE_CALORIES_EXPENDED");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing. TYPE_CALORIES_EXPENDED");
                        }
                    }
                });

        // Initiating TYPE_NUTRITION
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_NUTRITION)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected. TYPE_CALORIES_EXPENDED");
                            } else {
                                Log.i(TAG, "Successfully subscribed! to TYPE_CALORIES_EXPENDED");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing. TYPE_CALORIES_EXPENDED");
                        }
                    }
                });
    }

    /**
     * Handle action STEPS_PER_SECOND_COUNT in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStepsPerSecond() {
        Log.d(TAG, "Counting steps as of now.");

        // [START find_data_sources]
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());
                            // Checking step count delta
                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA) && mListener == null) {
                                Log.i(TAG, "Data source for STEP_COUNT_DELTA found!  Registering.");
                                DataType dataType = DataType.TYPE_STEP_COUNT_DELTA;


                                // [START register_data_listener]
                                // Creating a listener for step count delta
                                mListener = new OnDataPointListener() {
                                    @Override
                                    public void onDataPoint(DataPoint dataPoint) {
                                        for (Field field : dataPoint.getDataType().getFields()) {
                                            Value val = dataPoint.getValue(field);
                                            Log.i(TAG, "Detected DataPoint field: " + field.getName());
                                            Log.i(TAG, "Detected DataPoint value: " + val);

                                            Log.d(TAG, "Broadcasting total steps for now.");
                                            // Broadcasting step count now
                                            Intent stepCountNowResultIntent =
                                                    new Intent(GoogleFitService.STEPS_PER_SECOND_COUNT)
                                                            // Puts the status into the Intent
                                                            // Put the information received instead of a hardcoded 10
                                                            .putExtra(GoogleFitService.STEPS_PER_SECOND_COUNT_RESULT, val.asInt());
                                            // Broadcasts the Intent to receivers in this app.
                                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(stepCountNowResultIntent);
                                        }
                                    }
                                };

                                Fitness.SensorsApi.add(
                                        mClient,
                                        new SensorRequest.Builder()
                                                .setDataSource(dataSource) // Optional but recommended for custom data sets.
                                                .setDataType(dataType) // Can't be omitted.
                                                .setSamplingRate(1, TimeUnit.SECONDS)
                                                .build(),
                                        mListener)
                                        .setResultCallback(new ResultCallback<Status>() {
                                            @Override
                                            public void onResult(Status status) {
                                                if (status.isSuccess()) {
                                                    Log.i(TAG, "Listener registered!");
                                                } else {
                                                    Log.i(TAG, "Listener not registered.");
                                                }
                                            }
                                        });
                                // [END register_data_listener]
                            }
                        }
                    }
                });
        // [END find_data_sources]
    }

    /**
     * Handle action STEP_COUNT_TODAY in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStepCountToday() {
        Log.d(TAG, "Counting steps for today.");
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startTime = cal.getTimeInMillis();

        final DataReadRequest stepCountTodayReadRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.HistoryApi.readData(mClient, stepCountTodayReadRequest).setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(DataReadResult dataReadResult) {
                // Getting step data for today
                Log.d(TAG, "Getting step data for today");
                DataSet stepData = dataReadResult.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

                int totalSteps = 0;

                for (DataPoint dp : stepData.getDataPoints()) {
                    for(Field field : dp.getDataType().getFields()) {
                        int steps = dp.getValue(field).asInt();

                        totalSteps += steps;

                    }
                }

                // Broadcasting total steps for today
                Log.d(TAG, "Broadcasting total steps for today.");
                Intent stepCountTodayResultIntent =
                        new Intent(GoogleFitService.STEP_COUNT_TODAY)
                                // Puts the status into the Intent
                                .putExtra(GoogleFitService.STEP_COUNT_TODAY_RESULT, totalSteps);
                Log.d(TAG, "Step count today result " + totalSteps);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(stepCountTodayResultIntent);
            }
        });
    }

    private void handleActionCaloriesExpendedToday() {
        Log.d(TAG, "Counting calories expended for today.");
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startTime = cal.getTimeInMillis();

        final DataReadRequest caloriesExpendedTodayReadRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_CALORIES_EXPENDED)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.HistoryApi.readData(mClient, caloriesExpendedTodayReadRequest).setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(DataReadResult dataReadResult) {
                // Getting calories expended for today
                Log.d(TAG, "Getting calories expended data for today");
                DataSet caloriesData = dataReadResult.getDataSet(DataType.TYPE_CALORIES_EXPENDED);

                float totalCalories = 0;

                for (DataPoint dp : caloriesData.getDataPoints()) {
                    for(Field field : dp.getDataType().getFields()) {
                        float calories = dp.getValue(field).asFloat();
                        totalCalories += calories;
                    }
                }

                totalCalories = totalCalories / 1000;
                totalCalories = (float) (Math.round(totalCalories * 100.0) / 100.0);

                // Broadcasting total miles for today
                Log.d(TAG, "Broadcasting total calories for today.");
                Intent caloriesExpendedTodayResultIntent =
                        new Intent(GoogleFitService.CALORIES_EXPENDED_TODAY)
                                // Puts the status into the Intent
                                .putExtra(GoogleFitService.CALORIES_EXPENDED_TODAY_RESULT, totalCalories);
                Log.d(TAG, "Miles count today result " + totalCalories);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(caloriesExpendedTodayResultIntent);
            }
        });
    }
}
