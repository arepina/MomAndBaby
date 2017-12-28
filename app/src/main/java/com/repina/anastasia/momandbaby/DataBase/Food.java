package com.repina.anastasia.momandbaby.DataBase;

import com.repina.anastasia.momandbaby.R;

public class Food {
    private String babyId;
    private String date;
    private String note;
    private int howMuch;

    public Food() {
    }

    public Food(String babyId, String date, String note, int howMuch) {
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getHowMuch() {
        return howMuch;
    }

    public void setHowMuch(int howMuch) {
        this.howMuch = howMuch;
    }

    @Override
    public String toString() {
        return "Питание " + date + " " + R.string.сomment + " " + note + " " + R.string.rate_feature + " " + howMuch;
    }
}
