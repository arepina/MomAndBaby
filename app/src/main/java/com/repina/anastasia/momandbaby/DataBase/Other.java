package com.repina.anastasia.momandbaby.DataBase;


import com.repina.anastasia.momandbaby.R;

/**
 * Other feature
 */
public class Other {
    private String babyId;
    private String date;
    private String note;

    public Other() {
    }

    public Other(String babyId, String date, String note) {
        this.babyId = babyId;
        this.date = date;
        this.note = note;
    }

    public String getBabyId() {
        return babyId;
    }

    public void setBabyId(String babyId) {
        this.babyId = babyId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Другое/Планы " + date + " " + R.string.сomment + " " + note + " " + R.string.rate_feature;
    }
}
