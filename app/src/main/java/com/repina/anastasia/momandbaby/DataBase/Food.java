package com.repina.anastasia.momandbaby.DataBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Food {
    private String id;
    private Date date;
    private String note;

    public Food(String id, Date date, String note) {
        this.id = id;
        this.date = date;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
