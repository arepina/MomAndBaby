package com.repina.anastasia.momandbaby.DataBase;

public class Stool  {
    private String babyId;
    private String date;
    private int howMuch;

    public Stool(String babyId, String date, int howMuch) {
        this.babyId = babyId;
        this.date = date;
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
}
