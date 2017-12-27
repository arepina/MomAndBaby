package com.repina.anastasia.momandbaby.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.repina.anastasia.momandbaby.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleFitActivity extends AppCompatActivity //implements
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        View.OnClickListener
{

    private Button mButtonViewWeek;
    private Button mButtonViewToday;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_fit);

//        initViews();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Fitness.HISTORY_API)
//                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
//                .addConnectionCallbacks(this)
//                .enableAutoManage(this, 0, this)
//                .build();
    }

//    private void initViews() {
//        mButtonViewWeek = (Button) findViewById(R.id.btn_view_week);
//        mButtonViewToday = (Button) findViewById(R.id.btn_view_today);
//
//        mButtonViewWeek.setOnClickListener(this);
//        mButtonViewToday.setOnClickListener(this);
//    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }

//    //In use, call this every 30 seconds in active mode, 60 in ambient on watch faces
//    private void displayStepDataForToday() {
//        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(5, TimeUnit.SECONDS);
//        showDataSet(result.getTotal());
//    }

//    private void displayLastWeeksData() {
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//        cal.add(Calendar.WEEK_OF_YEAR, -1);
//        long startTime = cal.getTimeInMillis();
//
//        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
//        Log.e("History", "Range Start: " + dateFormat.format(startTime));
//        Log.e("History", "Range End: " + dateFormat.format(endTime));
//
//        //Check how many steps were walked and recorded in the last 7 days
//        DataReadRequest readRequest = new DataReadRequest.Builder()
//                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
//                .bucketByTime(1, TimeUnit.DAYS)
//                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                .build();
//
//        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(5, TimeUnit.SECONDS);
//
//        //Used for aggregated data
//        if (dataReadResult.getBuckets().size() > 0) {
//            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
//            for (Bucket bucket : dataReadResult.getBuckets()) {
//                List<DataSet> dataSets = bucket.getDataSets();
//                for (DataSet dataSet : dataSets) {
//                    showDataSet(dataSet);
//                }
//            }
//        }
//        //Used for non-aggregated data
//        else if (dataReadResult.getDataSets().size() > 0) {
//            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
//            for (DataSet dataSet : dataReadResult.getDataSets()) {
//                showDataSet(dataSet);
//            }
//        }
//    }
//
//
//    private void showDataSet(DataSet dataSet) {
//        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
//        DateFormat dateFormat = DateFormat.getDateInstance();
//        DateFormat timeFormat = DateFormat.getTimeInstance();
//
//        for (DataPoint dp : dataSet.getDataPoints()) {
//            Log.e("History", "Data point:");
//            Log.e("History", "\tType: " + dp.getDataType().getName());
//            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
//            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
//            for(Field field : dp.getDataType().getFields()) {
//                Log.e("History", "\tField: " + field.getName() +
//                        " Value: " + dp.getValue(field));
//            }
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.e("HistoryAPI", "onConnectionSuspended");
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Log.e("HistoryAPI", "onConnectionFailed");
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch(v.getId()) {
//            case R.id.btn_view_week: {
//                new ViewWeekStepCountTask().execute();
//                break;
//            }
//            case R.id.btn_view_today: {
//                new ViewTodaysStepCountTask().execute();
//                break;
//            }
//        }
//    }
//
//    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
//        protected Void doInBackground(Void... params) {
//            displayLastWeeksData();
//            return null;
//        }
//    }
//
//    private class ViewTodaysStepCountTask extends AsyncTask<Void, Void, Void> {
//        protected Void doInBackground(Void... params) {
//            displayStepDataForToday();
//            return null;
//        }
//    }
}