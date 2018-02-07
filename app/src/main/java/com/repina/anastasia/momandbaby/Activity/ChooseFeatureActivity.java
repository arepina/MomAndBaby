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
import com.repina.anastasia.momandbaby.R;

import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.CALLING;

public class ChooseFeatureActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_feature);

        this.setFinishOnTouchOutside(false);

        final String[] features;
        int[] imageId;

        imageId = new int[]{
                R.mipmap.height,
                R.mipmap.weight,
                R.mipmap.diapers,
                R.mipmap.vaccination,
                R.mipmap.illness,
                R.mipmap.food,
                R.mipmap.outdoor,
                R.mipmap.sleep,
                R.mipmap.other,
                R.mipmap.tooth
        };
        features = getResources().getStringArray(R.array.parametersBaby);

        CustomGrid adapter = new CustomGrid(ChooseFeatureActivity.this, features, imageId);

        GridView grid = (GridView) findViewById(R.id.gridView);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {
                    if(features[position].equals(features[9])) // teeth
                    {
                        Intent nextActivity = new Intent(getApplicationContext(), TeethActivity.class);
                        nextActivity.putExtra("data", features[position]);
                        nextActivity.putExtra(CALLING, ChooseFeatureActivity.class.toString());
                        startActivity(nextActivity);
                        finish();//back to main screen
                    }else { // all the others
                        Intent nextActivity = new Intent(getApplicationContext(), NewFeatureActivity.class);
                        nextActivity.putExtra("data", features[position]);
                        startActivity(nextActivity);
                        finish();//back to main screen
                    }
                }
            }
        });

    }
}
