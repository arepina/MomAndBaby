package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.DataBase.User;
import com.repina.anastasia.momandbaby.Connectors.FirebaseConnection;
import com.repina.anastasia.momandbaby.R;
import com.repina.anastasia.momandbaby.Helpers.SharedConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Register
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button signUp = (Button) findViewById(R.id.btn_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionDetector.isConnected(getApplicationContext())) {//check if internet is working

                    String email = ((EditText) findViewById(R.id.input_email)).getText().toString();
                    String password = ((EditText) findViewById(R.id.input_password)).getText().toString();
                    String passwordAgain = ((EditText) findViewById(R.id.input_password_again)).getText().toString();
                    String name = ((EditText) findViewById(R.id.input_name)).getText().toString();
                    if (isValidEmailAddress(email)) {
                        if (password.length() >= 8) {
                            if (name.length() > 0)
                                if (password.equals(passwordAgain))
                                    isEmailAlreadyInUse(email, password, name);
                                else
                                    NotificationsShow.showToast(getApplicationContext(), R.string.different_passwords);
                            else
                                NotificationsShow.showToast(getApplicationContext(), R.string.invalid_name);
                        } else
                            NotificationsShow.showToast(getApplicationContext(), R.string.invalid_password);
                    } else
                        NotificationsShow.showToast(getApplicationContext(), R.string.invalid_email);
                }
            }
        });

        TextView logIn = (TextView) findViewById(R.id.link_login);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(nextActivity);
                finish();
            }
        });
    }

    /**
     * Validate email
     *
     * @param email email
     * @return if email is valid
     */
    public static boolean isValidEmailAddress(String email) {
        if (email.length() < 8)
            return false;
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Check email for being in use
     *
     * @param email    email
     * @param password password
     * @param name     user name
     */
    private void isEmailAlreadyInUse(final String email, final String password, final String name) {
        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();

        final DatabaseReference databaseReference = database.getReference().child(DatabaseNames.USER);

        databaseReference.orderByChild("email")//try to find the baby with the entered momId
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                            createNewAccount(email, password, name);
                        else
                            NotificationsShow.showToast(getApplicationContext(), R.string.email_already_used);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        NotificationsShow.showToast(getApplicationContext(), R.string.unpredicted_error);
                    }
                });
    }

    /**
     * Create new account
     *
     * @param email    email
     * @param password password
     * @param name     user name
     */
    private void createNewAccount(String email, String password, String name) {
        FirebaseConnection connection = new FirebaseConnection();
        FirebaseDatabase database = connection.getDatabase();

        User user = new User(email, password, name);

        DatabaseReference databaseReference = database.getReference().child(DatabaseNames.USER);

        String momId = databaseReference.push().getKey();
        databaseReference.child(momId).setValue(user);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedConstants.MOM_ID_KEY, momId);
        editor.putString(SharedConstants.MOM_NAME_KEY, name);
        editor.putString(SharedConstants.MOM_EMAIL, email);
        editor.apply();

        Intent nextActivity = new Intent(getApplicationContext(), BabyInfoActivity.class);
        startActivity(nextActivity);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
