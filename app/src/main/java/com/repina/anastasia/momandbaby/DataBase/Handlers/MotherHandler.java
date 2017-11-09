package com.repina.anastasia.momandbaby.DataBase.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.Activity.Helper;
import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.Mother;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MotherHandler extends DatabaseHandler {

    public MotherHandler(Context context) {
        super(context);
    }

    @Override
    public long add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        Mother m = (Mother) ob;
        ContentValues values = formContentValues(m);
        return db.insert(TABLE_MOTHER, null, values);
    }

    private Mother parseFromCursor(Cursor c) {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String date = c.getString((c.getColumnIndex(KEY_DATE)));
        double sleepLength = c.getDouble((c.getColumnIndex(KEY_SLEEP_DURATION)));
        int steps = c.getInt((c.getColumnIndex(KEY_STEPS)));
        int calories = c.getInt((c.getColumnIndex(KEY_CALORIES)));
        double weight = c.getDouble(((c.getColumnIndex(KEY_WEIGHT))));
        Date parsedDate = null;
        try {
            parsedDate = Helper.dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Mother(id, parsedDate, sleepLength, steps, calories, weight);
    }

    private ContentValues formContentValues(Mother m) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, m.getDate());
        values.put(KEY_SLEEP_DURATION, m.getSleepLength());
        values.put(KEY_STEPS, m.getSteps());
        values.put(KEY_CALORIES, m.getCalories());
        values.put(KEY_WEIGHT, m.getWeight());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_MOTHER + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> motherRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_MOTHER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Mother record = parseFromCursor(c);
                motherRecords.add(record);
            } while (c.moveToNext());
        }

        return motherRecords;
    }

    @Override
    public int update(int id, Object ob) {
        Mother m = (Mother) ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(m);
        // updating row
        return db.update(TABLE_MOTHER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(m.getId())});
    }
}
