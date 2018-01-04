package com.repina.anastasia.momandbaby.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

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
import com.repina.anastasia.momandbaby.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFitActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener
{

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_fit);

        initViews();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();
    }

    private void initViews() {
        Button mButtonViewWeek = (Button) findViewById(R.id.btn_view_week);
        Button mButtonViewToday = (Button) findViewById(R.id.btn_view_today);

        mButtonViewWeek.setOnClickListener(this);
        mButtonViewToday.setOnClickListener(this);
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }

    //In use, call this every 30 seconds in active mode, 60 in ambient on watch faces
    private void displayStepDataForToday() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(5, TimeUnit.SECONDS);
        ArrayList<Pair<String, Integer>> stepsData = parseStepsData(result.getTotal());
        //todo
        //if(!stepsData.size() == 0)//no data for today
        //show toast

    }

    private void displayLastWeeksData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);//one week from today's date backward
        long startTime = cal.getTimeInMillis();

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
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    ArrayList<Pair<String, Integer>> stepsDataForADay = parseStepsData(dataSet);
                    if(stepsDataForADay.size() == 0)//no data for a day
                        stepsDataForADay.add(new Pair<>(dateFormat.format(cal.getTimeInMillis()),-1));
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

        //todo show sumStepsData
    }

    private ArrayList<Pair<String, Integer>> parseStepsData(DataSet dataSet) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        ArrayList<Pair<String, Integer>> stepsData = new ArrayList<>();
        for (DataPoint dp : dataSet.getDataPoints()) {
            String startDate = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
            String endDate = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
            if(startDate.equals(endDate)) {
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


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_view_week: {
                new ViewWeekStepCountTask().execute();
                break;
            }
            case R.id.btn_view_today: {
                new ViewTodaysStepCountTask().execute();
                break;
            }
        }
    }

    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksData();
            return null;
        }
    }

    private class ViewTodaysStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayStepDataForToday();
            return null;
        }
    }
}