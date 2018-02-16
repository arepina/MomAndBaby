package com.repina.anastasia.momandbaby.Connectors;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Firebase connection
 */
public class FirebaseConnection {

    private FirebaseDatabase database;

    public FirebaseConnection() {
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Get DB
     *
     * @return Firebase DB
     */
    public FirebaseDatabase getDatabase() {
        return database;
    }
}
