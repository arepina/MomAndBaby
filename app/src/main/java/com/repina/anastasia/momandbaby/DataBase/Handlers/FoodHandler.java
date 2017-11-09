package com.repina.anastasia.momandbaby.DataBase.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.repina.anastasia.momandbaby.Activity.Helper;
import com.repina.anastasia.momandbaby.DataBase.DataBaseClasses.Food;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FoodHandler extends DatabaseHandler{

    public FoodHandler(Context context) {
        super(context);
    }

    @Override
    public long add(Object ob) {
        SQLiteDatabase db = this.getWritableDatabase();
        Food f = (Food)ob;
        ContentValues values = formContentValues(f);
        return db.insert(TABLE_FOOD, null, values);
    }

    private Food parseFromCursor(Cursor c)
    {
        int id = c.getInt((c.getColumnIndex(KEY_ID)));
        String date = c.getString((c.getColumnIndex(KEY_DATE)));
        String note = c.getString(((c.getColumnIndex(KEY_NOTE))));
        Date parsedDate = null;
        try {
            parsedDate = Helper.dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Food(id, parsedDate, note);
    }

    private ContentValues formContentValues(Food f)
    {
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, f.getDate());
        values.put(KEY_NOTE, f.getNote());
        return values;
    }

    @Override
    public Object getObject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_FOOD + " WHERE " + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return parseFromCursor(c);
    }

    @Override
    public List<Object> getAll() {
        List<Object> foodRecords = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_FOOD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Food record = parseFromCursor(c);
                foodRecords.add(record);
            } while (c.moveToNext());
        }

        return foodRecords;
    }

    @Override
    public int update(int id, Object ob) {
        Food f = (Food)ob;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = formContentValues(f);
        // updating row
        return db.update(TABLE_FOOD, values, KEY_ID + " = ?",
                new String[] { String.valueOf(f.getId()) });
    }

}
