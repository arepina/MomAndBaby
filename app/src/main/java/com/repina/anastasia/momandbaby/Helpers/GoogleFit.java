package com.repina.anastasia.momandbaby.Helpers;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.SessionsApi;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFit implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private GridItemArrayAdapter adapter;
    private ListView listView;
    private FragmentActivity activity;

    private String start, end;

    public GoogleFit(Activity activity) {
        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(fragmentActivity, 0, this)
                .addScope(Fitness.SCOPE_LOCATION_READ)
                .addScope(Fitness.SCOPE_ACTIVITY_READ)
                .addScope(Fitness.SCOPE_BODY_READ)
                .addScope(Fitness.SCOPE_NUTRITION_READ)
                .build();
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    void getPeriodData(Calendar startDate, Calendar endDate, FragmentActivity activity, GridItemArrayAdapter adapter, ListView listView, boolean isEmail) {
        this.start = FormattedDate.getFormattedDateWithoutTime(startDate);
        this.end = FormattedDate.getFormattedDateWithoutTime(endDate);
        this.adapter = adapter;
        this.listView = listView;
        this.activity = activity;
        new ViewPeriodTask(isEmail).execute(startDate, endDate);
    }

    void getOneDayData(Calendar date, FragmentActivity activity, GridItemArrayAdapter adapter, ListView listView, boolean isEmail) {
        this.start = FormattedDate.getFormattedDateWithoutTime(date);
        this.end = FormattedDate.getFormattedDateWithoutTime(date);
        this.adapter = adapter;
        this.listView = listView;
        this.activity = activity;
        new ViewTodayTask(isEmail).execute(date);
    }

    private ArrayList<Pair<DataType, Pair<String, Double>>> dataForToday(DataType type) {
        DailyTotalResult result = Fitness.HistoryApi
                .readDailyTotal(mGoogleApiClient, type)
                .await(5, TimeUnit.SECONDS);
        return parseData(result.getTotal(), type);
    }

    private ArrayList<Pair<DataType, Pair<String, Double>>> periodData(Calendar startDate, Calendar endDate, DataType type, DataType agrType) {
        Calendar startDateClone = Calendar.getInstance();
        startDateClone.setTime(startDate.getTime());

        long endTime = endDate.getTimeInMillis();
        long startTime = startDateClone.getTimeInMillis();

        DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(type, agrType)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(5, TimeUnit.SECONDS);

        ArrayList<Pair<DataType, Pair<String, Double>>> sumData = new ArrayList<>();

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    startDateClone.add(Calendar.DAY_OF_YEAR, 1);
                    ArrayList<Pair<DataType, Pair<String, Double>>> dataForADay = parseData(dataSet, type);
                    sumData.addAll(dataForADay);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                ArrayList<Pair<DataType, Pair<String, Double>>> dataForADay = parseData(dataSet, type);
                sumData.addAll(dataForADay);
            }
        }

        return sumData;
    }

    private ArrayList<Pair<DataType, Pair<String, Double>>> parseData(DataSet dataSet, DataType type) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        ArrayList<Pair<DataType, Pair<String, Double>>> parsedData = new ArrayList<>();
        for (DataPoint dp : dataSet.getDataPoints()) {
            String startDate = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
            String endDate = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
            if (startDate.equals(endDate)) {
                String startTime = timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                String endTime = timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                Log.e("History", "\tStart: " + startDate + " " + startTime);
                Log.e("History", "\tEnd: " + endDate + " " + endTime);
                double value = 0;
                if (type.equals(DataType.TYPE_ACTIVITY_SEGMENT)) {
                    dp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.SLEEP);
                    String activity = dp.getValue(Field.FIELD_ACTIVITY).asActivity();
                    value = dp.getValue(Field.FIELD_DURATION).asInt();

                    Calendar cal = Calendar.getInstance();
                    Date now = new Date();
                    cal.setTime(now);
                    long endTime1 = cal.getTimeInMillis();
                    cal.add(Calendar.WEEK_OF_YEAR, -1);
                    long startTime1 = cal.getTimeInMillis();


//                    final SessionReadRequest.Builder sessionBuilder = new SessionReadRequest.Builder()
//
//                            .setTimeInterval(startTime1, endTime1, TimeUnit.MILLISECONDS)
//                            .read(DataType.TYPE_ACTIVITY_SEGMENT)
//                            .readSessionsFromAllApps()
//                            .enableServerQueries();
//
//                    final SessionReadRequest readRequest = sessionBuilder.build();
//
//                    SessionReadResult sessionReadResult =
//                            SessionsApi.readSession(mGoogleApiClient, readRequest).await(120, TimeUnit.SECONDS);
//
//                    Status status = sessionReadResult.getStatus();

                    Log.e("History", "\tField: " + activity + " Value: " + value);
                } else {
                    Field field = dp.getDataType().getFields().get(0);
                    Log.e("History", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                    value = Double.parseDouble(dp.getValue(field).toString());
                }
                Pair<String, Double> newDataEntry = new Pair<>(startDate, value);
                Pair<DataType, Pair<String, Double>> entry = new Pair<>(type, newDataEntry);
                parsedData.add(entry);
            }
        }
        return parsedData;
    }

    private class ViewPeriodTask extends AsyncTask<Calendar, ArrayList<Pair<DataType, Pair<String, Double>>>, ArrayList<Pair<DataType, Pair<String, Double>>>> {
        private boolean isEmail;

        ViewPeriodTask(boolean isEmail) {
            this.isEmail = isEmail;
        }

        protected ArrayList<Pair<DataType, Pair<String, Double>>> doInBackground(Calendar... params) {
            DataType type = DataType.TYPE_STEP_COUNT_DELTA;
            DataType agrType = DataType.AGGREGATE_STEP_COUNT_DELTA;
            ArrayList<Pair<DataType, Pair<String, Double>>> result = periodData(params[0], params[1], type, agrType);

            type = DataType.TYPE_CALORIES_EXPENDED;
            agrType = DataType.AGGREGATE_CALORIES_EXPENDED;
            ArrayList<Pair<DataType, Pair<String, Double>>> result1 = periodData(params[0], params[1], type, agrType);
            result.addAll(result1);

            type = DataType.TYPE_WEIGHT;
            agrType = DataType.AGGREGATE_WEIGHT_SUMMARY;
            result1 = periodData(params[0], params[1], type, agrType);
            result.addAll(result1);

            type = DataType.TYPE_NUTRITION;
            agrType = DataType.AGGREGATE_NUTRITION_SUMMARY;
            result1 = periodData(params[0], params[1], type, agrType);
            result.addAll(result1);

            //todo sleep
            //datapoint
            //dp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.SLEEP);
            type = DataType.TYPE_ACTIVITY_SEGMENT;
            agrType = DataType.AGGREGATE_ACTIVITY_SUMMARY;
            result1 = periodData(params[0], params[1], type, agrType);
            result.addAll(result1);

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Pair<DataType, Pair<String, Double>>> result) {
            for (Pair<DataType, Pair<String, Double>> pair : result) {
                DataType type = pair.first;
                Pair<String, Double> entry = pair.second;
                String date = entry.first;
                Double value = entry.second;
                GridItem item = null;
                if (type.equals(DataType.TYPE_STEP_COUNT_DELTA))
                    item = new GridItem(R.mipmap.steps, value.toString(), date);
                if (type.equals(DataType.TYPE_CALORIES_EXPENDED))
                    item = new GridItem(R.mipmap.calories, value.toString(), date);
                if (type.equals(DataType.TYPE_WEIGHT))
                    item = new GridItem(R.mipmap.weight, value.toString(), date);
                if (type.equals(DataType.TYPE_NUTRITION))
                    item = new GridItem(R.mipmap.nutrition, value.toString(), date);
                if (type.equals(DataType.TYPE_ACTIVITY_SEGMENT))
                    item = new GridItem(R.mipmap.rest, value.toString(), date);
                if (!adapter.hasItem(item))
                    adapter.add(item);
            }
            if (adapter.getCount() == 0)//no data for today
            {
                GridItem item = new GridItem(R.mipmap.cross, activity.getResources().getString(R.string.need_to_sync), null, null);
                adapter.add(item);
            }
            if (!isEmail)
                listView.setAdapter(adapter);
            else
                SendEmail.formMomsReport(adapter, activity.getApplicationContext(), start, end);
        }
    }

    private class ViewTodayTask extends AsyncTask<Calendar, ArrayList<Pair<DataType, Pair<String, Double>>>, ArrayList<Pair<DataType, Pair<String, Double>>>> {

        private boolean isEmail;

        ViewTodayTask(boolean isEmail) {
            this.isEmail = isEmail;
        }

        protected ArrayList<Pair<DataType, Pair<String, Double>>> doInBackground(Calendar... params) {
            DataType type = DataType.TYPE_STEP_COUNT_DELTA;
            ArrayList<Pair<DataType, Pair<String, Double>>> result = dataForToday(type);

            type = DataType.TYPE_CALORIES_EXPENDED;
            ArrayList<Pair<DataType, Pair<String, Double>>> result1 = dataForToday(type);
            result.addAll(result1);

            type = DataType.TYPE_WEIGHT;
            result1 = dataForToday(type);
            result.addAll(result1);

            type = DataType.TYPE_NUTRITION;
            result1 = dataForToday(type);
            result.addAll(result1);

            //todo sleep
            type = DataType.TYPE_ACTIVITY_SEGMENT;
            result1 = dataForToday(type);
            result.addAll(result1);

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Pair<DataType, Pair<String, Double>>> result) {
            for (Pair<DataType, Pair<String, Double>> pair : result) {
                DataType type = pair.first;
                Pair<String, Double> entry = pair.second;
                String date = entry.first;
                Double value = entry.second;
                GridItem item = null;
                if (type.equals(DataType.TYPE_STEP_COUNT_DELTA))
                    item = new GridItem(R.mipmap.steps, value.toString(), date);
                if (type.equals(DataType.TYPE_CALORIES_EXPENDED))
                    item = new GridItem(R.mipmap.calories, value.toString(), date);
                if (type.equals(DataType.TYPE_WEIGHT))
                    item = new GridItem(R.mipmap.weight, value.toString(), date);
                if (type.equals(DataType.TYPE_NUTRITION))
                    item = new GridItem(R.mipmap.nutrition, value.toString(), date);
                if (type.equals(DataType.TYPE_ACTIVITY_SEGMENT))
                    item = new GridItem(R.mipmap.rest, value.toString(), date);
                if (!adapter.hasItem(item))
                    adapter.add(item);
            }
            if (adapter.getCount() == 0)//no data for today
            {
                GridItem item = new GridItem(R.mipmap.cross, activity.getResources().getString(R.string.need_to_sync), null, null);
                adapter.add(item);
            }
            if (!isEmail)
                listView.setAdapter(adapter);
            else
                SendEmail.formMomsReport(adapter, activity.getApplicationContext(), start, end);
        }
    }
}