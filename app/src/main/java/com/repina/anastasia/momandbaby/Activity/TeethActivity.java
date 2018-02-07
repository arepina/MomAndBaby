package com.repina.anastasia.momandbaby.Activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.Food;
import com.repina.anastasia.momandbaby.DataBase.Illness;
import com.repina.anastasia.momandbaby.DataBase.Metrics;
import com.repina.anastasia.momandbaby.DataBase.Other;
import com.repina.anastasia.momandbaby.DataBase.Outdoor;
import com.repina.anastasia.momandbaby.DataBase.Sleep;
import com.repina.anastasia.momandbaby.DataBase.Stool;
import com.repina.anastasia.momandbaby.DataBase.Teeth;
import com.repina.anastasia.momandbaby.DataBase.User;
import com.repina.anastasia.momandbaby.DataBase.Vaccination;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;

public class TeethActivity extends AppCompatActivity {

    private Teeth teeth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teeth);

        FirebaseConnection connection = new FirebaseConnection();
        final FirebaseDatabase database = connection.getDatabase();
        SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        final String babyId = sp.getString(SharedConstants.BABY_ID_KEY, "");

        Button addData = (Button) findViewById(R.id.addData);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {
                    addNewValueToFirebase(database, babyId);
                    finish();//back to choosing
                }
            }
        });

        initFABs();

        getTeethFromFirebase(database, babyId);
    }

    //region FABs

    private void initFABs() {
        initRed();
        initYellow();
        initGreen();
        initBlue();
        initPurple();
    }

    private void initRed() {
        FloatingActionButton q1 = (FloatingActionButton) findViewById(R.id.q1);
        q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q2 = (FloatingActionButton) findViewById(R.id.q2);
        q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q11 = (FloatingActionButton) findViewById(R.id.q11);
        q11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q12 = (FloatingActionButton) findViewById(R.id.q12);
        q12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initYellow() {
        FloatingActionButton q3 = (FloatingActionButton) findViewById(R.id.q3);
        q3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q4 = (FloatingActionButton) findViewById(R.id.q4);
        q4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q13 = (FloatingActionButton) findViewById(R.id.q13);
        q13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q14 = (FloatingActionButton) findViewById(R.id.q14);
        q14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initGreen() {
        FloatingActionButton q5 = (FloatingActionButton) findViewById(R.id.q5);
        q5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q6 = (FloatingActionButton) findViewById(R.id.q6);
        q6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q15 = (FloatingActionButton) findViewById(R.id.q15);
        q15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q16 = (FloatingActionButton) findViewById(R.id.q16);
        q16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initBlue() {
        FloatingActionButton q7 = (FloatingActionButton) findViewById(R.id.q7);
        q7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q8 = (FloatingActionButton) findViewById(R.id.q8);
        q8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q17 = (FloatingActionButton) findViewById(R.id.q17);
        q17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q18 = (FloatingActionButton) findViewById(R.id.q18);
        q18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initPurple() {
        FloatingActionButton q9 = (FloatingActionButton) findViewById(R.id.q9);
        q9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q10 = (FloatingActionButton) findViewById(R.id.q10);
        q10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q19 = (FloatingActionButton) findViewById(R.id.q19);
        q19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FloatingActionButton q20 = (FloatingActionButton) findViewById(R.id.q20);
        q20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //endregion

    //region Firebase

    private void addNewValueToFirebase(FirebaseDatabase database, String babyId) {
        ArrayList<Pair<Integer, Boolean>> doesHave = teeth.getDoesHave();
        ArrayList<Pair<Integer, String>> whenHave = teeth.getWhenHave();
        Teeth t = new Teeth(babyId, doesHave, whenHave);
        DatabaseReference databaseReference = database.getReference().child(DatabaseNames.TEETH);
        databaseReference.push().setValue(t);
    }

    public void getTeethFromFirebase(FirebaseDatabase database, String babyId) {
        final DatabaseReference databaseReference = database.getReference().child(DatabaseNames.TEETH);

        databaseReference.orderByChild("babyId")
                .equalTo(babyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            teeth = snapshot.getValue(Teeth.class);
                        } else
                            teeth = new Teeth();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        NotificationsShow.showToast(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }

    //endregion
}
