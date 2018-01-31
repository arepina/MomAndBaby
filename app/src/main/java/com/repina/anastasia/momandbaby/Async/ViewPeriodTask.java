package com.repina.anastasia.momandbaby.Async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.repina.anastasia.momandbaby.Activity.ChartActivity;
import com.repina.anastasia.momandbaby.Activity.TabsActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Fragment.FragmentMom;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.Processing.TextProcessing;
import com.repina.anastasia.momandbaby.R;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ViewPeriodTask extends AsyncTask<Calendar, ArrayList<Pair<DataType, Pair<String, Double>>>, ArrayList<Pair<DataType, Pair<String, Double>>>> {
    private boolean isEmail;
    private boolean isChart;
    private String selectedItemName;
    private WeakReference<FragmentActivity> activityWeakReference;
    private WeakReference<ListView> listViewWeakReference;
    private GridItemArrayAdapter adapter;
    private String start, end;
    private ProgressDialog dialog;

    public ViewPeriodTask(boolean isEmail, boolean isChart, String selectedItemName,
                          FragmentActivity activity, ListView listView, GridItemArrayAdapter adapter,
                          String start, String end) {
        this.isEmail = isEmail;
        this.isChart = isChart;
        this.selectedItemName = selectedItemName;
        this.activityWeakReference = new WeakReference<>(activity);
        this.listViewWeakReference = new WeakReference<>(listView);
        this.adapter = adapter;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(activityWeakReference.get());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(activityWeakReference.get().getString(R.string.google_fit_load));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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

    private ArrayList<Pair<DataType, Pair<String, Double>>> periodData(Calendar startDate,
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

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(TabsActivity.mClient, readRequest).await(1, TimeUnit.MINUTES);

        ArrayList<Pair<DataType, Pair<String, Double>>> sumData = new ArrayList<>();

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    startDateClone.add(Calendar.DAY_OF_YEAR, 1);
                    ArrayList<Pair<DataType, Pair<String, Double>>> dataForADay = FragmentMom.parseData(dataSet, type, activity);
                    sumData.addAll(dataForADay);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                ArrayList<Pair<DataType, Pair<String, Double>>> dataForADay = FragmentMom.parseData(dataSet, type, activity);
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
        dialog.dismiss();
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

    @Override
    protected void onCancelled() {
        super.onCancelled();
        NotificationsShow.showToast(activityWeakReference.get().getApplicationContext(),
                activityWeakReference.get().getApplicationContext().getString(R.string.data_load_stop));
    }
}
