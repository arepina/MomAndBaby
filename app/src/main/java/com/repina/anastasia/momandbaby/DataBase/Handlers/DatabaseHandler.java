package com.repina.anastasia.momandbaby.DataBase.Handlers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;


public abstract class DatabaseHandler extends SQLiteOpenHelper implements IDatabaseHandler {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MAIN_DATABASE";

    // Table Names
    static final String TABLE_USERS = "USERS";
    static final String TABLE_MOTHER = "MOTHER";
    static final String TABLE_BABY = "BABY";
    static final String TABLE_FOOD = "FOOD";
    static final String TABLE_OUTDOOR = "OUTDOOR";
    static final String TABLE_ILLNESS = "ILLNESS";
    static final String TABLE_SLEEP = "SLEEP";
    static final String TABLE_VACCINATION = "VACCINATION";
    static final String TABLE_TEETH= "TEETH";
    static final String TABLE_STOOL = "STOOL";

    // Common column names
    static final String KEY_ID = "id";
    static final String KEY_DATE = "date";
    static final String KEY_SLEEP_DURATION = "sleep_duration";
    static final String KEY_WEIGHT = "weight";
    static final String KEY_NOTE = "note";

    // USERS Table - column names
    static final String KEY_NAME = "name";
    static final String KEY_GENDER = "gender";
    static final String KEY_BAND_CODE = "band_code";
    static final String KEY_BAND_STATUS = "band_status";

    // MOTHER Table - column names
    static final String KEY_STEPS = "steps";
    static final String KEY_CALORIES = "calories";

    // BABY Table - column names
    static final String KEY_HEIGHT= "height";

    // OUTDOOR Table - column names
    static final String KEY_OUTDOOR_LENGTH = "outdoor_length";

    // ILLNESS Table - column names
    static final String KEY_SYMPTOMES = "symptomes";
    static final String KEY_PILLS = "pills";
    static final String KEY_TEMPERATURE = "temperature";

    // FOOD Table - column names - all fields are in common
    // SLEEP Table - column names - all fields are in common

    // VACCINATION Table - column names
    static final String KEY_VACCINATION_NAME = "vaccination_name";

    // TEETH Table - column names
    static final String KEY_TEETH_NUM = "teeth_num";

    // STOOL Table - column names
    static final String KEY_ABUNDANCE = "abundance";

    // Table Create Statements
    // TABLE_USERS create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_DATE + " DATETIME," + KEY_GENDER
            + " TEXT," + KEY_BAND_CODE + " TEXT," + KEY_BAND_STATUS + " TEXT" + ")";

    // TABLE_MOTHER create statement
    private static final String CREATE_TABLE_MOTHER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_MOTHER + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_SLEEP_DURATION + " DOUBLE," + KEY_STEPS
            + " INTEGER," + KEY_CALORIES + " INTEGER," + KEY_WEIGHT + " DOUBLE" + ")";

    // TABLE_BABY create statement
    private static final String CREATE_TABLE_BABY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_BABY + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_WEIGHT + " DOUBLE," +  KEY_HEIGHT + " DOUBLE," + KEY_NOTE + " TEXT" +")";

    // TABLE_OUTDOOR create statement
    private static final String CREATE_TABLE_OUTDOOR = "CREATE TABLE IF NOT EXISTS "
            + TABLE_OUTDOOR + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_OUTDOOR_LENGTH + " DOUBLE" + ")";

    // TABLE_ILLNESS create statement
    private static final String CREATE_TABLE_ILLNESS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ILLNESS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_SYMPTOMES + " TEXT," + KEY_PILLS + " TEXT," + KEY_TEMPERATURE
            + " DOUBLE" + ")";

    // TABLE_FOOD create statement
    private static final String CREATE_TABLE_FOOD = "CREATE TABLE IF NOT EXISTS "
            + TABLE_FOOD + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_NOTE + " TEXT" + ")";

    // TABLE_SLEEP create statement
    private static final String CREATE_TABLE_SLEEP = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SLEEP + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_SLEEP_DURATION + " DOUBLE" + ")";

    // TABLE_VACCINATION create statement
    private static final String CREATE_TABLE_VACCINATION = "CREATE TABLE IF NOT EXISTS "
            + TABLE_VACCINATION + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_VACCINATION_NAME + " TEXT," +  KEY_NOTE + " TEXT" + ")";

    // TABLE_TEETH create statement
    private static final String CREATE_TABLE_TEETH = "CREATE TABLE IF NOT EXISTS "
            + TABLE_TEETH + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE
            + " DATETIME," + KEY_TEETH_NUM + " INTEGER," +  KEY_NOTE + " TEXT" + ")";

    // TABLE_STOOL create statement
    private static final String CREATE_TABLE_STOOL = "CREATE TABLE IF NOT EXISTS "
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
    public int getSize(String table_name) {
        String countQuery = "SELECT  * FROM " + table_name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    @Override
    public void deleteAll(String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_name, null, null);
        db.close();
    }

    @Override
    public void delete(String table_name, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_name, KEY_ID + " = ?", new String[] { String.valueOf(id) });
    }
}
