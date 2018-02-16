package com.repina.anastasia.momandbaby.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.R;

import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.CALLING;

/**
 * Statistics choose
 */
public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        CardView cardViewChart = (CardView) findViewById(R.id.chartAnalytics);
        cardViewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {
                    Intent nextActivity = new Intent(v.getContext(), ChartActivity.class);
                    nextActivity.putExtra("Type", "Baby");
                    startActivity(nextActivity);
                }
            }
        });

        CardView cardViewVaccination = (CardView) findViewById(R.id.vaccinationsAnalytics);
        cardViewVaccination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {
                    Intent nextActivity = new Intent(v.getContext(), VaccinationsActivity.class);
                    startActivity(nextActivity);
                }
            }
        });

        CardView cardViewTeeth = (CardView) findViewById(R.id.teethAnalytics);
        cardViewTeeth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {
                    Intent nextActivity = new Intent(v.getContext(), TeethActivity.class);
                    nextActivity.putExtra(CALLING, StatsActivity.class.toString());
                    startActivity(nextActivity);
                }
            }
        });
    }
}
