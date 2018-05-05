package com.repina.anastasia.momandbaby.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

/**
 * Teeth
 */
public class TeethActivity extends AppCompatActivity {

    private Teeth teeth, teethOld;
    private String babyId;
    private String firebaseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teeth);

        String calling = getIntent().getStringExtra(CALLING);

        FirebaseConnection connection = new FirebaseConnection();
        final FirebaseDatabase database = connection.getDatabase();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        babyId = sp.getString(SharedConstants.BABY_ID_KEY, "");

        Button addData = findViewById(R.id.addData);
        addData.setVisibility(View.VISIBLE);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {
                    boolean anyChanges = false;
                    ArrayList<Boolean> doesHaveOld = teethOld.getDoesHave();
                    ArrayList<Boolean> doesHave = teeth.getDoesHave();
                    for(int i = 0; i < doesHave.size(); i++)
                    {
                        if(doesHaveOld == null || !doesHave.get(i).equals(doesHaveOld.get(i))) {
                            anyChanges = true;
                            break;
                        }
                    }
                    if (anyChanges) {
                        addNewValueToFirebase(database);
                        finish();//back to choosing
                    } else
                        NotificationsShow.showToast(getApplicationContext(), R.string.add_any_data);
                }
            }
        });

        initFABs();

        if (calling.equals(StatsActivity.class.toString())) // the user just watch the teeth
        {
            TextView textView = findViewById(R.id.task);
            textView.setText(getString(R.string.teeth_map));
            makeEnabledFalse();
            addData.setVisibility(View.GONE);
        }

        getTeethFromFirebase(database, babyId);
    }

    // region Data update

    /**
     * Get months number between two dates
     *
     * @return months number
     */
    public static int getMonthBetween(Context context) {
        Calendar today = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String birthday = sp.getString(SharedConstants.BABY_BIRTHDAY, "");
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

    /**
     * Create text bitmap for FAB
     *
     * @param text      text
     * @param textSize  text size
     * @param textColor text color
     * @return bitmap
     */
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

    /**
     * Initialise teeth lists
     */
    private void initLists() {
        ArrayList<Boolean> doesHave = teeth.getDoesHave();
        ArrayList<Integer> whenHave = teeth.getWhenHave();
        if (doesHave == null && whenHave == null) // we have not added the teeth data before
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

    /**
     * Update teeth lists info
     *
     * @param i     tooth number
     * @param value month when tooth was born
     */
    private void updateListsInfo(int i, int value) {
        initLists();
        ArrayList<Boolean> doesHave = teeth.getDoesHave();
        ArrayList<Integer> whenHave = teeth.getWhenHave();
        whenHave.set(i, value);
        if(value == -1) {
            doesHave.set(i, false);
        }else
            doesHave.set(i, true);
    }

    //endregion

    //region FABs

    /**
     * Initialise FABs
     */
    private void initFABs() {
        initRed();
        initYellow();
        initGreen();
        initBlue();
        initPurple();
    }

    /**
     * Make FABs unable to click
     */
    private void makeEnabledFalse() {
        for (int i = 1; i <= 20; i++) {
            FloatingActionButton button = findViewById(getResources().getIdentifier("q" + i, "id",
                    this.getPackageName()));
            button.setEnabled(false);
        }
    }

    /**
     * Initialise red teeth
     */
    private void initRed() {
        final FloatingActionButton q1 = findViewById(R.id.q1);
        q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q1.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q1.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(0, months);
                }else{
                    q1.setImageDrawable(null);
                    updateListsInfo(0, -1);
                }
            }
        });

        final FloatingActionButton q2 = findViewById(R.id.q2);
        q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q2.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q2.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(1, months);
                }else{
                    q2.setImageDrawable(null);
                    updateListsInfo(1, -1);
                }
            }
        });

        final FloatingActionButton q11 = findViewById(R.id.q11);
        q11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q11.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q11.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(10, months);
                }else{
                    q11.setImageDrawable(null);
                    updateListsInfo(10, -1);
                }
            }
        });

        final FloatingActionButton q12 = findViewById(R.id.q12);
        q12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q12.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q12.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(11, months);
                }else{
                    q12.setImageDrawable(null);
                    updateListsInfo(11, -1);
                }
            }
        });
    }

    /**
     * Initialise yellow teeth
     */
    private void initYellow() {
        final FloatingActionButton q3 = findViewById(R.id.q3);
        q3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q3.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q3.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(2, months);
                }else{
                    q3.setImageDrawable(null);
                    updateListsInfo(2, -1);
                }
            }
        });

        final FloatingActionButton q4 = findViewById(R.id.q4);
        q4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q4.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q4.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(3, months);
                }else{
                    q4.setImageDrawable(null);
                    updateListsInfo(3, -1);
                }
            }
        });

        final FloatingActionButton q13 = findViewById(R.id.q13);
        q13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q13.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q13.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(12, months);
                }else{
                    q13.setImageDrawable(null);
                    updateListsInfo(12, -1);
                }
            }
        });

        final FloatingActionButton q14 = findViewById(R.id.q14);
        q14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q14.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q14.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(13, months);
                }else{
                    q14.setImageDrawable(null);
                    updateListsInfo(13, -1);
                }
            }
        });
    }

    /**
     * Initialise green teeth
     */
    private void initGreen() {
        final FloatingActionButton q5 = findViewById(R.id.q5);
        q5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q5.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q5.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(4, months);
                }else{
                    q5.setImageDrawable(null);
                    updateListsInfo(4, -1);
                }
            }
        });

        final FloatingActionButton q6 = findViewById(R.id.q6);
        q6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q6.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q6.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(5, months);
                }else{
                    q6.setImageDrawable(null);
                    updateListsInfo(5, -1);
                }
            }
        });

        final FloatingActionButton q15 = findViewById(R.id.q15);
        q15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q15.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q15.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(14, months);
                }else{
                    q15.setImageDrawable(null);
                    updateListsInfo(14, -1);
                }
            }
        });

        final FloatingActionButton q16 = findViewById(R.id.q16);
        q16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q16.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q16.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(15, months);
                }else{
                    q16.setImageDrawable(null);
                    updateListsInfo(15, -1);
                }
            }
        });
    }

    /**
     * Initialise blue teeth
     */
    private void initBlue() {
        final FloatingActionButton q7 = findViewById(R.id.q7);
        q7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q7.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q7.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(6, months);
                }else{
                    q7.setImageDrawable(null);
                    updateListsInfo(6, -1);
                }
            }
        });

        final FloatingActionButton q8 = findViewById(R.id.q8);
        q8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q8.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q8.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(7, months);
                }else{
                    q8.setImageDrawable(null);
                    updateListsInfo(7, -1);
                }
            }
        });

        final FloatingActionButton q17 = findViewById(R.id.q17);
        q17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q17.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q17.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(16, months);
                }else{
                    q17.setImageDrawable(null);
                    updateListsInfo(16, -1);
                }
            }
        });

        final FloatingActionButton q18 = findViewById(R.id.q18);
        q18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q18.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q18.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(17, months);
                }else{
                    q18.setImageDrawable(null);
                    updateListsInfo(17, -1);
                }
            }
        });
    }

    /**
     * Initialise purple teeth
     */
    private void initPurple() {
        final FloatingActionButton q9 = findViewById(R.id.q9);
        q9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q9.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q9.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(8, months);
                }else{
                    q9.setImageDrawable(null);
                    updateListsInfo(8, -1);
                }
            }
        });

        final FloatingActionButton q10 = findViewById(R.id.q10);
        q10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q10.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q10.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(9, months);
                }else{
                    q10.setImageDrawable(null);
                    updateListsInfo(9, -1);
                }
            }
        });

        final FloatingActionButton q19 = findViewById(R.id.q19);
        q19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q19.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q19.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(18, months);
                }else{
                    q19.setImageDrawable(null);
                    updateListsInfo(18, -1);
                }
            }
        });

        final FloatingActionButton q20 = findViewById(R.id.q20);
        q20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q20.getDrawable() == null) {
                    int months = getMonthBetween(getApplicationContext());
                    q20.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                    updateListsInfo(19, months);
                }else{
                    q20.setImageDrawable(null);
                    updateListsInfo(19, -1);
                }
            }
        });
    }

    //endregion

    //region Firebase

    /**
     * Add new value to Firebase
     */
    private void addNewValueToFirebase(FirebaseDatabase database) {
        if (teeth.getBabyId() != null) {
            Calendar today = Calendar.getInstance();
            String lastUpdateDate = FormattedDate.getFormattedDate(today);
            teeth.setDate(lastUpdateDate);
            if (firebaseKey.length() != 0) // we have data for that babyId, so just update it
                FirebaseDatabase.getInstance().getReference().child(DatabaseNames.TEETH).child(firebaseKey).setValue(teeth);
            else {
                DatabaseReference databaseReference = database.getReference().child(DatabaseNames.TEETH);
                databaseReference.push().setValue(teeth);
            }
        } else
            NotificationsShow.showToast(getApplicationContext(), getString(R.string.add_any_data));
    }

    /**
     * Get teeth data from Firebase
     *
     * @param database DB
     * @param babyId   baby ID
     */
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
                            teethOld = snapshot.getValue(Teeth.class);
                            firebaseKey = snapshot.getKey();
                            int number = 1;
                            for (Boolean have : teeth.getDoesHave()) {
                                if (have) { // user should not change teeth which were already added
                                    FloatingActionButton button = findViewById(getResources().getIdentifier("q" + number, "id",
                                            getApplication().getPackageName()));
                                    int months = teeth.getWhenHave().get(number - 1);
                                    button.setImageBitmap(textAsBitmap(String.valueOf(months) + "M", 40, Color.WHITE));
                                    button.setEnabled(false);
                                }
                                number++;
                            }
                        } else {
                            teeth = new Teeth();
                            teethOld = new Teeth();
                            firebaseKey = "";
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        NotificationsShow.showToast(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }

    //endregion
}
