package com.repina.anastasia.momandbaby.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.TimeZone;

import static android.provider.CalendarContract.CalendarCache.URI;

public class GoogleCalendar {

    public static void getAllCalendars(Context context) {
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");
        String[] projection = new String[]{"_id", "name"};
        Cursor managedCursor = context.getContentResolver().query(calendars,
                projection, null, null, null);
        if (managedCursor != null && managedCursor.moveToFirst()) {
            String calName;
            String calID;
            int nameColumn = managedCursor.getColumnIndex("name");
            int idColumn = managedCursor.getColumnIndex("_id");
            do {
                calName = managedCursor.getString(nameColumn);
                calID = managedCursor.getString(idColumn);
                Log.e("Cal name", calName + " " + calID);
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }
    }

    public static void insertEvent(Context context, String calID, String title, String desc, long start, long end) {
        ContentValues event = new ContentValues();
        event.put("calendar_id", calID);  // ID календаря мы получили ранее
        event.put("title", title); // Название события
        event.put("description", desc); // Описание события
        event.put("dtstart", start); // время начала
        event.put("dtend", end); // время окончания
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        Uri eventsURI = Uri.parse("content://com.android.calendar/events");
        context.getContentResolver().insert(eventsURI, event);
    }
}
