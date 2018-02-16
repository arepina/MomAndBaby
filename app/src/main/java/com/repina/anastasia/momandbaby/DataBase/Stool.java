package com.repina.anastasia.momandbaby.DataBase;

import com.repina.anastasia.momandbaby.R;

/**
 * Diapers
 */
public class Stool {
    private String babyId;
    private String date;
    private String note;
    private int howMuch;

    public Stool() {
    }

    public Stool(String babyId, String date, String note, int howMuch) {
        this.babyId = babyId;
        this.date = date;
        this.note = note;
        this.howMuch = howMuch;
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

    public int getHowMuch() {
        return howMuch;
    }

    public void setHowMuch(int howMuch) {
        this.howMuch = howMuch;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Стул " + date + " " + R.string.сomment + " " + note + " " + R.string.rate_feature + " " + howMuch;
    }
}
