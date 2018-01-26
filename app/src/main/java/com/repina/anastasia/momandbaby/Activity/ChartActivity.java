package com.repina.anastasia.momandbaby.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.Helpers.Processing.StatsProcessing;
import com.repina.anastasia.momandbaby.Helpers.ToastShow;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.Food;
import com.repina.anastasia.momandbaby.DataBase.Illness;
import com.repina.anastasia.momandbaby.DataBase.Metrics;
import com.repina.anastasia.momandbaby.DataBase.Outdoor;
import com.repina.anastasia.momandbaby.DataBase.Sleep;
import com.repina.anastasia.momandbaby.DataBase.Stool;
import com.repina.anastasia.momandbaby.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.repina.anastasia.momandbaby.Activity.TabsActivity.googleFit;


public class ChartActivity extends AppCompatActivity {

    LineChart chart;
    ArrayList<Entry> entries;
    ArrayList<Entry> girlHeight;
    ArrayList<Entry> girlWeight;
    ArrayList<String> labels;
    ArrayList<String> labelsIdeal;
    List<ILineDataSet> dataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        final String type = getIntent().getExtras().getString("Type");


        //https://www.android-examples.com/create-bar-chart-graph-using-mpandroidchart-library/
        //https://github.com/numetriclabz/numAndroidCharts
        chart = (LineChart) findViewById(R.id.graph);

        final ArrayList<String> choose;
        ArrayAdapter<?> adapter;
        if (type.equals("Mom")) {
            adapter = ArrayAdapter.createFromResource(this, R.array.parametersMom, android.R.layout.simple_spinner_item);
            choose = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.parametersMom)));
        } else {
            choose = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.parametersBaby)));
            choose.remove(3);//no vaccinations
            choose.remove(7);//no other
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, choose);
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataSets = new ArrayList<>();
                if (type.equals("Mom")) {
                    String selectedItemName = choose.get(position);
                    getValuesFromGoogleFit(selectedItemName);
                } else {
                    if (ConnectionDetector.isConnected(view.getContext())) {
                        String selectedItemName = choose.get(position);
                        SharedPreferences sp = view.getContext().getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
                        String babyId = sp.getString(SharedConstants.BABY_ID_KEY, "");
                        FirebaseConnection connection = new FirebaseConnection();
                        FirebaseDatabase database = connection.getDatabase();
                        initIdealChartData(getApplicationContext(), selectedItemName); // add the ideal data to chart
                        getValuesFromFirebase(database,
                                valueToDBNameConvert(selectedItemName, choose),
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

    public static String valueToDBNameConvert(String value, ArrayList<String> features) {
        if (value.equals(features.get(0))) return DatabaseNames.METRICS;
        if (value.equals(features.get(1))) return DatabaseNames.METRICS;
        if (value.equals(features.get(2))) return DatabaseNames.STOOL;
        if (value.equals(features.get(3))) return DatabaseNames.ILLNESS;
        if (value.equals(features.get(4))) return DatabaseNames.FOOD;
        if (value.equals(features.get(5))) return DatabaseNames.OUTDOOR;
        if (value.equals(features.get(6))) return DatabaseNames.SLEEP;
        return "";
    }

    void getValuesFromFirebase(final FirebaseDatabase database, final String dbName,
                               final String id, final String selectedItemName) {
        DatabaseReference databaseReference = database.getReference().child(dbName);
        databaseReference.orderByChild("babyId").
                equalTo(id).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            fillChartBaby(dataSnapshot, dbName, selectedItemName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        ToastShow.show(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }

    private void getValuesFromGoogleFit(String selectedItemName) {
        StatsProcessing.getMomStatsForPeriod(googleFit,
                new GridItemArrayAdapter(getApplicationContext(), R.layout.custom_row),
                Calendar.getInstance(), this, null, 31, false, true);
    }

    private void initIdealChartData(Context context, String dbName) {
        SharedPreferences sp = context.getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        String gender = sp.getString(SharedConstants.BABY_GENDER_KEY, "");
        String birthday = sp.getString(SharedConstants.BABY_BIRTHDAY, "");
        labelsIdeal = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        try {
            calendar.setTime(sd.parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        labelsIdeal.add(FormattedDate.getFormattedDateWithoutTime(calendar));
        for (int i = 1; i <= 12; i++) {
            calendar.add(Calendar.MONTH, 1);
            labelsIdeal.add(FormattedDate.getFormattedDateWithoutTime(calendar));
        }
        ArrayList<LineDataSet> sets;
        if (gender.equals("boy"))
            sets = initBoy(dbName);
        else
            sets = initGirl(dbName);
        if (sets != null) {
            sets.get(0).setColors(new int[]{R.color.border}, getApplicationContext());
            sets.get(1).setColors(new int[]{R.color.norm}, getApplicationContext());
            sets.get(2).setColors(new int[]{R.color.border}, getApplicationContext());
            dataSets.add(sets.get(0));
            dataSets.add(sets.get(1));
            dataSets.add(sets.get(2));
        }
    }

    private ArrayList<LineDataSet> initBoy(String dbName) {
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
            boyHeightNorm.add(new Entry((float) Double.parseDouble(boyParamsHeightNorm.get(i)), i));
            boyWeightNorm.add(new Entry((float) Double.parseDouble(boyParamsWeightNorm.get(i)), i));
            boyHeightMin.add(new Entry((float) Double.parseDouble(boyParamsHeightMin.get(i)), i));
            boyWeightMin.add(new Entry((float) Double.parseDouble(boyParamsWeightMin.get(i)), i));
            boyHeightMax.add(new Entry((float) Double.parseDouble(boyParamsHeightMax.get(i)), i));
            boyWeightMax.add(new Entry((float) Double.parseDouble(boyParamsWeightMax.get(i)), i));
        }
        if (dbName.equals("Рост")) {
            lineDataSetNorm = new LineDataSet(boyHeightNorm, getString(R.string.ideal_height));
            lineDataSetMin = new LineDataSet(boyHeightMin, getString(R.string.min_height));
            lineDataSetMax = new LineDataSet(boyHeightMax, getString(R.string.max_height));
        }
        if (dbName.equals("Вес")) {
            lineDataSetNorm = new LineDataSet(boyWeightNorm, getString(R.string.ideal_weight));
            lineDataSetMin = new LineDataSet(boyWeightMin, getString(R.string.min_weight));
            lineDataSetMax = new LineDataSet(boyWeightMax, getString(R.string.max_weight));
        }
        setsList.add(lineDataSetMin);
        setsList.add(lineDataSetNorm);
        setsList.add(lineDataSetMax);
        return setsList;
    }

    private ArrayList<LineDataSet> initGirl(String dbName) {
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
            girlHeightNorm.add(new Entry((float) Double.parseDouble(girlParamsHeightNorm.get(i)), i));
            girlWeightNorm.add(new Entry((float) Double.parseDouble(girlParamsWeightNorm.get(i)), i));
            girlHeightMin.add(new Entry((float) Double.parseDouble(girlParamsHeightMin.get(i)), i));
            girlWeightMin.add(new Entry((float) Double.parseDouble(girlParamsWeightMin.get(i)), i));
            girlHeightMax.add(new Entry((float) Double.parseDouble(girlParamsHeightMax.get(i)), i));
            girlWeightMax.add(new Entry((float) Double.parseDouble(girlParamsWeightMax.get(i)), i));
        }
        if (dbName.equals("Рост")) {
            lineDataSetNorm = new LineDataSet(girlHeightNorm, getString(R.string.ideal_height));
            lineDataSetMin = new LineDataSet(girlHeightMin, getString(R.string.min_height));
            lineDataSetMax = new LineDataSet(girlHeightMax, getString(R.string.max_height));
        }
        if (dbName.equals("Вес")) {
            lineDataSetNorm = new LineDataSet(girlWeightNorm, getString(R.string.ideal_weight));
            lineDataSetMin = new LineDataSet(girlWeightMin, getString(R.string.min_weight));
            lineDataSetMax = new LineDataSet(girlWeightMax, getString(R.string.max_weight));
        }
        setsList.add(lineDataSetMin);
        setsList.add(lineDataSetNorm);
        setsList.add(lineDataSetMax);
        return setsList;
    }

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
                if (selectedItemName.equals("Рост")) {
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
        LineData lineData = new LineData(labelsIdeal, dataSets);
        chart.setData(lineData);
        chart.animateY(2000);
    }

    public static void fillChartMom(GridItemArrayAdapter adapter) {
        //todo
//        switch (value)
//        {
//            case "Сон":{}
//            case "Шаги":{}
//            case "Калории":{}
//            case "Питание":{}
//            case "Вес":{}
//        }
    }
}