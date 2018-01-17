package com.repina.anastasia.momandbaby.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.Classes.ToastShow;
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

import java.util.ArrayList;
import java.util.Arrays;


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

        final ArrayList<String> choose;
        ArrayAdapter<?> adapter;
        if (type.equals("Mom")) {
            adapter = ArrayAdapter.createFromResource(this, R.array.parametersMom, android.R.layout.simple_spinner_item);
            choose = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.parametersMom)));
        } else {
            choose = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.parametersBaby)));
            choose.remove(3);//no vaccinations
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, choose);
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (type.equals("Mom")) {
                    //todo
                } else {
                    if (ConnectionDetector.isConnected(view.getContext())) {
                            String selectedItemName = choose.get(position);
                            SharedPreferences sp = view.getContext().getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
                            String babyId = sp.getString(SharedConstants.BABY_ID_KEY, "");
                            FirebaseConnection connection = new FirebaseConnection();
                            FirebaseDatabase database = connection.getDatabase();
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

        //https://www.android-examples.com/create-bar-chart-graph-using-mpandroidchart-library/
        //https://github.com/numetriclabz/numAndroidCharts
        chart = (BarChart) findViewById(R.id.graph);
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
                            fillChart(dataSnapshot, dbName, selectedItemName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        ToastShow.show(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }

    private void fillChart(DataSnapshot dataSnapshot, String dbName, String selectedItemName) {
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
                    labels.add(m.getDate());
                    entries.add(new BarEntry((float) height, counter));
                } else {
                    labels.add(m.getDate());
                    entries.add(new BarEntry((float) weight, counter));
                }
                continue;
            }
            if (dbName.equals(DatabaseNames.FOOD)) {
                Food f = snapshot.getValue(Food.class);
                labels.add(f.getDate());
                entries.add(new BarEntry(f.getHowMuch(), counter));
                continue;
            }
            if (dbName.equals(DatabaseNames.ILLNESS)) {
                Illness i = snapshot.getValue(Illness.class);
                labels.add(i.getDate());
                entries.add(new BarEntry((float) i.getTemperature(), counter));
                continue;
            }
            if (dbName.equals(DatabaseNames.OUTDOOR)) {
                Outdoor o = snapshot.getValue(Outdoor.class);
                labels.add(o.getDate());
                entries.add(new BarEntry((float) o.getLength(), counter));
                continue;
            }
            if (dbName.equals(DatabaseNames.SLEEP)) {
                Sleep s = snapshot.getValue(Sleep.class);
                labels.add(s.getDate());
                entries.add(new BarEntry((float) s.getLength(), counter));
                continue;
            }
            if (dbName.equals(DatabaseNames.STOOL)) {
                Stool s = snapshot.getValue(Stool.class);
                labels.add(s.getDate());
                entries.add(new BarEntry(s.getHowMuch(), counter));
                continue;
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