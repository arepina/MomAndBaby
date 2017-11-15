package com.repina.anastasia.momandbaby.DataBase;

public class Outdoor  {
    private String babyId;
    private String date;
    private double length;

    public Outdoor(String babyId, String date, double length) {
        this.babyId = babyId;
        this.date = date;
        this.length = length;
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

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
}
