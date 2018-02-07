package com.repina.anastasia.momandbaby.Activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.Teeth;
import com.repina.anastasia.momandbaby.Helpers.FormattedDate;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class TeethActivity extends AppCompatActivity {

    private Teeth teeth;
    private String birthday;
    private String babyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teeth);

        FirebaseConnection connection = new FirebaseConnection();
        final FirebaseDatabase database = connection.getDatabase();
        SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
        babyId = sp.getString(SharedConstants.BABY_ID_KEY, "");
        birthday = sp.getString(SharedConstants.BABY_BIRTHDAY, "");

        Button addData = (Button) findViewById(R.id.addData);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {
                    addNewValueToFirebase(database);
                    finish();//back to choosing
                }
            }
        });

        initFABs();

        getTeethFromFirebase(database, babyId);
    }

    // region Data update

    private int getMonthBetween() {
        Calendar today = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        Date date = null;
        try {
            date = FormattedDate.stringToDate(birthday);
        } catch (Exception ignored) {
        }
        birth.setTime(date);
        int numMonths = (today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)) * 12;
        numMonths = numMonths + ((12 - (birth.get(Calendar.MONTH) - 1) + (today.get(Calendar.MONTH) - 12)));
        return numMonths;
    }

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void initLists() {
        ArrayList<Pair<Integer, Boolean>> doesHave = teeth.getDoesHave();
        ArrayList<Pair<Integer, String>> whenHave = teeth.getWhenHave();
        if (doesHave == null) // we have not added the teeth data before
        {
            doesHave = new ArrayList<>();
            whenHave = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                doesHave.add(new Pair<>(i, false));
                whenHave.add(new Pair<>(i, ""));
            }
            teeth.setDoesHave(doesHave);
            teeth.setWhenHave(whenHave);
            teeth.setBabyId(babyId);
        }
    }

    private void updateListsInfo(int i) {
        initLists();
        ArrayList<Pair<Integer, Boolean>> doesHave = teeth.getDoesHave();
        ArrayList<Pair<Integer, String>> whenHave = teeth.getWhenHave();
        doesHave.set(i, new Pair<>(i, true));
        whenHave.set(i, new Pair<>(i, FormattedDate.getFormattedDate(Calendar.getInstance())));
    }

    //endregion

    //region FABs

    private void initFABs() {
        initRed();
        initYellow();
        initGreen();
        initBlue();
        initPurple();
    }

    private void initRed() {
        final FloatingActionButton q1 = (FloatingActionButton) findViewById(R.id.q1);
        q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q1.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(0);
            }
        });

        final FloatingActionButton q2 = (FloatingActionButton) findViewById(R.id.q2);
        q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q2.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(1);
            }
        });

        final FloatingActionButton q11 = (FloatingActionButton) findViewById(R.id.q11);
        q11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q11.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(10);
            }
        });

        final FloatingActionButton q12 = (FloatingActionButton) findViewById(R.id.q12);
        q12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q12.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(11);
            }
        });
    }

    private void initYellow() {
        final FloatingActionButton q3 = (FloatingActionButton) findViewById(R.id.q3);
        q3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q3.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(2);
            }
        });

        final FloatingActionButton q4 = (FloatingActionButton) findViewById(R.id.q4);
        q4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q4.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(3);
            }
        });

        final FloatingActionButton q13 = (FloatingActionButton) findViewById(R.id.q13);
        q13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q13.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(12);
            }
        });

        final FloatingActionButton q14 = (FloatingActionButton) findViewById(R.id.q14);
        q14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q14.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(13);
            }
        });
    }

    private void initGreen() {
        final FloatingActionButton q5 = (FloatingActionButton) findViewById(R.id.q5);
        q5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q5.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(4);
            }
        });

        final FloatingActionButton q6 = (FloatingActionButton) findViewById(R.id.q6);
        q6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q6.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(5);
            }
        });

        final FloatingActionButton q15 = (FloatingActionButton) findViewById(R.id.q15);
        q15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q15.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(14);
            }
        });

        final FloatingActionButton q16 = (FloatingActionButton) findViewById(R.id.q16);
        q16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q16.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(15);
            }
        });
    }

    private void initBlue() {
        final FloatingActionButton q7 = (FloatingActionButton) findViewById(R.id.q7);
        q7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q7.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(6);
            }
        });

        final FloatingActionButton q8 = (FloatingActionButton) findViewById(R.id.q8);
        q8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q8.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(7);
            }
        });

        final FloatingActionButton q17 = (FloatingActionButton) findViewById(R.id.q17);
        q17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q17.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(16);
            }
        });

        final FloatingActionButton q18 = (FloatingActionButton) findViewById(R.id.q18);
        q18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q18.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(17);
            }
        });
    }

    private void initPurple() {
        final FloatingActionButton q9 = (FloatingActionButton) findViewById(R.id.q9);
        q9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q9.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(8);
            }
        });

        final FloatingActionButton q10 = (FloatingActionButton) findViewById(R.id.q10);
        q10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q10.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(9);
            }
        });

        final FloatingActionButton q19 = (FloatingActionButton) findViewById(R.id.q19);
        q19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q19.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(18);
            }
        });

        final FloatingActionButton q20 = (FloatingActionButton) findViewById(R.id.q20);
        q20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q20.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(19);
            }
        });
    }

    //endregion

    //region Firebase

    private void addNewValueToFirebase(FirebaseDatabase database) {
        if(teeth.getBabyId() != null) {
            DatabaseReference databaseReference = database.getReference().child(DatabaseNames.TEETH);
            databaseReference.push().setValue(teeth);
        }else
            NotificationsShow.showToast(getApplicationContext(), getString(R.string.add_any_data));
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
                            //todo iterate all the teeth and make the true once enabled
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
