package com.repina.anastasia.momandbaby.Classes;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConnection {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    private FirebaseConnection() {}

    public static FirebaseDatabase getDatabase() {
        return database;
    }
}
