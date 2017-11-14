package com.repina.anastasia.momandbaby.DataBase;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Baby  {
    private String id;
    private Date date;
    private double weight;
    private double height;
    private String note;

    public Baby(String id, Date date, double weight, double height, String note) {
        this.id = id;
        this.date = date;
        this.weight = weight;
        this.height = height;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
