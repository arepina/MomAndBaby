package com.repina.anastasia.momandbaby.DataBase;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Illness  {
    private String id;
    private Date date;
    private String symptomes;
    private String pills;
    private double temperature;

    public Illness(String id, Date date, String symptomes, String pills, double temperature) {
        this.id = id;
        this.date = date;
        this.symptomes = symptomes;
        this.pills = pills;
        this.temperature = temperature;
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

    public String getSymptomes() {
        return symptomes;
    }

    public void setSymptomes(String symptomes) {
        this.symptomes = symptomes;
    }

    public String getPills() {
        return pills;
    }

    public void setPills(String pills) {
        this.pills = pills;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
