package com.repina.anastasia.momandbaby.Activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Adapters.ListViewArrayAdapter;
import com.repina.anastasia.momandbaby.Adapters.ListViewItem;
import com.repina.anastasia.momandbaby.Classes.SharedConstants;
import com.repina.anastasia.momandbaby.Classes.ToastShow;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.Vaccination;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;

public class VaccinationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccination);

        if (ConnectionDetector.isConnected(getApplicationContext()))
            loadVaccinations();
    }

    private void loadVaccinations() {
        SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        String babyId = sp.getString(SharedConstants.BABY_ID_KEY, "");
        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();
        DatabaseReference databaseReference = database.getReference().child(DatabaseNames.VACCINATION);
        databaseReference.orderByChild("babyId").
                equalTo(babyId).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<ListViewItem> items = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Vaccination v = snapshot.getValue(Vaccination.class);
                                items.add(new ListViewItem(v.getVaccinationName(), 1));
                            }
                            ListView lv = (ListView) findViewById(R.id.vaccinations);
                            ListViewArrayAdapter adapter = new ListViewArrayAdapter(getApplicationContext(), items);
                            lv.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        ToastShow.show(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }

}
