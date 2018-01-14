package com.repina.anastasia.momandbaby.Activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Classes.ConnectionDetector;
import com.repina.anastasia.momandbaby.Classes.FirebaseConnection;
import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.Classes.ToastShow;
import com.repina.anastasia.momandbaby.DataBase.BandData;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.Food;
import com.repina.anastasia.momandbaby.DataBase.Illness;
import com.repina.anastasia.momandbaby.DataBase.Metrics;
import com.repina.anastasia.momandbaby.DataBase.Outdoor;
import com.repina.anastasia.momandbaby.DataBase.Sleep;
import com.repina.anastasia.momandbaby.DataBase.Stool;
import com.repina.anastasia.momandbaby.DataBase.Vaccination;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;


public class ChartActivity extends AppCompatActivity {

    BarChart chart;
    ArrayList<BarEntry> entries;
    ArrayList<String> labels;
    BarDataSet barDataSet;
    BarData barData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        final String type = getIntent().getExtras().getString("Type");
        final String[] choose;
        ArrayAdapter<?> adapter;
        if (type.equals("Mom")) {
            adapter = ArrayAdapter.createFromResource(this, R.array.parametersMom, android.R.layout.simple_spinner_item);
            choose = getResources().getStringArray(R.array.parametersMom);
        } else {
            adapter = ArrayAdapter.createFromResource(this, R.array.parametersBaby, android.R.layout.simple_spinner_item);
            choose = getResources().getStringArray(R.array.parametersBaby);
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (type.equals("Mom")) {

                } else {
                    if (ConnectionDetector.isConnected(getApplicationContext())) {
                        String selectedItemName = choose[position];
                        SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
                        String babyId = sp.getString(SharedConstants.BABY_ID_KEY, "");
                        FirebaseConnection connection = new FirebaseConnection();
                        FirebaseDatabase database = connection.getDatabase();
                        getValuesFromFirebase(database, valueToDBNameConvert(selectedItemName, choose), babyId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        //https://www.android-examples.com/create-bar-chart-graph-using-mpandroidchart-library/
        //https://github.com/numetriclabz/numAndroidCharts
        chart = (BarChart) findViewById(R.id.graph);
    }

    private String valueToDBNameConvert(String value, String[] features) {
        if (value.equals(features[0])) return DatabaseNames.METRICS;
        if (value.equals(features[1])) return DatabaseNames.METRICS;
        if (value.equals(features[2])) return DatabaseNames.STOOL;
        if (value.equals(features[3])) return DatabaseNames.VACCINATION;
        if (value.equals(features[4])) return DatabaseNames.ILLNESS;
        if (value.equals(features[5])) return DatabaseNames.FOOD;
        if (value.equals(features[6])) return DatabaseNames.OUTDOOR;
        if (value.equals(features[7])) return DatabaseNames.SLEEP;
        return "";
    }

    void getValuesFromFirebase(final FirebaseDatabase database, final String dbName, final String id) {
        DatabaseReference databaseReference = database.getReference().child(dbName);
        databaseReference.orderByChild("babyId").
                equalTo(id).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            entries = new ArrayList<>();
                            labels = new ArrayList<>();
                            int counter = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                BandData bandData = snapshot.getValue(BandData.class);
                                labels.add(bandData.getDate());
                                switch (dbName) {
                                    case "Шаги": {
                                        int steps = bandData.getSteps();
                                        entries.add(new BarEntry(steps, counter));
                                        break;
                                    }
                                    case "Калории": {
                                        double calories = bandData.getCalories();
                                        entries.add(new BarEntry((float) calories, counter));
                                        break;
                                    }
                                    case "Сон": {
                                        double sleep = bandData.getSleepHours();
                                        entries.add(new BarEntry((float) sleep, counter));
                                        break;
                                    }
                                }
                                counter++;
                            }

                            barDataSet = new BarDataSet(entries, dbName);
                            barData = new BarData(labels, barDataSet);
                            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            chart.setData(barData);
                            chart.animateY(2000);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        ToastShow.show(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }
}
