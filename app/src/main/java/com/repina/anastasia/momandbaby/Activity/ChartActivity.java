package com.repina.anastasia.momandbaby.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.Food;
import com.repina.anastasia.momandbaby.DataBase.Illness;
import com.repina.anastasia.momandbaby.DataBase.Metrics;
import com.repina.anastasia.momandbaby.DataBase.Outdoor;
import com.repina.anastasia.momandbaby.DataBase.Sleep;
import com.repina.anastasia.momandbaby.DataBase.Stool;
import com.repina.anastasia.momandbaby.Fragment.FragmentMom;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.GoogleFitService;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.Processing.StatsProcessing;
import com.repina.anastasia.momandbaby.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.repina.anastasia.momandbaby.Fragment.FragmentMom.AUTH_PENDING;
import static com.repina.anastasia.momandbaby.Fragment.FragmentMom.REQUEST_OAUTH;
import static com.repina.anastasia.momandbaby.Helpers.FormattedDate.getFormattedDate;
import static com.repina.anastasia.momandbaby.Helpers.FormattedDate.stringToDate;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_CONNECTION_MESSAGE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.FIT_NOTIFY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_EXTRA_AGGREGATED;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.HISTORY_INTENT;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.SERVICE_REQUEST_TYPE;
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.TYPE_REQUEST_CONNECTION;

/**
 * Charts
 */
public class ChartActivity extends AppCompatActivity {

    private static LineChart chart;
    private static ArrayList<Entry> entries;
    private static ArrayList<String> labels;
    private ArrayList<String> labelsIdeal;
    private static List<ILineDataSet> dataSets;
    private ProgressDialog dialog;
    private int spinnerSelectedIndex = 0;
    private int animationDuration = 2000;
    private static ArrayList<String> features;
    private String selectedItemName;

    private ConnectionResult mFitResultResolution;
    private boolean authInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        final String type = Objects.requireNonNull(getIntent().getExtras()).getString("Type");

        initChart(type);

        LocalBroadcastManager.getInstance(this).registerReceiver(mFitStatusReceiver, new IntentFilter(FIT_NOTIFY_INTENT));
        LocalBroadcastManager.getInstance(this).registerReceiver(mFitDataReceiver, new IntentFilter(HISTORY_INTENT));
        requestFitConnection();
    }

    //region Init Chart

    /**
     * Initialisation of chart
     *
     * @param type Mom or Baby
     */
    void initChart(final String type) {
        //https://www.android-examples.com/create-bar-chart-graph-using-mpandroidchart-library/
        //https://github.com/numetriclabz/numAndroidCharts
        chart = (LineChart) findViewById(R.id.graph);

        ArrayAdapter<?> adapter;
        if ("Mom".equals(type)) {
            adapter = ArrayAdapter.createFromResource(this, R.array.parametersMom, android.R.layout.simple_spinner_item);
            features = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.parametersMom)));
        } else {
            features = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.parametersBaby)));
            features.remove(3);//no vaccinations
            features.remove(7);//no other
            features.remove(7);//no teeth
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, features);
        }

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.fit_data_load));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setAdapter(adapter);
        spinner.setSelection(spinnerSelectedIndex);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelectedIndex = position;
                dataSets = new ArrayList<>();
                labelsIdeal = new ArrayList<>();
                if ("Mom".equals(type)) {
                    getValuesFromGoogleFit();
                } else {
                    if (ConnectionDetector.isConnected(view.getContext())) {
                        selectedItemName = features.get(position);
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String babyId = sp.getString(SharedConstants.BABY_ID_KEY, "");
                        FirebaseConnection connection = new FirebaseConnection();
                        FirebaseDatabase database = connection.getDatabase();
                        getValuesFromFirebase(database,
                                getBabyChartDBName(selectedItemName),
                                babyId,
                                selectedItemName);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });
    }

    /**
     * Get baby chart DB name
     *
     * @param value russian translation of DB name
     * @return DB name
     */
    public static String getBabyChartDBName(String value) {
        if (value.equals(features.get(0))) return DatabaseNames.METRICS;
        if (value.equals(features.get(1))) return DatabaseNames.METRICS;
        if (value.equals(features.get(2))) return DatabaseNames.STOOL;
        if (value.equals(features.get(3))) return DatabaseNames.ILLNESS;
        if (value.equals(features.get(4))) return DatabaseNames.FOOD;
        if (value.equals(features.get(5))) return DatabaseNames.OUTDOOR;
        if (value.equals(features.get(6))) return DatabaseNames.SLEEP;
        return "";
    }

    /**
     * Get mom chart name
     *
     * @return mom chart name
     */
    private String getMomChartName() {
        switch (spinnerSelectedIndex) {
            case 0: {
                return features.get(0);
            }
            case 1: {
                return features.get(1);
            }
            case 2: {
                return features.get(2);
            }
            case 3: {
                return features.get(3);
            }
        }
        return "";
    }

    //endregion

    // region Get chart data from Firebase and Google Fit

    /**
     * Get values for charts in Firebase
     *
     * @param database         Firebase DB
     * @param dbName           DB name
     * @param id               baby id
     * @param selectedItemName selected item name
     */
    void getValuesFromFirebase(final FirebaseDatabase database, final String dbName,
                               final String id, final String selectedItemName) {
        dialog.show();
        DatabaseReference databaseReference = database.getReference().child(dbName);
        databaseReference.orderByChild("babyId").
                equalTo(id).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            fillChartBaby(dataSnapshot, dbName, selectedItemName);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();
                        NotificationsShow.showToast(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }

    /**
     * Get values from GoogleFit
     */
    private void getValuesFromGoogleFit() {
        dialog.show();
        // ask for 1 month data for a specific type
        // steps 0 - 2
        // sleep 1 - 3
        // weight 2 - 5
        // calories 3 - 6
        int index = spinnerSelectedIndex;
        if (index == 0 || index == 1) // steps or sleep
            index += 2;
        else
            index += 3; // weight or calories
        StatsProcessing.getMomStats(Calendar.getInstance(), 31, this, index, ChartActivity.class.toString());
    }

    //endregion

    //region Fill charts with ideal data

    /**
     * Initialisation of ideal charts
     *  @param context app Context
     * @param dbName  DB name
     * @param labelsIdealBeforeMod
     */
    private void initIdealDataSets(Context context, String dbName, ArrayList<String> labelsIdealBeforeMod) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String gender = sp.getString(SharedConstants.BABY_GENDER_KEY, "");
        ArrayList<LineDataSet> sets;
        if (gender.equals(getString(R.string.boy_eng)))
            sets = initBoy(dbName, labelsIdealBeforeMod);
        else
            sets = initGirl(dbName, labelsIdealBeforeMod);
        if (sets != null) {
            sets.get(0).setColors(new int[]{R.color.border}, getApplicationContext());
            sets.get(1).setColors(new int[]{R.color.norm}, getApplicationContext());
            sets.get(2).setColors(new int[]{R.color.border}, getApplicationContext());
            dataSets.add(sets.get(0));
            dataSets.add(sets.get(1));
            dataSets.add(sets.get(2));
        }
    }

    /**
     * Initialisation of ideal charts
     *
     * @param context app Context
     */
    private void initIdealLabels(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String birthday = sp.getString(SharedConstants.BABY_BIRTHDAY, "");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        try {
            calendar.setTime(sd.parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        labelsIdeal.add(getFormattedDate(calendar));
        for (int i = 1; i <= 12; i++) {
            calendar.add(Calendar.MONTH, 1);
            labelsIdeal.add(getFormattedDate(calendar));
        }
    }

    /**
     * Get entries for boys ideal chart
     *
     * @param dbName DB name
     * @return entries list for ideal chart
     */
    private ArrayList<LineDataSet> initBoy(String dbName, ArrayList<String> labelsIdealBeforeMod) {
        ArrayList<LineDataSet> setsList = new ArrayList<>();
        LineDataSet lineDataSetNorm = null;
        LineDataSet lineDataSetMin = null;
        LineDataSet lineDataSetMax = null;
        ArrayList<String> boyParamsHeightNorm = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.heightBoyNorm)));
        ArrayList<String> boyParamsWeightNorm = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.weightBoyNorm)));
        ArrayList<String> boyParamsHeightMin = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.heightBoyMin)));
        ArrayList<String> boyParamsWeightMin = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.weightBoyMin)));
        ArrayList<String> boyParamsHeightMax = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.heightBoyMax)));
        ArrayList<String> boyParamsWeightMax = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.weightBoyMax)));
        ArrayList<Entry> boyHeightNorm = new ArrayList<>();
        ArrayList<Entry> boyWeightNorm = new ArrayList<>();
        ArrayList<Entry> boyHeightMin = new ArrayList<>();
        ArrayList<Entry> boyWeightMin = new ArrayList<>();
        ArrayList<Entry> boyHeightMax = new ArrayList<>();
        ArrayList<Entry> boyWeightMax = new ArrayList<>();
        for (int i = 0; i < boyParamsHeightNorm.size(); i++) {
            int xIndex = findNewXIndex(i,  labelsIdealBeforeMod);
            boyHeightNorm.add(new Entry((float) Double.parseDouble(boyParamsHeightNorm.get(i)), xIndex));
            boyWeightNorm.add(new Entry((float) Double.parseDouble(boyParamsWeightNorm.get(i)), xIndex));
            boyHeightMin.add(new Entry((float) Double.parseDouble(boyParamsHeightMin.get(i)), xIndex));
            boyWeightMin.add(new Entry((float) Double.parseDouble(boyParamsWeightMin.get(i)), xIndex));
            boyHeightMax.add(new Entry((float) Double.parseDouble(boyParamsHeightMax.get(i)), xIndex));
            boyWeightMax.add(new Entry((float) Double.parseDouble(boyParamsWeightMax.get(i)), xIndex));
        }
        if (dbName.equals(getString(R.string.height_word))) {
            lineDataSetNorm = new LineDataSet(boyHeightNorm, getString(R.string.ideal_height));
            lineDataSetMin = new LineDataSet(boyHeightMin, getString(R.string.min_height));
            lineDataSetMax = new LineDataSet(boyHeightMax, getString(R.string.max_height));
        }
        if (dbName.equals(getString(R.string.weight_word))) {
            lineDataSetNorm = new LineDataSet(boyWeightNorm, getString(R.string.ideal_weight));
            lineDataSetMin = new LineDataSet(boyWeightMin, getString(R.string.min_weight));
            lineDataSetMax = new LineDataSet(boyWeightMax, getString(R.string.max_weight));
        }
        setsList.add(lineDataSetMin);
        setsList.add(lineDataSetNorm);
        setsList.add(lineDataSetMax);
        return setsList;
    }

    /**
     * Get entries for girls ideal chart
     *
     * @param dbName DB name
     * @return entries list for ideal chart
     */
    private ArrayList<LineDataSet> initGirl(String dbName,  ArrayList<String> labelsIdealBeforeMod) {
        ArrayList<LineDataSet> setsList = new ArrayList<>();
        LineDataSet lineDataSetNorm = null;
        LineDataSet lineDataSetMin = null;
        LineDataSet lineDataSetMax = null;
        ArrayList<String> girlParamsHeightNorm = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.heightGirlNorm)));
        ArrayList<String> girlParamsWeightNorm = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.weightGirlNorm)));
        ArrayList<String> girlParamsHeightMin = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.heightGirlMin)));
        ArrayList<String> girlParamsWeightMin = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.weightGirlMin)));
        ArrayList<String> girlParamsHeightMax = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.heightGirlMax)));
        ArrayList<String> girlParamsWeightMax = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.weightGirlMax)));
        ArrayList<Entry> girlHeightNorm = new ArrayList<>();
        ArrayList<Entry> girlWeightNorm = new ArrayList<>();
        ArrayList<Entry> girlHeightMin = new ArrayList<>();
        ArrayList<Entry> girlWeightMin = new ArrayList<>();
        ArrayList<Entry> girlHeightMax = new ArrayList<>();
        ArrayList<Entry> girlWeightMax = new ArrayList<>();
        for (int i = 0; i < girlParamsHeightNorm.size(); i++) {
            int xIndex = findNewXIndex(i,  labelsIdealBeforeMod);
            girlHeightNorm.add(new Entry((float) Double.parseDouble(girlParamsHeightNorm.get(i)), xIndex));
            girlWeightNorm.add(new Entry((float) Double.parseDouble(girlParamsWeightNorm.get(i)), xIndex));
            girlHeightMin.add(new Entry((float) Double.parseDouble(girlParamsHeightMin.get(i)), xIndex));
            girlWeightMin.add(new Entry((float) Double.parseDouble(girlParamsWeightMin.get(i)), xIndex));
            girlHeightMax.add(new Entry((float) Double.parseDouble(girlParamsHeightMax.get(i)), xIndex));
            girlWeightMax.add(new Entry((float) Double.parseDouble(girlParamsWeightMax.get(i)), xIndex));
        }
        if (dbName.equals(getString(R.string.height_word))) {
            lineDataSetNorm = new LineDataSet(girlHeightNorm, getString(R.string.ideal_height));
            lineDataSetMin = new LineDataSet(girlHeightMin, getString(R.string.min_height));
            lineDataSetMax = new LineDataSet(girlHeightMax, getString(R.string.max_height));
        }
        if (dbName.equals(getString(R.string.weight_word))) {
            lineDataSetNorm = new LineDataSet(girlWeightNorm, getString(R.string.ideal_weight));
            lineDataSetMin = new LineDataSet(girlWeightMin, getString(R.string.min_weight));
            lineDataSetMax = new LineDataSet(girlWeightMax, getString(R.string.max_weight));
        }
        setsList.add(lineDataSetMin);
        setsList.add(lineDataSetNorm);
        setsList.add(lineDataSetMax);
        return setsList;
    }

    private int findNewXIndex(int i,  ArrayList<String> labelsIdealBeforeMod) {
        String date = labelsIdealBeforeMod.get(i);
        return labelsIdeal.indexOf(date);
    }

    //endregion

    //region Fill charts with data

    /**
     * Fill baby chart with data
     *
     * @param dataSnapshot     snapshot
     * @param dbName           DB name
     * @param selectedItemName selected item name
     */
    private void fillChartBaby(DataSnapshot dataSnapshot, String dbName, String selectedItemName) {
        entries = new ArrayList<>();
        labels = new ArrayList<>();
        int counter = 0;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            if (dbName.equals(DatabaseNames.METRICS)) {
                Metrics m = snapshot.getValue(Metrics.class);
                double weight, height;
                weight = m.getWeight();
                height = m.getHeight();
                if (selectedItemName.equals(features.get(0))) {
                    chart.setDescription(getString(R.string.height_legend));
                    if (height != 0) // not the weight entry
                    {
                        labels.add(m.getDate());
                        entries.add(new Entry((float) height, counter));
                        counter++;
                    }
                } else {
                    chart.setDescription(getString(R.string.weight_legend));
                    if (weight != 0) { // not the height entry
                        labels.add(m.getDate());
                        entries.add(new Entry((float) weight, counter));
                        counter++;
                    }
                }
                continue;
            }
            if (dbName.equals(DatabaseNames.FOOD)) {
                chart.setDescription(getString(R.string.food_legend));
                Food f = snapshot.getValue(Food.class);
                labels.add(f.getDate());
                entries.add(new Entry(f.getHowMuch(), counter));
                counter++;
                continue;
            }
            if (dbName.equals(DatabaseNames.ILLNESS)) {
                chart.setDescription(getString(R.string.illness_legend));
                Illness i = snapshot.getValue(Illness.class);
                labels.add(i.getDate());
                entries.add(new Entry((float) i.getTemperature(), counter));
                counter++;
                continue;
            }
            if (dbName.equals(DatabaseNames.OUTDOOR)) {
                chart.setDescription(getString(R.string.outdoor_legend));
                Outdoor o = snapshot.getValue(Outdoor.class);
                labels.add(o.getDate());
                entries.add(new Entry((float) o.getLength(), counter));
                counter++;
                continue;
            }
            if (dbName.equals(DatabaseNames.SLEEP)) {
                chart.setDescription(getString(R.string.sleep_legend));
                Sleep s = snapshot.getValue(Sleep.class);
                labels.add(s.getDate());
                entries.add(new Entry((float) s.getLength(), counter));
                counter++;
                continue;
            }
            if (dbName.equals(DatabaseNames.STOOL)) {
                chart.setDescription(getString(R.string.stool_legend));
                Stool s = snapshot.getValue(Stool.class);
                labels.add(s.getDate());
                entries.add(new Entry(s.getHowMuch(), counter));
                counter++;
            }
        }
        LineDataSet lineDataSet = new LineDataSet(entries, dbName);
        lineDataSet.setColors(new int[]{R.color.colorPrimary}, getApplicationContext());
        dataSets.add(lineDataSet);
        if (selectedItemName.equals(features.get(0)) || selectedItemName.equals(features.get(1))) // height and weight only
        {
            initIdealLabels(getApplicationContext());
            ArrayList<String> labelsIdealBeforeMod = new ArrayList<>(labelsIdeal);
            formRightLabels();
            initIdealDataSets(getApplicationContext(), selectedItemName, labelsIdealBeforeMod); // add the ideal data to chart
        }else{
            labelsIdeal = labels;
        }

        LineData lineData = new LineData(labelsIdeal, dataSets);
        chart.setData(lineData);
        chart.animateY(animationDuration);
    }


    private void formRightLabels() {
        for (int j = 0; j < labels.size(); j++) {
            String label = labels.get(j);
            for (int i = 0; i < labelsIdeal.size() - 1; i++) {
                try {
                    Date dBefore = stringToDate(labelsIdeal.get(i));
                    Date dAfter = stringToDate(labelsIdeal.get(i + 1));
                    Date newD = stringToDate(label);
                    Calendar myCal = Calendar.getInstance();
                    myCal.setTime(newD);
                    if (dBefore.getTime() < newD.getTime() && newD.getTime() < dAfter.getTime()) {
                        labelsIdeal.add(i + 1, getFormattedDate(myCal));
                        break;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        //change xIndexes according to new labels
        for (int j = 0; j < entries.size(); j++) {
            Date cur = null;
            try {
                cur = stringToDate(labels.get(j));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < labelsIdeal.size(); i++) {
                try {
                    Date d = stringToDate(labelsIdeal.get(i));
                    if(d.getTime() == cur.getTime())
                    {
                        entries.get(j).setXIndex(i);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Fill mom chart with data
     *
     * @param sumData aggregated 1 month long data
     */
    private void fillChartMom(ArrayList<Pair<DataType, Pair<String, String>>> sumData) {
        entries = new ArrayList<>();
        labels = new ArrayList<>();
        if (sumData.size() == 0) { // empty result list
            NotificationsShow.showToast(getApplicationContext(), getString(R.string.no_data_chart));
            if (dialog != null)
                dialog.dismiss();
            LineData lineData = new LineData(labels, dataSets);
            chart.setData(lineData);
            return;
        }
        int counter = 0;
        for (int i = 0; i < sumData.size(); i++) {
            Pair<DataType, Pair<String, String>> it = sumData.get(i);
            Entry e = null;
            DataType type = it.first;
            String date = it.second.first;
            String value = it.second.second;
            switch (spinnerSelectedIndex) {
                case 0: {
                    if (type == DataType.TYPE_STEP_COUNT_DELTA) {
                        e = new Entry((float) Double.parseDouble(value), counter);
                        counter++;
                    }
                    break;
                }
                case 1: {
                    if (type == DataType.TYPE_ACTIVITY_SEGMENT) {
                        value = value.replace(getString(R.string.sleep_str), "").replace(getString(R.string.hours_str), "").replace(",", ".").trim();
                        e = new Entry((float) Double.parseDouble(value), counter);
                        counter++;
                    }
                    break;
                }
                case 2: {
                    if (type == DataType.TYPE_WEIGHT) {
                        e = new Entry((float) Double.parseDouble(value), counter);
                        counter++;
                    }
                    break;
                }
                case 3: {
                    if (type == DataType.TYPE_CALORIES_EXPENDED) {
                        e = new Entry((float) Double.parseDouble(value), counter);
                        counter++;
                    }
                    break;
                }
            }
            if (e != null) {
                entries.add(e);
                labels.add(date);
            }
        }

        LineDataSet lineDataSet = new LineDataSet(entries, getMomChartName());
        lineDataSet.setColors(new int[]{R.color.colorPrimary}, getApplicationContext());
        dataSets.add(lineDataSet);
        LineData lineData = new LineData(labels, dataSets);
        chart.setData(lineData);
        chart.animateY(animationDuration);
        dialog.dismiss();
    }

    //endregion

    //region Fit service connection

    /**
     * Request connection to GoogleFit
     */
    private void requestFitConnection() {
        Intent service = new Intent(this, GoogleFitService.class);
        service.putExtra(SERVICE_REQUEST_TYPE, TYPE_REQUEST_CONNECTION);
        startService(service);
    }

    /**
     * Broadcast service status receiver
     */
    private BroadcastReceiver mFitStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra(FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE) &&
                    intent.hasExtra(FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE)) {
                //Recreate the connection result
                int statusCode = intent.getIntExtra(FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE, 0);
                PendingIntent pendingIntent = intent.getParcelableExtra(FIT_EXTRA_NOTIFY_FAILED_INTENT);
                ConnectionResult result = new ConnectionResult(statusCode, pendingIntent);
                Log.d(FragmentMom.TAG, "Fit connection failed - opening connect screen");
                fitHandleFailedConnection(result);
            }
            if (intent.hasExtra(FIT_EXTRA_CONNECTION_MESSAGE)) {
                Log.d(FragmentMom.TAG, "Fit connection successful - closing connect screen if it's open");
            }
        }
    };

    /**
     * Broadcast service data receiver
     */
    private BroadcastReceiver mFitDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra(HISTORY_EXTRA_AGGREGATED)) {
                ArrayList<Pair<DataType, Pair<String, String>>> sumData = (ArrayList<Pair<DataType, Pair<String, String>>>) intent.getSerializableExtra(HISTORY_EXTRA_AGGREGATED);
                fillChartMom(sumData);
            }
        }
    };

    /**
     * Connection errors handler
     *
     * @param result connection result
     */
    private void fitHandleFailedConnection(ConnectionResult result) {
        Log.i(FragmentMom.TAG, "Activity Thread Google Fit Connection failed. Cause: " + result.toString());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an authorization dialog is displayed to the user.
        if (!authInProgress) {
            if (result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
                try {
                    Log.d(FragmentMom.TAG, "Google Fit connection failed with OAuth failure.  Trying to ask for consent (again)");
                    result.startResolutionForResult(this, REQUEST_OAUTH);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(FragmentMom.TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            } else {
                Log.i(FragmentMom.TAG, "Activity Thread Google Fit Attempting to resolve failed connection");
                mFitResultResolution = result;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fitSaveInstanceState(outState);
    }

    /**
     * Save the state of an instance
     *
     * @param outState state
     */
    private void fitSaveInstanceState(Bundle outState) {
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fitActivityResult(requestCode, resultCode);
    }

    /**
     * Process the result of fit activity connection
     *
     * @param requestCode code
     * @param resultCode  result
     */
    private void fitActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                Log.d(FragmentMom.TAG, "Fit auth completed. Asking for reconnect");
                requestFitConnection();
            } else {
                try {
                    authInProgress = true;
                    mFitResultResolution.startResolutionForResult(this, REQUEST_OAUTH);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(FragmentMom.TAG, "Activity Thread Google Fit Exception while starting resolution activity", e);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFitStatusReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mFitDataReceiver);
        super.onDestroy();
    }

    //endregion

    @Override
    public void onPause() {
        super.onPause();
        if (dialog != null)
            dialog.dismiss();
    }
}