package com.repina.anastasia.momandbaby.DataBase.DataBaseClasses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Teeth {
    private int id;
    private Date date;
    private int teethNum;
    private String note;

    public Teeth(int id, Date date, int teethNum, String note) {
        this.id = id;
        this.date = date;
        this.teethNum = teethNum;
        this.note = note;
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

    public int getTeethNum() {
        return teethNum;
    }

    public void setTeethNum(int teethNum) {
        this.teethNum = teethNum;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
