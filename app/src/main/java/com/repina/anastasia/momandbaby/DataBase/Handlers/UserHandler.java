package com.repina.anastasia.momandbaby.DataBase.Handlers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.User;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserHandler extends DatabaseHandler {

    public UserHandler(Context context) {
        super(context);
    }

    @Override
    public void add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        User u = (User) ob;
        ContentValues values = formContentValues(u);
        db.insert(TABLE_USERS, null, values);
    }

    private User parseFromCursor(Cursor c) {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String name = c.getString((c.getColumnIndex(KEY_NAME)));
        String dateOfBirth = c.getString((c.getColumnIndex(KEY_DATE)));
        String gender = c.getString((c.getColumnIndex(KEY_GENDER)));
        String bandCode = c.getString((c.getColumnIndex(KEY_BAND_CODE)));
        String bandStatus = c.getString((c.getColumnIndex(KEY_BAND_STATUS)));
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateOfBirth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new User(id, name, parsedDate, gender, bandCode, bandStatus);
    }

    private ContentValues formContentValues(User u) {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, u.getDate());
        values.put(KEY_NAME, u.getName());
        values.put(KEY_GENDER, u.getGender());
        values.put(KEY_BAND_CODE, u.getBandCode());
        values.put(KEY_BAND_STATUS, u.getBandStatus());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> userRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User record = parseFromCursor(c);
                userRecords.add(record);
            } while (c.moveToNext());
        }

        return userRecords;
    }

    @Override
    public int update(int id, Object ob) {
        User u = (User) ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(u);
        // updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(u.getId())});
    }
}
