package com.repina.anastasia.momandbaby.Activity;

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
import com.repina.anastasia.momandbaby.Classes.ToastShow;
import com.repina.anastasia.momandbaby.DataBase.BandData;
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
        String type = getIntent().getExtras().getString("Type");
        //todo add logic depends on type
        if(type.equals("Mom"))
        {

        }else{

        }

        final String bandCode = getIntent().getStringExtra("address");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.characteristics, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //todo uncomment later
                /*if (ConnectionDetector.isConnected(getApplicationContext())) {
                    String[] choose = getResources().getStringArray(R.array.characteristics);
                    FirebaseConnection connection = new FirebaseConnection();
                    FirebaseDatabase database = connection.getDatabase();
                    getValuesFromFirebase(database, choose[position], bandCode);
                }*/
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

    void getValuesFromFirebase(final FirebaseDatabase database, final String valType, final String bandCode) {
        DatabaseReference databaseReference = database.getReference().child("BandData");
        databaseReference.orderByChild("bandCode").
                equalTo(bandCode).
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
                                switch (valType) {
                                    case "Шаги": {
                                        int steps = bandData.getSteps();
                                        entries.add(new BarEntry(steps, counter));
                                        break;
                                    }
                                    case "Калории": {
                                        double calories = bandData.getCalories();
                                        entries.add(new BarEntry((float)calories, counter));
                                        break;
                                    }
                                    case "Сон": {
                                        double sleep = bandData.getSleepHours();
                                        entries.add(new BarEntry((float)sleep, counter));
                                        break;
                                    }
                                }
                                counter++;
                            }

                            barDataSet = new BarDataSet(entries, valType);
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
