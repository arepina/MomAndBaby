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
import static com.repina.anastasia.momandbaby.Helpers.LocalConstants.CALLING;

public class TeethActivity extends AppCompatActivity {

    private Teeth teeth;
    private String birthday;
    private String babyId;

    private String calling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teeth);

        calling = getIntent().getStringExtra(CALLING);

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

        if (calling.equals(StatsActivity.class.toString())) // the user just watch the teeth
            makeEnabledFalse();

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
        ArrayList<Boolean> doesHave = teeth.getDoesHave();
        ArrayList<Integer> whenHave = teeth.getWhenHave();
        if (doesHave == null) // we have not added the teeth data before
        {
            doesHave = new ArrayList<>();
            whenHave = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                doesHave.add(false);
                whenHave.add(-1);
            }
            teeth.setDoesHave(doesHave);
            teeth.setWhenHave(whenHave);
            teeth.setBabyId(babyId);
        }
    }

    private void updateListsInfo(int i, int value) {
        initLists();
        ArrayList<Boolean> doesHave = teeth.getDoesHave();
        ArrayList<Integer> whenHave = teeth.getWhenHave();
        doesHave.set(i, true);
        whenHave.set(i, value);
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

    private void makeEnabledFalse() {
        for (int i = 1; i <= 20; i++) {
            FloatingActionButton button = (FloatingActionButton) findViewById(getResources().getIdentifier("q" + i, "id",
                    this.getPackageName()));
            button.setEnabled(false);
        }
    }

    private void initRed() {
        final FloatingActionButton q1 = (FloatingActionButton) findViewById(R.id.q1);
        q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q1.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(0, months);
            }
        });

        final FloatingActionButton q2 = (FloatingActionButton) findViewById(R.id.q2);
        q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q2.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(1, months);
            }
        });

        final FloatingActionButton q11 = (FloatingActionButton) findViewById(R.id.q11);
        q11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q11.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(10, months);
            }
        });

        final FloatingActionButton q12 = (FloatingActionButton) findViewById(R.id.q12);
        q12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q12.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(11, months);
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
                updateListsInfo(2, months);
            }
        });

        final FloatingActionButton q4 = (FloatingActionButton) findViewById(R.id.q4);
        q4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q4.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(3, months);
            }
        });

        final FloatingActionButton q13 = (FloatingActionButton) findViewById(R.id.q13);
        q13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q13.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(12, months);
            }
        });

        final FloatingActionButton q14 = (FloatingActionButton) findViewById(R.id.q14);
        q14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q14.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(13, months);
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
                updateListsInfo(4, months);
            }
        });

        final FloatingActionButton q6 = (FloatingActionButton) findViewById(R.id.q6);
        q6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q6.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(5, months);
            }
        });

        final FloatingActionButton q15 = (FloatingActionButton) findViewById(R.id.q15);
        q15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q15.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(14, months);
            }
        });

        final FloatingActionButton q16 = (FloatingActionButton) findViewById(R.id.q16);
        q16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q16.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(15, months);
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
                updateListsInfo(6, months);
            }
        });

        final FloatingActionButton q8 = (FloatingActionButton) findViewById(R.id.q8);
        q8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q8.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(7, months);
            }
        });

        final FloatingActionButton q17 = (FloatingActionButton) findViewById(R.id.q17);
        q17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q17.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(16, months);
            }
        });

        final FloatingActionButton q18 = (FloatingActionButton) findViewById(R.id.q18);
        q18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q18.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(17, months);
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
                updateListsInfo(8, months);
            }
        });

        final FloatingActionButton q10 = (FloatingActionButton) findViewById(R.id.q10);
        q10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q10.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(9, months);
            }
        });

        final FloatingActionButton q19 = (FloatingActionButton) findViewById(R.id.q19);
        q19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q19.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(18, months);
            }
        });

        final FloatingActionButton q20 = (FloatingActionButton) findViewById(R.id.q20);
        q20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int months = getMonthBetween();
                q20.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                updateListsInfo(19, months);
            }
        });
    }

    //endregion

    //region Firebase

    private void addNewValueToFirebase(FirebaseDatabase database) {
        //todo check if we have already added teeth values for aby to firebase, so we can just update them
        if (teeth.getBabyId() != null) {
            Calendar today = Calendar.getInstance();
            String lastUpdateDate = FormattedDate.getFormattedDate(today);
            teeth.setDate(lastUpdateDate);
            DatabaseReference databaseReference = database.getReference().child(DatabaseNames.TEETH);
            databaseReference.push().setValue(teeth);
        } else
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
                            int number = 1;
                            for (Boolean have : teeth.getDoesHave()) {
                                if (have) { // user should not change teeth which were already added
                                    FloatingActionButton button = (FloatingActionButton) findViewById(getResources().getIdentifier("q" + number, "id",
                                            getApplication().getPackageName()));
                                    int months = teeth.getWhenHave().get(number - 1);
                                    button.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                                    button.setEnabled(false);
                                }
                                number++;
                            }
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
