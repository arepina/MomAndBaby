package com.repina.anastasia.momandbaby.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Adapters.CustomGrid;
import com.repina.anastasia.momandbaby.Helpers.ToastShow;
import com.repina.anastasia.momandbaby.R;

public class ChooseFeatureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_feature);

        this.setFinishOnTouchOutside(false);

        int activityCode = getIntent().getExtras().getInt("requestCode");

        final String[] features;
        int[] imageId;

        if (activityCode == 0) // baby
        {
            imageId = new int[]{
                    R.mipmap.height,
                    R.mipmap.weight,
                    R.mipmap.diapers,
                    R.mipmap.vaccination,
                    R.mipmap.illness,
                    R.mipmap.food,
                    R.mipmap.outdoor,
                    R.mipmap.sleep,
                    R.mipmap.other
            };
            features = getResources().getStringArray(R.array.parametersBaby);
        } else // mom
        {
            imageId = new int[]{
                    R.mipmap.weight,
                    R.mipmap.food,
                    R.mipmap.sleep,
                    R.mipmap.google_fit
            };
            features = getResources().getStringArray(R.array.parametersMom);
        }

        CustomGrid adapter = new CustomGrid(ChooseFeatureActivity.this, features, imageId);

        GridView grid = (GridView) findViewById(R.id.gridView);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(features[position].equals(getString(R.string.google_fit))) {
                    ToastShow.show(v.getContext(), getString(R.string.google_fit_notification));
                    finish();//back to main screen
                }
                else if (ConnectionDetector.isConnected(getApplicationContext())) {
                    Intent nextActivity = new Intent(getApplicationContext(), NewFeatureActivity.class);
                    nextActivity.putExtra("data", features[position]);
                    startActivity(nextActivity);
                    finish();//back to main screen
                }
            }
        });

    }
}
