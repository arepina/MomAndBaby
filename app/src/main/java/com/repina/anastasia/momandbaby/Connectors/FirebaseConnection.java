package com.repina.anastasia.momandbaby.Connectors;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConnection {

    private FirebaseDatabase database;

    public FirebaseConnection()
    {
        database = FirebaseDatabase.getInstance();
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }
}
