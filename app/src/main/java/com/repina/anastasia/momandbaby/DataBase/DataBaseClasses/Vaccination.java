package com.repina.anastasia.momandbaby.DataBase.DataBaseClasses;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Vaccination  {
    private String id;
    private Date date;
    private String vaccinationName;
    private String note;

    public Vaccination(String id, Date date, String vaccinationName, String note) {
        this.id = id;
        this.date = date;
        this.vaccinationName = vaccinationName;
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

    public String getVaccinationName() {
        return vaccinationName;
    }

    public void setVaccinationName(String vaccinationName) {
        this.vaccinationName = vaccinationName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
