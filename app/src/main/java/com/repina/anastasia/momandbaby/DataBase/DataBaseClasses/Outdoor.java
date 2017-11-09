package com.repina.anastasia.momandbaby.DataBase.DataBaseClasses;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Outdoor {
    private int id;
    private Date date;
    private double outdoorLength;

    public Outdoor(int id, Date date, double outdoorLength) {
        this.id = id;
        this.date = date;
        this.outdoorLength = outdoorLength;
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

    public double getOutdoorLength() {
        return outdoorLength;
    }

    public void setOutdoorLength(double outdoorLength) {
        this.outdoorLength = outdoorLength;
    }
}
