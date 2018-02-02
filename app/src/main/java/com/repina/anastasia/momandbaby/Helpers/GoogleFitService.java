package com.repina.anastasia.momandbaby.Helpers;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_CONNECTION_MESSAGE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_NOTIFY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FROM;
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
import static java.text.DateFormat.getTimeInstance;

public class GoogleFitService extends IntentService {

    public static final String TAG = "GoogleFitService";
    private GoogleApiClient mGoogleApiFitnessClient;
    private boolean mTryingToConnect = false;

    @Override
    public void onDestroy() {
        Log.d(TAG, "GoogleFitService destroyed");
        if (mGoogleApiFitnessClient.isConnected()) {
            Log.d(TAG, "Disconecting Google Fit.");
            mGoogleApiFitnessClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildFitnessClient();
        Log.d(TAG, "GoogleFitService created");
    }

    public GoogleFitService() {
        super("GoogleFitService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(SERVICE_REQUEST_TYPE, -1);
        if (!mGoogleApiFitnessClient.isConnected()) {
            mTryingToConnect = true;
            mGoogleApiFitnessClient.connect();//Wait until the service either connects or fails to connect
            while (mTryingToConnect) {
                try {
                    Thread.sleep(100, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        long startMill = intent.getLongExtra(FROM, -1);
        long endMill = intent.getLongExtra(TO, -1);
        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startMill);
        end.setTimeInMillis(endMill);
        if (mGoogleApiFitnessClient.isConnected()) {
            if (type == TYPE_GET_STEP_TODAY_DATA)
                getStepsToday(start, end);
            else if (type == TYPE_GET_CALORIES_TODAY_DATA)
                getCaloriesToday(start, end);
            else if (type == TYPE_GET_WEIGHT_TODAY_DATA)
                getWeightToday(start, end);
            else if (type == TYPE_GET_NUTRITION_TODAY_DATA)
                getNutritionToday(start, end);
            else if (type == TYPE_GET_SLEEP_TODAY_DATA)
                getSleepToday(start, end);
        } else
            Log.w(TAG, "Fit wasn't able to connect, so the request failed.");
    }

    private void getStepsToday(Calendar start, Calendar end) {
        long endTime = end.getTimeInMillis();
        long startTime = start.getTimeInMillis();
        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readRequest).await(1, TimeUnit.MINUTES);
        DataSet stepData = dataReadResult.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
        int totalSteps = 0;
        for (DataPoint dp : stepData.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                int steps = dp.getValue(field).asInt();
                totalSteps += steps;
            }
        }
        Intent intent = new Intent(HISTORY_INTENT);
        intent.putExtra(HISTORY_EXTRA_STEPS_TODAY, totalSteps);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void getCaloriesToday(Calendar start, Calendar end) {
        long endTime = end.getTimeInMillis();
        long startTime = start.getTimeInMillis();
        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_CALORIES_EXPENDED)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        double totalCalories = 0;
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readRequest).await(1, TimeUnit.MINUTES);
        DataSet calData = dataReadResult.getDataSet(DataType.TYPE_CALORIES_EXPENDED);
        for (DataPoint dp : calData.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                double cal = dp.getValue(field).asFloat();
                totalCalories += cal;
            }
        }
        Intent intent = new Intent(HISTORY_INTENT);
        intent.putExtra(HISTORY_EXTRA_CALORIES_TODAY, totalCalories);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void getWeightToday(Calendar start, Calendar end) {
        long endTime = end.getTimeInMillis();
        long startTime = start.getTimeInMillis();
        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .setLimit(1)
                .build();
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readRequest).await(1, TimeUnit.MINUTES);
        DataSet weightData = dataReadResult.getDataSet(DataType.TYPE_WEIGHT);
        double totalWeight = 0;
        for (DataPoint dp : weightData.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                double weight = dp.getValue(field).asFloat();
                totalWeight += weight;
            }
        }
        Intent intent = new Intent(HISTORY_INTENT);
        intent.putExtra(HISTORY_EXTRA_WEIGHT_TODAY, totalWeight);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void getNutritionToday(Calendar start, Calendar end) {
        long endTime = end.getTimeInMillis();
        long startTime = start.getTimeInMillis();
        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_NUTRITION)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .setLimit(1)
                .build();
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readRequest).await(1, TimeUnit.MINUTES);
        DataSet nutritionData = dataReadResult.getDataSet(DataType.TYPE_NUTRITION);
        StringBuilder result = new StringBuilder(0);
        for (DataPoint dp : nutritionData.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                String res = dp.getValue(field).toString().replace("{", "").replace("}", "");
                result.append(res);
            }
        }
        Intent intent = new Intent(HISTORY_INTENT);
        intent.putExtra(HISTORY_EXTRA_NUTRITION_TODAY, result.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void getSleepToday(Calendar start, Calendar end) {
        long endTime = end.getTimeInMillis();
        long startTime = start.getTimeInMillis();
        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .setLimit(1)
                .build();
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readRequest).await(1, TimeUnit.MINUTES);
        DataSet activityData = dataReadResult.getDataSet(DataType.TYPE_ACTIVITY_SEGMENT);
        StringBuilder result = new StringBuilder(0);
        for (DataPoint dp : activityData.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                String res = dp.getValue(field).toString();
                result.append(res);
            }
        }
        Intent intent = new Intent(HISTORY_INTENT);
        intent.putExtra(HISTORY_EXTRA_SLEEP_TODAY, result.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void getWeek(Calendar start, Calendar end, DataType type, DataType agrType) {
        long endTime = end.getTimeInMillis();
        long startTime = start.getTimeInMillis();
        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        .aggregate(type, agrType)
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        DataReadResult dataReadResult = Fitness.HistoryApi
                .readData(mGoogleApiFitnessClient, readRequest).await(1, TimeUnit.MINUTES);
        Constructor<DataReadResponse> constructor = (Constructor<DataReadResponse>) DataReadResponse.class.getDeclaredConstructors()[1];
        constructor.setAccessible(true);
        DataReadResponse response = null;
        try {
            response = constructor.newInstance(dataReadResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        printData(response);
    }

    public static void printData(DataReadResponse dataReadResult) {
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
    }

    private static void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
            }
        }
    }

    private void buildFitnessClient() {
        // Create the Google API Client
        mGoogleApiFitnessClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Google Fit connected.");
                                mTryingToConnect = false;
                                Log.d(TAG, "Notifying the UI that we're connected.");
                                notifyUiFitConnected();

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                mTryingToConnect = false;
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Google Fit Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Google Fit Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                mTryingToConnect = false;
                                notifyUiFailedConnection(result);
                            }
                        }
                )
                .build();
    }

    private void notifyUiFitConnected() {
        Intent intent = new Intent(FIT_NOTIFY_INTENT);
        intent.putExtra(FIT_EXTRA_CONNECTION_MESSAGE, FIT_EXTRA_CONNECTION_MESSAGE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyUiFailedConnection(ConnectionResult result) {
        Intent intent = new Intent(FIT_NOTIFY_INTENT);
        intent.putExtra(FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE, result.getErrorCode());
        intent.putExtra(FIT_EXTRA_NOTIFY_FAILED_INTENT, result.getResolution());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
