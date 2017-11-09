package com.repina.anastasia.momandbaby.DataBase.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.Activity.Helper;
import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.Baby;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BabyHandler extends DatabaseHandler{
    public BabyHandler(Context context) {
        super(context);
    }

    @Override
    public long add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        Baby b = (Baby)ob;
        ContentValues values = formContentValues(b);
        return db.insert(TABLE_BABY, null, values);
    }

    private Baby parseFromCursor(Cursor c)
    {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String date = c.getString((c.getColumnIndex(KEY_DATE)));
        double weight = c.getDouble(((c.getColumnIndex(KEY_WEIGHT))));
        double height = c.getDouble(((c.getColumnIndex(KEY_HEIGHT))));
        String note = c.getString(((c.getColumnIndex(KEY_NOTE))));
        Date parsedDate = null;
        try {
            parsedDate = Helper.dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Baby(id, parsedDate, weight, height, note);
    }

    private ContentValues formContentValues(Baby b)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, b.getDate());
        values.put(KEY_WEIGHT, b.getWeight());
        values.put(KEY_HEIGHT, b.getHeight());
        values.put(KEY_NOTE, b.getNote());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_BABY + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> babyRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_BABY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Baby record = parseFromCursor(c);
                babyRecords.add(record);
            } while (c.moveToNext());
        }

        return babyRecords;
    }

    @Override
    public int update(int id, Object ob) {
        Baby b = (Baby)ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(b);
        // updating row
        return db.update(TABLE_BABY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(b.getId()) });
    }
}
