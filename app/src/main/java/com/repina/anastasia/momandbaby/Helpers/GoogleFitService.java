package com.repina.anastasia.momandbaby.Helpers;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;

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
import com.repina.anastasia.momandbaby.Activity.ChartActivity;
import com.repina.anastasia.momandbaby.Fragment.FragmentMom;
import com.repina.anastasia.momandbaby.Fragment.FragmentSettings;
import com.repina.anastasia.momandbaby.Processing.TextProcessing;
import com.repina.anastasia.momandbaby.R;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.CALLING;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_CONNECTION_MESSAGE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_NOTIFY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FROM;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_DATE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_AGGREGATED;
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
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

/**
 * GoogleFit service
 */
public class GoogleFitService extends IntentService {

    public static final String TAG = "GoogleFitService";
    private GoogleApiClient mGoogleApiFitnessClient;
    private boolean mTryingToConnect = false;

    private static final int hours = 24;

    @Override
    public void onDestroy() {
        Log.d(TAG, "GoogleFitService destroyed");
        if (mGoogleApiFitnessClient.isConnected()) {
            Log.d(TAG, "Disconnecting Google Fit.");
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
        String callingActivity = intent.getStringExtra(CALLING);
        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startMill);
        end.setTimeInMillis(endMill);
        if (mGoogleApiFitnessClient.isConnected()) {
            if (callingActivity != null &&  // need non aggregated buckets data
                    (callingActivity.equals(ChartActivity.class.toString())  // charts
                            || callingActivity.equals(FragmentSettings.class.toString()))) // or email
                if (type == 0) // need aggregated data for all types in sum
                {
                    ArrayList<Pair<DataType, Pair<String, String>>> sumData =
                            iterateTypes(start, end);
                    returnValues(sumData);
                } else // need aggregated data for all types in parts
                {
                    ArrayList<Pair<DataType, Pair<String, String>>> sumData =
                            getPeriod(start, end, getMainType(type), getAggregationType(type));
                    returnValues(sumData);
                }
            else {
                switch (type) {// need aggregated non buckets data
                    case TYPE_GET_STEP_TODAY_DATA:
                        getStepsToday(start, end);
                        break;
                    case TYPE_GET_CALORIES_TODAY_DATA:
                        getCaloriesToday(start, end);
                        break;
                    case TYPE_GET_WEIGHT_TODAY_DATA:
                        getWeightToday(start, end);
                        break;
                    case TYPE_GET_NUTRITION_TODAY_DATA: {
                        end.add(Calendar.DATE, 1); // added because of google fit api bug
                        getNutritionToday(start, end);
                        break;
                    }
                    case TYPE_GET_SLEEP_TODAY_DATA:
                        getSleepToday(start, end, false);
                        break;
                }
            }
        } else
            Log.w(TAG, "Fit wasn't able to connect, so the request failed.");
    }

    //region Get Types

    private DataType getAggregationType(int type) {
        if (type == TYPE_GET_STEP_TODAY_DATA)
            return DataType.AGGREGATE_STEP_COUNT_DELTA;
        else if (type == TYPE_GET_CALORIES_TODAY_DATA)
            return DataType.AGGREGATE_CALORIES_EXPENDED;
        else if (type == TYPE_GET_WEIGHT_TODAY_DATA)
            return DataType.AGGREGATE_WEIGHT_SUMMARY;
        else if (type == TYPE_GET_NUTRITION_TODAY_DATA)
            return DataType.AGGREGATE_NUTRITION_SUMMARY;
        else if (type == TYPE_GET_SLEEP_TODAY_DATA)
            return DataType.AGGREGATE_ACTIVITY_SUMMARY;
        return null;
    }

    private DataType getMainType(int type) {
        if (type == TYPE_GET_STEP_TODAY_DATA)
            return DataType.TYPE_STEP_COUNT_DELTA;
        else if (type == TYPE_GET_CALORIES_TODAY_DATA)
            return DataType.TYPE_CALORIES_EXPENDED;
        else if (type == TYPE_GET_WEIGHT_TODAY_DATA)
            return DataType.TYPE_WEIGHT;
        else if (type == TYPE_GET_NUTRITION_TODAY_DATA)
            return DataType.TYPE_NUTRITION;
        else if (type == TYPE_GET_SLEEP_TODAY_DATA)
            return DataType.TYPE_ACTIVITY_SEGMENT;
        return null;
    }

    //endregion

    //region Today data

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
        intent.putExtra(HISTORY_DATE, FormattedDate.getFormattedDate(start));
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
        intent.putExtra(HISTORY_DATE, FormattedDate.getFormattedDate(start));
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
        intent.putExtra(HISTORY_DATE, FormattedDate.getFormattedDate(start));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void getNutritionToday(Calendar start, Calendar end) {
        long endTime = end.getTimeInMillis();
        long startTime = start.getTimeInMillis();
        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_NUTRITION)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readRequest).await(1, TimeUnit.MINUTES);
        DataSet nutritionData = dataReadResult.getDataSet(DataType.TYPE_NUTRITION);
        StringBuilder result = new StringBuilder(0);
        for (DataPoint dp : nutritionData.getDataPoints()) {
            Field field = dp.getDataType().getFields().get(0);
            String temp = dp.getValue(field).toString().replace("{", "").replace("}", "");
            ArrayList<String> tempArr = new ArrayList<>(Arrays.asList(temp.split(", ")));
            for (String aTempArr : tempArr) {
                String[] wordValue = aTempArr.split("=");
                double val = Double.parseDouble(wordValue[1]);
                if (val != 0.0) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    result.append(TextProcessing.translateWord(wordValue[0])).append("=").append(df.format(val)).append(", ");
                }
            }
            result = new StringBuilder(result.substring(0, result.length() - 2));
        }
        Intent intent = new Intent(HISTORY_INTENT);
        intent.putExtra(HISTORY_EXTRA_NUTRITION_TODAY, result.toString());
        intent.putExtra(HISTORY_DATE, FormattedDate.getFormattedDate(start));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private Pair<DataType, Pair<String, String>> getSleepToday(Calendar start, Calendar end, boolean flag) {
        long endTime = end.getTimeInMillis();
        long startTime = start.getTimeInMillis();
        final DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mGoogleApiFitnessClient, readRequest).await(1, TimeUnit.MINUTES);
        DataSet activityData = dataReadResult.getDataSet(DataType.TYPE_ACTIVITY_SEGMENT);
        StringBuilder result = new StringBuilder(0);
        DateFormat dateFormat = getTimeInstance();
        double sumDifference = 0;
        for (DataPoint dp : activityData.getDataPoints()) {
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tDataType: " + dp.getDataType());
            Log.i(TAG, "\tDataSource: " + dp.getDataSource());
            Log.i(TAG, "\tDataSource1: " + dp.getOriginalDataSource());
            if (dp.getOriginalDataSource().getName() != null) {
                double difference = ((dp.getEndTime(TimeUnit.MILLISECONDS) - dp.getStartTime(TimeUnit.MILLISECONDS)) / 1000.0) / 3600.0;
                sumDifference += difference;
            }
        }
        String startDate = getDateInstance().format(start.getTime());
        DecimalFormat df = new DecimalFormat("0.0");
        if (Math.round(sumDifference) < hours) {
            result.append(getString(R.string.sleep_str)).append(df.format(sumDifference)).append(getString(R.string.hours_str));
            if (flag) { // from aggregate
                Pair<String, String> pair = new Pair<>(startDate, result.toString());
                return new Pair<>(DataType.TYPE_ACTIVITY_SEGMENT, pair);
            }
        }
        if (flag)
            return new Pair<>(DataType.TYPE_ACTIVITY_SEGMENT, new Pair<>(startDate, ""));
        Intent intent = new Intent(HISTORY_INTENT);
        intent.putExtra(HISTORY_EXTRA_SLEEP_TODAY, result.toString());
        intent.putExtra(HISTORY_DATE, FormattedDate.getFormattedDate(start));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        if (FragmentMom.dialog != null)
            FragmentMom.dialog.dismiss();
        return null;
    }

    //endregion

    //region Period data

    private ArrayList<Pair<DataType, Pair<String, String>>> iterateTypes(Calendar start, Calendar end) {
        ArrayList<Pair<DataType, Pair<String, String>>> sumData = new ArrayList<>();
        for (int i = 2; i <= 6; i++) {
            sumData.addAll(getPeriod(start, end, getMainType(i), getAggregationType(i)));
        }
        return sumData;
    }

    private ArrayList<Pair<DataType, Pair<String, String>>> getPeriod(Calendar start, Calendar end, DataType type, DataType agrType) {
        long endTime = end.getTimeInMillis();
        long startTime = start.getTimeInMillis();
        ArrayList<Pair<DataType, Pair<String, String>>> sumData = new ArrayList<>();
        if (type.equals(DataType.TYPE_ACTIVITY_SEGMENT)) // only for sleep data, aggregation does not work
        {
            int difference = Math.abs(end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR));
            Calendar s = Calendar.getInstance();
            Calendar e = Calendar.getInstance();
            s.set(Calendar.HOUR_OF_DAY, 0);
            s.set(Calendar.MINUTE, 0);
            s.set(Calendar.SECOND, 0);
            e.set(Calendar.HOUR_OF_DAY, 23);
            e.set(Calendar.MINUTE, 59);
            e.set(Calendar.SECOND, 59);
            s.add(Calendar.DATE, -difference);
            e.add(Calendar.DATE, -difference);
            sumData.add(getSleepToday(s, e, true));
            for (int i = 1; i <= difference; i++) {
                s.add(Calendar.DATE, 1);
                e.add(Calendar.DATE, 1);
                sumData.add(getSleepToday(s, e, true));
            }
        } else {
            if (type == DataType.TYPE_NUTRITION) {
                Calendar e = Calendar.getInstance();
                e.setTime(end.getTime());
                e.set(Calendar.HOUR_OF_DAY, 23);
                e.set(Calendar.MINUTE, 59);
                e.set(Calendar.SECOND, 59);
                e.add(Calendar.DAY_OF_YEAR, 1); // added because of google fit api bug
                endTime = e.getTimeInMillis();
            }
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
            DataReadResponse response = new DataReadResponse();
            try {
                response = constructor.newInstance(dataReadResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response.getBuckets().size() > 0) {
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        sumData.addAll(dumpDataSet(dataSet, type));
                    }
                }
            } else if (response.getDataSets().size() > 0) {
                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    sumData.addAll(dumpDataSet(dataSet, type));
                }
            }
        }
        return sumData;
    }

    private static ArrayList<Pair<DataType, Pair<String, String>>> dumpDataSet(DataSet dataSet, DataType type) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = getTimeInstance();
        ArrayList<Pair<DataType, Pair<String, String>>> parsedData = new ArrayList<>();
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            String startDate = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
            String endDate = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
            String startTime = timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
            String endTime = timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
            Log.e("History", "\tStart: " + startDate + " " + startTime);
            Log.e("History", "\tEnd: " + endDate + " " + endTime);
            double doubleValue = 0;
            StringBuilder stringValue = new StringBuilder();
            Field field = dp.getDataType().getFields().get(0);
            Log.e("History", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
            if (field.getName().equals("nutrients")) // nutrition
                stringValue = new StringBuilder(dp.getValue(field).toString().replace("{", "").replace("}", ""));
            else // steps, calories, weight
                doubleValue = Double.parseDouble(dp.getValue(field).toString());
            if (stringValue.length() == 0)
                stringValue = new StringBuilder(String.valueOf(doubleValue));
            Pair<String, String> newDataEntry = new Pair<>(startDate, stringValue.toString());
            Pair<DataType, Pair<String, String>> entry = new Pair<>(type, newDataEntry);
            parsedData.add(entry);
        }
        return parsedData;
    }

    private void returnValues(ArrayList<Pair<DataType, Pair<String, String>>> sumData) {
        Intent intent = new Intent(HISTORY_INTENT);
        intent.putExtra(HISTORY_EXTRA_AGGREGATED, sumData);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //endregion

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
                            public void onConnectionFailed(@NonNull ConnectionResult result) {
                                mTryingToConnect = false;
                                notifyUiFailedConnection(result);
                            }
                        }
                )
                .build();
    }

    //region Notifiers

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

    //endregion
}
