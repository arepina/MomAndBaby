package com.repina.anastasia.momandbaby.DataBase;


public class BandData {
    private String momId;
    private String bandCode;
    private String date;
    private double steps;
    private int calories;
    private double sleepHours;

    public BandData() {
    }

    public BandData(String momId, String bandCode, String date, double steps, int calories, double sleepHours) {
        this.momId = momId;
        this.bandCode = bandCode;
        this.date = date;
        this.steps = steps;
        this.calories = calories;
        this.sleepHours = sleepHours;
    }

    public String getMomId() {
        return momId;
    }

    public void setMomId(String momId) {
        this.momId = momId;
    }

    public String getBandCode() {
        return bandCode;
    }

    public void setBandCode(String bandCode) {
        this.bandCode = bandCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(double sleepHours) {
        this.sleepHours = sleepHours;
    }
}
