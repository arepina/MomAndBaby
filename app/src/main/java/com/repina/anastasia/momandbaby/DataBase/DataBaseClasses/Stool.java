package com.repina.anastasia.momandbaby.DataBase.DataBaseClasses;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Stool {
    private int id;
    private Date date;
    private int abundance;

    public Stool(int id, Date date, int abundance) {
        this.id = id;
        this.date = date;
        this.abundance = abundance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getAbundance() {
        return abundance;
    }

    public void setAbundance(int abundance) {
        this.abundance = abundance;
    }
}
