package com.repina.anastasia.momandbaby.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.repina.anastasia.momandbaby.Classes.CustomGrid;
import com.repina.anastasia.momandbaby.R;

public class NewActivity extends AppCompatActivity {

    int[] imageId = {
            R.mipmap.height,
            R.mipmap.weight,
            R.mipmap.diapers,
            R.mipmap.tooth,
            R.mipmap.vaccination,
            R.mipmap.illness,
            R.mipmap.food,
            R.mipmap.outdoor,
            R.mipmap.sleep,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        final String[] features = getResources().getStringArray(R.array.parameters);
        CustomGrid adapter = new CustomGrid(NewActivity.this, features, imageId);

        GridView grid = (GridView)findViewById(R.id.gridView);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(NewActivity.this, "You Clicked at " + features[position], Toast.LENGTH_SHORT).show();
            }
        });

    }
}
