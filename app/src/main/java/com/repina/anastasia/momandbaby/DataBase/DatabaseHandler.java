package com.repina.anastasia.momandbaby.DataBase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseHandler extends SQLiteOpenHelper implements IDatabaseHandler {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MAIN_DATABASE";

    // Table Names
    private static final String TABLE_USERS = "USERS";
    private static final String TABLE_MOTHER = "MOTHER";
    private static final String TABLE_BABY = "BABY";
    private static final String TABLE_FOOD = "FOOD";
    private static final String TABLE_OUTDOOR = "OUTDOOR";
    private static final String TABLE_ILLNESS = "ILLNESS";
    private static final String TABLE_SLEEP = "SLEEP";
    private static final String TABLE_VACCINATION = "VACCINATION";
    private static final String TABLE_TEETH= "TEETH";
    private static final String TABLE_STOOL = "STOOL";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_SLEEP_DURATION = "sleep_duration";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_NOTE = "note";

    // USERS Table - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_BAND_CODE = "band_code";
    private static final String KEY_BAND_STATUS = "band_status";

    // MOTHER Table - column names
    private static final String KEY_STEPS = "steps";
    private static final String KEY_CALORIES = "calories";

    // BABY Table - column names
    private static final String KEY_HEIGHT= "height";

    // OUTDOOR Table - column names
    private static final String KEY_OUTDOOR_LENGTH = "outdoor_length";

    // ILLNESS Table - column names
    private static final String KEY_SYMPTOMES = "symptomes";
    private static final String KEY_PILLS = "pills";
    private static final String KEY_TEMPERATURE = "temperature";

    // FOOD Table - column names - all fields are in common
    // SLEEP Table - column names - all fields are in common

    // VACCINATION Table - column names
    private static final String KEY_VACCINATION_NAME = "vaccination_name";

    // TEETH Table - column names
    private static final String KEY_TEETH_NUM = "teeth_num";

    // STOOL Table - column names
    private static final String KEY_ABUNDANCE = "abundance";

    // Table Create Statements
    // TABLE_USERS create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_DATE_OF_BIRTH + " DATETIME," + KEY_GENDER
            + " TEXT," + KEY_BAND_CODE + " TEXT," + KEY_BAND_STATUS + " TEXT" + ")";

    // TABLE_MOTHER create statement
    private static final String CREATE_TABLE_MOTHER = "CREATE TABLE "
            + TABLE_MOTHER + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_SLEEP_DURATION + " DOUBLE," + KEY_STEPS
            + " INTEGER," + KEY_CALORIES + " INTEGER," + KEY_WEIGHT + " DOUBLE" + ")";

    // TABLE_BABY create statement
    private static final String CREATE_TABLE_BABY = "CREATE TABLE "
            + TABLE_BABY + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_WEIGHT + " DOUBLE," +  KEY_HEIGHT + " DOUBLE," + KEY_NOTE + " TEXT" +")";

    // TABLE_OUTDOOR create statement
    private static final String CREATE_TABLE_OUTDOOR = "CREATE TABLE "
            + TABLE_OUTDOOR + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_OUTDOOR_LENGTH + " DOUBLE" + ")";

    // TABLE_ILLNESS create statement
    private static final String CREATE_TABLE_ILLNESS = "CREATE TABLE "
            + TABLE_ILLNESS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_SYMPTOMES + " TEXT," + KEY_PILLS + " TEXT," + KEY_TEMPERATURE
            + " TEXT" + ")";

    // TABLE_FOOD create statement
    private static final String CREATE_TABLE_FOOD = "CREATE TABLE "
            + TABLE_FOOD + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_NOTE + " TEXT" + ")";

    // TABLE_SLEEP create statement
    private static final String CREATE_TABLE_SLEEP = "CREATE TABLE "
            + TABLE_SLEEP + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_SLEEP_DURATION + " DOUBLE" + ")";

    // TABLE_VACCINATION create statement
    private static final String CREATE_TABLE_VACCINATION = "CREATE TABLE "
            + TABLE_VACCINATION + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_VACCINATION_NAME + " TEXT," +  KEY_NOTE + " TEXT" + ")";

    // TABLE_TEETH create statement
    private static final String CREATE_TABLE_TEETH = "CREATE TABLE "
            + TABLE_TEETH + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_TEETH_NUM + " INTEGER," +  KEY_NOTE + " TEXT" + ")";

    // TABLE_STOOL create statement
    private static final String CREATE_TABLE_STOOL = "CREATE TABLE "
            + TABLE_STOOL + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_ABUNDANCE + " INTEGER" + ")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_BABY);
        db.execSQL(CREATE_TABLE_FOOD);
        db.execSQL(CREATE_TABLE_ILLNESS);
        db.execSQL(CREATE_TABLE_MOTHER);
        db.execSQL(CREATE_TABLE_OUTDOOR);
        db.execSQL(CREATE_TABLE_SLEEP);
        db.execSQL(CREATE_TABLE_STOOL);
        db.execSQL(CREATE_TABLE_TEETH);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_VACCINATION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BABY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ILLNESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOTHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTDOOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLEEP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOOL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEETH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VACCINATION);
        // create new tables
        onCreate(db);
    }

    @Override
    public void add(String table_name, Object ob) {
        //todo
    }

    @Override
    public Object getObject(String table_name, int id) {
        //todo
       /* SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table_name, new String[] { KEY_ID,
                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        Object contact = new Object(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));

        return contact;*/
       return null;
    }

    @Override
    public int getSize(String table_name) {
        String countQuery = "SELECT  * FROM " + table_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    @Override
    public int update(String table_name, int id, String key, String new_value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key, new_value);
        return db.update(table_name, values, KEY_ID + " = ?", new String[] { String.valueOf(id) });
    }

    @Override
    public void deleteAll(String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_name, null, null);
        db.close();
    }
}
