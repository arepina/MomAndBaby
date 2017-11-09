package com.repina.anastasia.momandbaby.DataBase.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.Teeth;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TeethHandler extends DatabaseHandler {

    public TeethHandler(Context context) {
        super(context);
    }

    @Override
    public void add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        Teeth t = (Teeth) ob;
        ContentValues values = formContentValues(t);
        db.insert(TABLE_TEETH, null, values);
    }

    private Teeth parseFromCursor(Cursor c) {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String date = c.getString((c.getColumnIndex(KEY_DATE)));
        int teethNum = c.getInt((c.getColumnIndex(KEY_TEETH_NUM)));
        String note = c.getString((c.getColumnIndex(KEY_NOTE)));
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Teeth(id, parsedDate, teethNum, note);
    }

    private ContentValues formContentValues(Teeth t) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, t.getDate());
        values.put(KEY_TEETH_NUM, t.getTeethNum());
        values.put(KEY_NOTE, t.getNote());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TEETH + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> teethRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TEETH;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Teeth record = parseFromCursor(c);
                teethRecords.add(record);
            } while (c.moveToNext());
        }

        return teethRecords;
    }

    @Override
    public int update(int id, Object ob) {
        Teeth t = (Teeth) ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(t);
        // updating row
        return db.update(TABLE_TEETH, values, KEY_ID + " = ?",
                new String[]{String.valueOf(t.getId())});
    }
}
