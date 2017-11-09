package com.repina.anastasia.momandbaby.DataBase.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.Activity.Helper;
import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.Stool;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StoolHandler  extends DatabaseHandler {

    public StoolHandler(Context context) {
        super(context);
    }

    @Override
    public long add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        Stool s = (Stool) ob;
        ContentValues values = formContentValues(s);
        return db.insert(TABLE_STOOL, null, values);
    }

    private Stool parseFromCursor(Cursor c) {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String date = c.getString((c.getColumnIndex(KEY_DATE)));
        int abundance = c.getInt((c.getColumnIndex(KEY_ABUNDANCE)));
        Date parsedDate = null;
        try {
            parsedDate = Helper.dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Stool(id, parsedDate, abundance);
    }

    private ContentValues formContentValues(Stool s) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, s.getDate());
        values.put(KEY_SLEEP_DURATION, s.getAbundance());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_STOOL + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> sleepRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_STOOL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Stool record = parseFromCursor(c);
                sleepRecords.add(record);
            } while (c.moveToNext());
        }

        return sleepRecords;
    }

    @Override
    public int update(int id, Object ob) {
        Stool s = (Stool) ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(s);
        // updating row
        return db.update(TABLE_STOOL, values, KEY_ID + " = ?",
                new String[]{String.valueOf(s.getId())});
    }
}
