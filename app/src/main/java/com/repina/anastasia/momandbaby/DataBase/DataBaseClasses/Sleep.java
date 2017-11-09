package com.repina.anastasia.momandbaby.DataBase.DataBaseClasses;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Sleep {
    private int id;
    private Date date;
    private double sleepDuration;

    public Sleep(int id, Date date, double sleepDuration) {
        this.id = id;
        this.date = date;
        this.sleepDuration = sleepDuration;
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

    public double getSleepDuration() {
        return sleepDuration;
    }

    public void setSleepDuration(double sleepDuration) {
        this.sleepDuration = sleepDuration;
    }
}
