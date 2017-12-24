package com.repina.anastasia.momandbaby.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.repina.anastasia.momandbaby.Classes.ConnectionDetector;
import com.repina.anastasia.momandbaby.Classes.CustomGrid;
import com.repina.anastasia.momandbaby.R;

public class ChooseFeatureActivity extends Activity {

    int[] imageId = {
            R.mipmap.height,
            R.mipmap.weight,
            R.mipmap.diapers,
            R.mipmap.vaccination,
            R.mipmap.illness,
            R.mipmap.food,
            R.mipmap.outdoor,
            R.mipmap.sleep,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_feature);

        this.setFinishOnTouchOutside(false);

        final String[] features = getResources().getStringArray(R.array.parameters);
        CustomGrid adapter = new CustomGrid(ChooseFeatureActivity.this, features, imageId);

        GridView grid = (GridView)findViewById(R.id.gridView);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(ConnectionDetector.isConnected(getApplicationContext())) {
                    Intent nextActivity = new Intent(getApplicationContext(), NewDataActivity.class);
                    nextActivity.putExtra("data", features[position]);
                    startActivity(nextActivity);
                    finish();//back to main screen
                }
            }
        });

    }
}
