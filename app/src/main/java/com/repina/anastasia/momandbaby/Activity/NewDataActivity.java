package com.repina.anastasia.momandbaby.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.repina.anastasia.momandbaby.R;

import org.w3c.dom.Text;

public class NewDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_data);
        String data = getIntent().getExtras().getString("data");
        Button addData = (Button) findViewById(R.id.addData);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo add new value to Firebase
                finish();
            }
        });
        changeLayout(data);
    }

    private void changeLayout(String data) {
        TextView dataName1 = (TextView) findViewById(R.id.dataName1);
        EditText dataValue1 = (EditText) findViewById(R.id.dataValue1);
        TextView dataName2 = (TextView) findViewById(R.id.dataName2);
        EditText dataValue2 = (EditText) findViewById(R.id.dataValue2);
        TextView dataName3 = (TextView) findViewById(R.id.dataName3);
        EditText dataValue3 = (EditText) findViewById(R.id.dataValue3);
        LinearLayout rateData = (LinearLayout) findViewById(R.id.rateData);
        Spinner vaccinationsData = (Spinner) findViewById(R.id.vaccinationsData);
        String[] features = getResources().getStringArray(R.array.parameters);
        if (data.equals(features[0])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.height));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[1])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.weight));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);

        }
        if (data.equals(features[2])) {
            dataName2.setText(getString(R.string.rateValue) + " " + getString(R.string.diapers));
            dataName2.setVisibility(View.VISIBLE);
            rateData.setVisibility(View.VISIBLE);
            dataName3.setText(R.string.Comment);
            dataName3.setVisibility(View.VISIBLE);
            dataValue3.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[3])) {
            dataName1.setText(getString(R.string.vaccination));
            dataName1.setVisibility(View.VISIBLE);
            ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.vaccinations, android.R.layout.simple_spinner_item);
            vaccinationsData.setAdapter(adapter);
            vaccinationsData.setSelection(0);
            vaccinationsData.setVisibility(View.VISIBLE);
            dataName2.setText(getString(R.string.add_vaccination));
            dataName2.setVisibility(View.VISIBLE);
            dataValue2.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[4])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.temperature));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
            dataName2.setText(getString(R.string.add_symptomes));
            dataName2.setVisibility(View.VISIBLE);
            dataValue2.setVisibility(View.VISIBLE);
            dataName3.setText(getString(R.string.add_pills));
            dataName3.setVisibility(View.VISIBLE);
            dataValue3.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[5])) {
            dataName2.setText(getString(R.string.rateValue) + getString(R.string.food));
            dataName2.setVisibility(View.VISIBLE);
            rateData.setVisibility(View.VISIBLE);
            dataName3.setText(R.string.Comment);
            dataName3.setVisibility(View.VISIBLE);
            dataValue3.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[6])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.outdoor_duration));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
        }
        if (data.equals(features[7])) {
            dataName1.setText(getString(R.string.add_new_data) + " " + getString(R.string.sleep_duration));
            dataName1.setVisibility(View.VISIBLE);
            dataValue1.setVisibility(View.VISIBLE);
        }
    }
}

