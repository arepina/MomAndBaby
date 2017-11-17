package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Classes.ConnectionDetector;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.User;
import com.repina.anastasia.momandbaby.Classes.FirebaseConnection;
import com.repina.anastasia.momandbaby.R;

public class SignupActivity extends AppCompatActivity {

    /**
     * Name of the shared preferences which holds mom's info
     */
    public static final String MOM_PREFS = "mom_spfile";

    /**
     * Key to use in {@code MOM_PREFS} to find the correct user
     */
    public static final String MOM_ID_KEY = "mom_id_spkey";

    /**
     * Key to use in {@code MOM_PREFS} to find user's name
     */
    public static final String MOM_NAME_KEY = "mom_name_spkey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button logIn = (Button) findViewById(R.id.btn_login);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionDetector.isConnected(getApplicationContext())) {//check if internet is working

                    final String email = ((EditText) findViewById(R.id.input_email)).getText().toString();
                    final String password = ((EditText) findViewById(R.id.input_password)).getText().toString();
                    if (email.length() == 0 || password.length() == 0) {//email and password should be entered
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.enter_email_and_password, Toast.LENGTH_SHORT);
                        TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                        if (textView != null) textView.setGravity(Gravity.CENTER);
                        toast.show();
                        return;
                    }

                    FirebaseConnection connection = new FirebaseConnection();
                    FirebaseDatabase database = connection.getDatabase();
                    User user = new User(email, password);
                    final DatabaseReference databaseReference = database.getReference().child(DatabaseNames.USER);
                    databaseReference.orderByChild("email")//try to find the user with the entered email
                            .equalTo(user.getEmail())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                                        User u = snapshot.getValue(User.class);
                                        if (password.equals(u.getPassword())) {//check whether the password is correct
                                            String momId = snapshot.getKey();
                                            String name = u.getName();
                                            SharedPreferences sp = getSharedPreferences(MOM_PREFS, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString(MOM_ID_KEY, momId);
                                            editor.putString(MOM_NAME_KEY, name);
                                            editor.apply();
                                            Intent nextActivity = new Intent(getApplicationContext(), TabsActivity.class);
                                            startActivity(nextActivity);
                                            finish();
                                        }
                                    } else {
                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_SHORT);
                                        TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                                        if (textView != null) textView.setGravity(Gravity.CENTER);
                                        toast.show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT);
                                    TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                                    if (textView != null) textView.setGravity(Gravity.CENTER);
                                    toast.show();
                                }
                            });
                }
            }
        });

        TextView signUp = (TextView) findViewById(R.id.link_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(nextActivity);
                finish();
            }
        });
    }
}
