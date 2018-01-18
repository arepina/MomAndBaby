package com.repina.anastasia.momandbaby.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.R;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        CardView cardViewMom = (CardView) findViewById(R.id.chartAnalytics);
        cardViewMom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionDetector.isConnected(getApplicationContext())) {
                    Intent nextActivity = new Intent(v.getContext(), ChartActivity.class);
                    nextActivity.putExtra("Type", "Baby");
                    startActivity(nextActivity);
                }
            }
        });

        CardView cardViewBaby = (CardView) findViewById(R.id.vaccinationsAnalytics);
        cardViewBaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionDetector.isConnected(getApplicationContext())) {
                    Intent nextActivity = new Intent(v.getContext(), VaccinationsActivity.class);
                    startActivity(nextActivity);
                }
            }
        });
    }
}
