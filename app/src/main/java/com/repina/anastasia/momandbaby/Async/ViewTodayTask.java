package com.repina.anastasia.momandbaby.Async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.ListView;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.repina.anastasia.momandbaby.Activity.TabsActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Fragment.FragmentMom;
import com.repina.anastasia.momandbaby.Helpers.GoogleFitDataParser;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Processing.TextProcessing;
import com.repina.anastasia.momandbaby.R;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ViewTodayTask extends AsyncTask<Calendar, ArrayList<Pair<DataType, Pair<String, String>>>,
        ArrayList<Pair<DataType, Pair<String, String>>>> {

    private boolean isEmail;
    private WeakReference<FragmentActivity> activityWeakReference;
    private WeakReference<ListView> listViewWeakReference;
    private GridItemArrayAdapter adapter;
    private String start, end;

    public ViewTodayTask(boolean isEmail, FragmentActivity activity, ListView listView, GridItemArrayAdapter adapter,
                         String start, String end) {
        this.isEmail = isEmail;
        this.activityWeakReference = new WeakReference<>(activity);
        this.listViewWeakReference = new WeakReference<>(listView);
        this.adapter = adapter;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        TabsActivity.dialog.show();
    }

    protected ArrayList<Pair<DataType, Pair<String, String>>> doInBackground(Calendar... params) {
        DataType type = DataType.TYPE_STEP_COUNT_DELTA;
        ArrayList<Pair<DataType, Pair<String, String>>> result = dataForToday(type, activityWeakReference.get());

        type = DataType.TYPE_CALORIES_EXPENDED;
        ArrayList<Pair<DataType, Pair<String, String>>> result1 = dataForToday(type, activityWeakReference.get());
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

    private ArrayList<Pair<DataType, Pair<String, String>>> dataForToday(DataType type, FragmentActivity activity) {
        DailyTotalResult result = Fitness.HistoryApi
                .readDailyTotal(TabsActivity.mClient, type)
                .await(5, TimeUnit.SECONDS);
        if (result.getTotal() != null)
            return GoogleFitDataParser.parseData(result.getTotal(), type, activity);
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(ArrayList<Pair<DataType, Pair<String, String>>> result) {
        adapter.clear();
        for (Pair<DataType, Pair<String, String>> pair : result) {
            DataType type = pair.first;
            Pair<String, String> entry = pair.second;
            String date = entry.first;
            String value = entry.second;
            GridItem item = null;
            if (type.equals(DataType.TYPE_STEP_COUNT_DELTA))
                item = new GridItem(R.mipmap.steps, "R.mipmap.steps", value, date);
            if (type.equals(DataType.TYPE_CALORIES_EXPENDED))
                item = new GridItem(R.mipmap.calories, "R.mipmap.calories", value, date);
            if (type.equals(DataType.TYPE_WEIGHT))
                item = new GridItem(R.mipmap.weight, "R.mipmap.weight", value, date);
            if (type.equals(DataType.TYPE_NUTRITION))
                item = new GridItem(R.mipmap.nutrition, "R.mipmap.nutrition", value, date);
            if (type.equals(DataType.TYPE_ACTIVITY_SEGMENT))
                item = new GridItem(R.mipmap.rest, "R.mipmap.rest", value, date);
            if (!adapter.hasItem(item))
                adapter.add(item);
        }
        TabsActivity.dialog.dismiss();
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

    @Override
    protected void onCancelled() {
        super.onCancelled();
        NotificationsShow.showToast(activityWeakReference.get().getApplicationContext(),
                activityWeakReference.get().getApplicationContext().getString(R.string.data_load_stop));
    }
}
