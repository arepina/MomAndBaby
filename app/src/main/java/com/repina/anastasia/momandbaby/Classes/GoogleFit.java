package com.repina.anastasia.momandbaby.Classes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;

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
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.repina.anastasia.momandbaby.Adapter.Item;
import com.repina.anastasia.momandbaby.Adapter.ItemArrayAdapter;
import com.repina.anastasia.momandbaby.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFit implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private ItemArrayAdapter adapter;
    private ListView listView;
    private FragmentActivity activity;

    public GoogleFit(FragmentActivity activity) {
        mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(activity, 0, this)
                .build();
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }

    void getWeekData(Calendar startDate, Calendar endDate, FragmentActivity activity, ItemArrayAdapter adapter, ListView listView) {
        this.adapter = adapter;
        this.listView = listView;
        this.activity = activity;
        new ViewWeekStepCountTask().execute(startDate, endDate);
    }

    void getOneDayData(Calendar date, FragmentActivity activity, ItemArrayAdapter adapter, ListView listView) {
        this.adapter = adapter;
        this.listView = listView;
        this.activity = activity;
        new ViewTodaysStepCountTask().execute(date);
    }

    private ArrayList<Pair<String, Integer>> stepDataForToday(Calendar date) {
        //todo use date here
        DailyTotalResult result = Fitness.HistoryApi
                .readDailyTotal(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .await(5, TimeUnit.SECONDS);
        return parseStepsData(result.getTotal());
    }

    private ArrayList<Pair<String, Integer>> lastWeeksData(Calendar startDate, Calendar endDate) {
        long endTime = endDate.getTimeInMillis();
        long startTime = startDate.getTimeInMillis();

        DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(5, TimeUnit.SECONDS);

        ArrayList<Pair<String, Integer>> sumStepsData = new ArrayList<>();

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    startDate.add(Calendar.DAY_OF_YEAR, 1);
                    ArrayList<Pair<String, Integer>> stepsDataForADay = parseStepsData(dataSet);
                    if (stepsDataForADay.size() == 0)//no data for a day
                        stepsDataForADay.add(new Pair<>(dateFormat.format(startDate.getTimeInMillis()), -1));
                    sumStepsData.addAll(stepsDataForADay);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                ArrayList<Pair<String, Integer>> stepsDataForADay = parseStepsData(dataSet);
                sumStepsData.addAll(stepsDataForADay);
            }
        }

        return sumStepsData;
    }

    private ArrayList<Pair<String, Integer>> parseStepsData(DataSet dataSet) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        ArrayList<Pair<String, Integer>> stepsData = new ArrayList<>();
        for (DataPoint dp : dataSet.getDataPoints()) {
            String startDate = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
            String endDate = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
            if (startDate.equals(endDate)) {
                String startTime = timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                String endTime = timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                Log.e("History", "\tStart: " + startDate + " " + startTime);
                Log.e("History", "\tEnd: " + endDate + " " + endTime);
                Field field = dp.getDataType().getFields().get(0);//steps number
                Log.e("History", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                int steps = Integer.parseInt(dp.getValue(field).toString());
                Pair<String, Integer> newStepsEntry = new Pair<>(startDate, steps);
                stepsData.add(newStepsEntry);
            }
        }
        return stepsData;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    private class ViewWeekStepCountTask extends AsyncTask<Calendar, ArrayList<Pair<String, Integer>>, ArrayList<Pair<String, Integer>>> {
        protected ArrayList<Pair<String, Integer>> doInBackground(Calendar... params) {
            return lastWeeksData(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<Pair<String, Integer>> result) {
            for(Pair<String, Integer> pair : result)
            {
                String date = pair.first;
                Integer steps = pair.second;
                Item item = new Item(R.mipmap.steps, steps.toString());
                if(!adapter.hasItem(item))
                    adapter.add(item);
            }
            if(adapter.getCount() == 0)//no data for today
            {
                Item item = new Item(R.mipmap.cross, activity.getResources().getString(R.string.need_to_sync));
                adapter.add(item);
            }
            listView.setAdapter(adapter);
        }
    }

    private class ViewTodaysStepCountTask extends AsyncTask<Calendar, ArrayList<Pair<String, Integer>>, ArrayList<Pair<String, Integer>>> {
        protected ArrayList<Pair<String, Integer>>  doInBackground(Calendar... params) {
            return stepDataForToday(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Pair<String, Integer>> result) {
            for(Pair<String, Integer> pair : result)
            {
                String date = pair.first;
                Integer steps = pair.second;
                Item item = new Item(R.mipmap.steps, steps.toString(), date);
                if(!adapter.hasItem(item))
                    adapter.add(item);
            }
            if(adapter.getCount() == 0)//no data for today
            {
                Item item = new Item(R.mipmap.cross, activity.getResources().getString(R.string.need_to_sync));
                adapter.add(item);
            }
            listView.setAdapter(adapter);
        }
    }
}