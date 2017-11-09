package com.repina.anastasia.momandbaby.DataBase.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.Sleep;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SleepHandler extends DatabaseHandler {

    public SleepHandler(Context context) {
        super(context);
    }

    @Override
    public void add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        Sleep s = (Sleep) ob;
        ContentValues values = formContentValues(s);
        db.insert(TABLE_SLEEP, null, values);
    }

    private Sleep parseFromCursor(Cursor c) {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String date = c.getString((c.getColumnIndex(KEY_DATE)));
        double sleepDuration = c.getDouble((c.getColumnIndex(KEY_SLEEP_DURATION)));
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Sleep(id, parsedDate, sleepDuration);
    }

    private ContentValues formContentValues(Sleep s) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, s.getDate());
        values.put(KEY_SLEEP_DURATION, s.getSleepDuration());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_SLEEP + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> sleepRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SLEEP;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Sleep record = parseFromCursor(c);
                sleepRecords.add(record);
            } while (c.moveToNext());
        }

        return sleepRecords;
    }

    @Override
    public int update(int id, Object ob) {
        Sleep s = (Sleep) ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(s);
        // updating row
        return db.update(TABLE_SLEEP, values, KEY_ID + " = ?",
                new String[]{String.valueOf(s.getId())});
    }
}
