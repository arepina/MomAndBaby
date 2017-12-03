package com.repina.anastasia.momandbaby.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.repina.anastasia.momandbaby.R;

public class NewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        GridView grid = (GridView)findViewById(R.id.gridView);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.parameters, android.R.layout.simple_gallery_item);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(NewActivity.this, "Position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
