package com.repina.anastasia.momandbaby.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.repina.anastasia.momandbaby.DataBase.User;
import com.repina.anastasia.momandbaby.Classes.FirebaseConnection;
import com.repina.anastasia.momandbaby.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button signUp = (Button)findViewById(R.id.btn_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo check for correctness
                String email = ((EditText)findViewById(R.id.input_email)).getText().toString();
                String password = ((EditText)findViewById(R.id.input_password)).getText().toString();
                String name = ((EditText)findViewById(R.id.input_name)).getText().toString();
                FirebaseConnection connection = new FirebaseConnection();
                FirebaseDatabase database = connection.getDatabase();
                User user = new User(email, password, name);
                //todo Firebase signUp
                //todo go to mibandactivity
            }
        });

        TextView logIn = (TextView)findViewById(R.id.link_login);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(nextActivity);
                finish();
            }
        });
    }
}
