package com.repina.anastasia.momandbaby.DataBase.Handlers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.Activity.Helper;
import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.Vaccination;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VaccinationHandler extends DatabaseHandler {

    public VaccinationHandler(Context context) {
        super(context);
    }

    @Override
    public long add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        Vaccination v = (Vaccination) ob;
        ContentValues values = formContentValues(v);
        return db.insert(TABLE_VACCINATION, null, values);
    }

    private Vaccination parseFromCursor(Cursor c) {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String date = c.getString((c.getColumnIndex(KEY_DATE)));
        String vaccinationName = c.getString((c.getColumnIndex(KEY_VACCINATION_NAME)));
        String note = c.getString((c.getColumnIndex(KEY_NOTE)));
        Date parsedDate = null;
        try {
            parsedDate = Helper.dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Vaccination(id, parsedDate, vaccinationName, note);
    }

    private ContentValues formContentValues(Vaccination o) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, o.getDate());
        values.put(KEY_VACCINATION_NAME, o.getVaccinationName());
        values.put(KEY_NOTE, o.getNote());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_VACCINATION + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> vaccinationRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_VACCINATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Vaccination record = parseFromCursor(c);
                vaccinationRecords.add(record);
            } while (c.moveToNext());
        }

        return vaccinationRecords;
    }

    @Override
    public int update(int id, Object ob) {
        Vaccination v = (Vaccination) ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(v);
        // updating row
        return db.update(TABLE_VACCINATION, values, KEY_ID + " = ?",
                new String[]{String.valueOf(v.getId())});
    }
}
