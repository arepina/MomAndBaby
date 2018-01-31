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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.Task;
import com.repina.anastasia.momandbaby.Activity.ChartActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Helpers.Processing.TextProcessing;
import com.repina.anastasia.momandbaby.R;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFit implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static GoogleApiClient mGoogleApiClient;
    private static GridItemArrayAdapter adapter;

    private static String start;
    private static String end;

    private FragmentActivity fragmentActivity;

    public GoogleFit(Activity activity) {
        fragmentActivity = (FragmentActivity) activity;
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

    public void getPeriodData(Calendar startDate, Calendar endDate,
                              GridItemArrayAdapter adapter, ListView listView,
                              boolean isEmail, boolean isChart, String selectedItemName) {
        start = FormattedDate.getFormattedDateWithoutTime(startDate);
        end = FormattedDate.getFormattedDateWithoutTime(endDate);
        GoogleFit.adapter = adapter;
        new ViewPeriodTask(isEmail, isChart, selectedItemName, fragmentActivity, listView).execute(startDate, endDate);
    }

    public void getOneDayData(Calendar date, GridItemArrayAdapter adapter,
                              ListView listView, boolean isEmail) {
        start = FormattedDate.getFormattedDateWithoutTime(date);
        end = FormattedDate.getFormattedDateWithoutTime(date);
        GoogleFit.adapter = adapter;
        new ViewTodayTask(isEmail, fragmentActivity, listView).execute(date);
    }

    private static ArrayList<Pair<DataType, Pair<String, Double>>> parseData(DataSet dataSet, DataType type, FragmentActivity activity) {
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
                    //todo sleep
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

    private static class ViewPeriodTask extends AsyncTask<Calendar, ArrayList<Pair<DataType, Pair<String, Double>>>, ArrayList<Pair<DataType, Pair<String, Double>>>> {
        private boolean isEmail;
        private boolean isChart;
        private String selectedItemName;
        private WeakReference<FragmentActivity> activityWeakReference;
        private WeakReference<ListView> listViewWeakReference;

        ViewPeriodTask(boolean isEmail, boolean isChart, String selectedItemName, FragmentActivity activity, ListView listView) {
            this.isEmail = isEmail;
            this.isChart = isChart;
            this.selectedItemName = selectedItemName;
            this.activityWeakReference = new WeakReference<>(activity);
            this.listViewWeakReference = new WeakReference<>(listView);
        }

        protected ArrayList<Pair<DataType, Pair<String, Double>>> doInBackground(Calendar... params) {
            DataType type = DataType.TYPE_STEP_COUNT_DELTA;
            DataType agrType = DataType.AGGREGATE_STEP_COUNT_DELTA;
            ArrayList<Pair<DataType, Pair<String, Double>>> result = periodData(params[0], params[1],
                    type, agrType, activityWeakReference.get());

            type = DataType.TYPE_CALORIES_EXPENDED;
            agrType = DataType.AGGREGATE_CALORIES_EXPENDED;
            ArrayList<Pair<DataType, Pair<String, Double>>> result1 = periodData(params[0], params[1],
                    type, agrType, activityWeakReference.get());
            result.addAll(result1);

            type = DataType.TYPE_WEIGHT;
            agrType = DataType.AGGREGATE_WEIGHT_SUMMARY;
            result1 = periodData(params[0], params[1], type, agrType, activityWeakReference.get());
            result.addAll(result1);

            type = DataType.TYPE_NUTRITION;
            agrType = DataType.AGGREGATE_NUTRITION_SUMMARY;
            result1 = periodData(params[0], params[1], type, agrType, activityWeakReference.get());
            result.addAll(result1);

            //todo sleep
//            type = DataType.TYPE_ACTIVITY_SEGMENT;
//            agrType = DataType.AGGREGATE_ACTIVITY_SUMMARY;
//            result1 = periodData(params[0], params[1], type, agrType, activityWeakReference.get());
//            result.addAll(result1);

            return result;
        }

        private static ArrayList<Pair<DataType, Pair<String, Double>>> periodData(Calendar startDate,
                                                                                  Calendar endDate,
                                                                                  DataType type,
                                                                                  DataType agrType,
                                                                                  FragmentActivity activity) {
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

            DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);

            ArrayList<Pair<DataType, Pair<String, Double>>> sumData = new ArrayList<>();

            //Used for aggregated data
            if (dataReadResult.getBuckets().size() > 0) {
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        startDateClone.add(Calendar.DAY_OF_YEAR, 1);
                        ArrayList<Pair<DataType, Pair<String, Double>>> dataForADay = parseData(dataSet, type, activity);
                        sumData.addAll(dataForADay);
                    }
                }
            }
            //Used for non-aggregated data
            else if (dataReadResult.getDataSets().size() > 0) {
                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    ArrayList<Pair<DataType, Pair<String, Double>>> dataForADay = parseData(dataSet, type, activity);
                    sumData.addAll(dataForADay);
                }
            }

            return sumData;
        }

        @Override
        protected void onPostExecute(ArrayList<Pair<DataType, Pair<String, Double>>> result) {
            adapter.clear();
            for (Pair<DataType, Pair<String, Double>> pair : result) {
                DataType type = pair.first;
                Pair<String, Double> entry = pair.second;
                String date = entry.first;
                Double value = entry.second;
                GridItem item = null;
                if (type.equals(DataType.TYPE_STEP_COUNT_DELTA))
                    item = new GridItem(R.mipmap.steps, "R.mipmap.steps", value.toString(), date);
                if (type.equals(DataType.TYPE_CALORIES_EXPENDED))
                    item = new GridItem(R.mipmap.calories, "R.mipmap.calories", value.toString(), date);
                if (type.equals(DataType.TYPE_WEIGHT))
                    item = new GridItem(R.mipmap.weight, "R.mipmap.weight", value.toString(), date);
                if (type.equals(DataType.TYPE_NUTRITION))
                    item = new GridItem(R.mipmap.nutrition, "R.mipmap.nutrition", value.toString(), date);
                if (type.equals(DataType.TYPE_ACTIVITY_SEGMENT))
                    item = new GridItem(R.mipmap.rest, "R.mipmap.rest", value.toString(), date);
                if (!adapter.hasItem(item))
                    adapter.add(item);
            }
            if (adapter.getCount() == 0)//no data for today
            {
                GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", activityWeakReference.get().getResources().getString(R.string.need_to_sync), null, null);
                adapter.add(item);
            }
            if (isEmail)
                TextProcessing.formMomReport(adapter, activityWeakReference.get().getApplicationContext(), start, end);
            if (isChart)
                ChartActivity.fillChartMom(adapter, activityWeakReference.get().getApplicationContext(), selectedItemName);
            else if (listViewWeakReference.get() != null)
                listViewWeakReference.get().setAdapter(adapter);
        }
    }

    private static class ViewTodayTask extends AsyncTask<Calendar, ArrayList<Pair<DataType, Pair<String, Double>>>, ArrayList<Pair<DataType, Pair<String, Double>>>> {

        private boolean isEmail;
        private WeakReference<FragmentActivity> activityWeakReference;
        private WeakReference<ListView> listViewWeakReference;

        ViewTodayTask(boolean isEmail, FragmentActivity activity, ListView listView) {
            this.isEmail = isEmail;
            this.activityWeakReference = new WeakReference<>(activity);
            this.listViewWeakReference = new WeakReference<>(listView);
        }

        protected ArrayList<Pair<DataType, Pair<String, Double>>> doInBackground(Calendar... params) {
            DataType type = DataType.TYPE_STEP_COUNT_DELTA;
            ArrayList<Pair<DataType, Pair<String, Double>>> result = dataForToday(type, activityWeakReference.get());

            type = DataType.TYPE_CALORIES_EXPENDED;
            ArrayList<Pair<DataType, Pair<String, Double>>> result1 = dataForToday(type, activityWeakReference.get());
            result.addAll(result1);

            type = DataType.TYPE_WEIGHT;
            result1 = dataForToday(type, activityWeakReference.get());
            result.addAll(result1);

            type = DataType.TYPE_NUTRITION;
            result1 = dataForToday(type, activityWeakReference.get());
            result.addAll(result1);

            //todo sleep
//            type = DataType.TYPE_ACTIVITY_SEGMENT;
//            result1 = dataForToday(type, activityWeakReference.get());
//            result.addAll(result1);

            return result;
        }

        private static ArrayList<Pair<DataType, Pair<String, Double>>> dataForToday(DataType type, FragmentActivity activity) {
            DailyTotalResult result = Fitness.HistoryApi
                    .readDailyTotal(mGoogleApiClient, type)
                    .await(5, TimeUnit.SECONDS);
            if (result.getTotal() != null)
                return parseData(result.getTotal(), type, activity);
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(ArrayList<Pair<DataType, Pair<String, Double>>> result) {
            adapter.clear();
            for (Pair<DataType, Pair<String, Double>> pair : result) {
                DataType type = pair.first;
                Pair<String, Double> entry = pair.second;
                String date = entry.first;
                Double value = entry.second;
                GridItem item = null;
                if (type.equals(DataType.TYPE_STEP_COUNT_DELTA))
                    item = new GridItem(R.mipmap.steps, "R.mipmap.steps", value.toString(), date);
                if (type.equals(DataType.TYPE_CALORIES_EXPENDED))
                    item = new GridItem(R.mipmap.calories, "R.mipmap.calories", value.toString(), date);
                if (type.equals(DataType.TYPE_WEIGHT))
                    item = new GridItem(R.mipmap.weight, "R.mipmap.weight", value.toString(), date);
                if (type.equals(DataType.TYPE_NUTRITION))
                    item = new GridItem(R.mipmap.nutrition, "R.mipmap.nutrition", value.toString(), date);
                if (type.equals(DataType.TYPE_ACTIVITY_SEGMENT))
                    item = new GridItem(R.mipmap.rest, "R.mipmap.rest", value.toString(), date);
                if (!adapter.hasItem(item))
                    adapter.add(item);
            }
            if (adapter.getCount() == 0)//no data for today
            {
                GridItem item = new GridItem(R.mipmap.cross, "R.mipmap.cross", activityWeakReference.get().getResources().getString(R.string.need_to_sync), null, null);
                adapter.add(item);
            }
            if (isEmail)
                TextProcessing.formMomReport(adapter, activityWeakReference.get().getApplicationContext(), start, end);
            else if (listViewWeakReference.get() != null)
                listViewWeakReference.get().setAdapter(adapter);
        }
    }
}