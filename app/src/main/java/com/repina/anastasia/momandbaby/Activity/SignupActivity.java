package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.repina.anastasia.momandbaby.Connectors.ConnectionDetector;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.DataBase.Baby;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.User;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.R;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;

/**
 * Sign up
 */
public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button logIn = (Button) findViewById(R.id.btn_login);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {//check if internet is working

                    final String email = ((EditText) findViewById(R.id.input_email)).getText().toString();
                    final String password = ((EditText) findViewById(R.id.input_password)).getText().toString();
                    if (email.length() == 0 || password.length() == 0) {//email and password should be entered
                        NotificationsShow.showToast(getApplicationContext(), R.string.enter_email_and_password);
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
                                        if (password.length() > 0 && password.equals(u.getPassword())) {//check whether the password is correct
                                            String momId = snapshot.getKey();
                                            String name = u.getName();
                                            SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString(SharedConstants.MOM_ID_KEY, momId);
                                            editor.putString(SharedConstants.MOM_NAME_KEY, name);
                                            editor.putString(SharedConstants.MOM_EMAIL, email);
                                            editor.apply();

                                            checkBabyIdInFirebase(momId);//try to find the baby if DB if we have an already existing account
                                        } else
                                            NotificationsShow.showToast(getApplicationContext(), R.string.wrong_password_or_login);
                                    } else
                                        NotificationsShow.showToast(getApplicationContext(), R.string.wrong_password_or_login);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    NotificationsShow.showToast(getApplicationContext(), R.string.unpredicted_error);
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

    /**
     * Get baby id in Firebase via mom id
     *
     * @param momId mom id
     */
    private void checkBabyIdInFirebase(String momId) {
        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();

        final DatabaseReference databaseReference = database.getReference().child(DatabaseNames.BABY);

        databaseReference.orderByChild("momId")//try to find the baby with the entered momId
                .equalTo(momId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                            SharedPreferences sp = getSharedPreferences(SharedConstants.APP_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(SharedConstants.BABY_ID_KEY, snapshot.getKey());
                            Baby b = snapshot.getValue(Baby.class);
                            editor.putString(SharedConstants.BABY_BIRTHDAY, b.getBirthDay());
                            editor.putString(SharedConstants.BABY_GENDER_KEY, b.getGender());
                            editor.putString(SharedConstants.BABY_NAME_KEY, b.getName());
                            editor.apply();
                        }

                        Intent nextActivity = new Intent(getApplicationContext(), TabsActivity.class);
                        startActivity(nextActivity);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        NotificationsShow.showToast(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }
}
