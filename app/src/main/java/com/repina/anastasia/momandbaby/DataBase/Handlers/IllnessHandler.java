package com.repina.anastasia.momandbaby.DataBase.Handlers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.Activity.Helper;
import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.Illness;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IllnessHandler  extends DatabaseHandler{

    public IllnessHandler(Context context) {
        super(context);
    }

    @Override
    public long add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        Illness il = (Illness)ob;
        ContentValues values = formContentValues(il);
        return db.insert(TABLE_ILLNESS, null, values);
    }

    private Illness parseFromCursor(Cursor c)
    {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String date = c.getString((c.getColumnIndex(KEY_DATE)));
        String symptomes = c.getString((c.getColumnIndex(KEY_SYMPTOMES)));
        String pills = c.getString((c.getColumnIndex(KEY_PILLS)));
        double temperature = c.getDouble((c.getColumnIndex(KEY_TEMPERATURE)));
        Date parsedDate = null;
        try {
            parsedDate = Helper.dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Illness(id, parsedDate, symptomes, pills, temperature);
    }

    private ContentValues formContentValues(Illness il)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, il.getDate());
        values.put(KEY_SYMPTOMES, il.getSymptomes());
        values.put(KEY_PILLS, il.getPills());
        values.put(KEY_TEMPERATURE, il.getTemperature());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ILLNESS + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> illnessRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_ILLNESS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Illness record = parseFromCursor(c);
                illnessRecords.add(record);
            } while (c.moveToNext());
        }

        return illnessRecords;
    }

    @Override
    public int update(int id, Object ob) {
        Illness il = (Illness)ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(il);
        // updating row
        return db.update(TABLE_ILLNESS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(il.getId()) });
    }

}
