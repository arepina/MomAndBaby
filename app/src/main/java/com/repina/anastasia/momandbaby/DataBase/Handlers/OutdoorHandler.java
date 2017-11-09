package com.repina.anastasia.momandbaby.DataBase.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.Outdoor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OutdoorHandler extends DatabaseHandler {

    public OutdoorHandler(Context context) {
        super(context);
    }

    @Override
    public void add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        Outdoor o = (Outdoor) ob;
        ContentValues values = formContentValues(o);
        db.insert(TABLE_OUTDOOR, null, values);
    }

    private Outdoor parseFromCursor(Cursor c) {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String date = c.getString((c.getColumnIndex(KEY_DATE)));
        double outdoorLength = c.getDouble((c.getColumnIndex(KEY_OUTDOOR_LENGTH)));
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Outdoor(id, parsedDate, outdoorLength);
    }

    private ContentValues formContentValues(Outdoor o) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, o.getDate());
        values.put(KEY_OUTDOOR_LENGTH, o.getOutdoorLength());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_OUTDOOR + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> outdoorRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_OUTDOOR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Outdoor record = parseFromCursor(c);
                outdoorRecords.add(record);
            } while (c.moveToNext());
        }

        return outdoorRecords;
    }

    @Override
    public int update(int id, Object ob) {
        Outdoor o = (Outdoor) ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(o);
        // updating row
        return db.update(TABLE_OUTDOOR, values, KEY_ID + " = ?",
                new String[]{String.valueOf(o.getId())});
    }
}
