package com.repina.anastasia.momandbaby.Async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.widget.ListView;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.repina.anastasia.momandbaby.Activity.TabsActivity;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Fragment.FragmentMom;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.Processing.TextProcessing;
import com.repina.anastasia.momandbaby.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ViewTodayTask extends AsyncTask<Calendar, ArrayList<Pair<DataType, Pair<String, Double>>>, ArrayList<Pair<DataType, Pair<String, Double>>>> {

    private boolean isEmail;
    private WeakReference<FragmentActivity> activityWeakReference;
    private WeakReference<ListView> listViewWeakReference;
    private GridItemArrayAdapter adapter;
    private String start, end;
    private ProgressDialog dialog;

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
        dialog = new ProgressDialog(activityWeakReference.get());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(activityWeakReference.get().getString(R.string.google_fit_load));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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

    private ArrayList<Pair<DataType, Pair<String, Double>>> dataForToday(DataType type, FragmentActivity activity) {
        DailyTotalResult result = Fitness.HistoryApi
                .readDailyTotal(TabsActivity.mClient, type)
                .await(5, TimeUnit.SECONDS);
        if (result.getTotal() != null)
            return FragmentMom.parseData(result.getTotal(), type, activity);
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
        dialog.dismiss();
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
