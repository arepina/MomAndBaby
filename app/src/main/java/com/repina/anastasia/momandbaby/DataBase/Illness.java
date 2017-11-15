package com.repina.anastasia.momandbaby.DataBase;


public class Illness  {
    private String babyId;
    private String date;
    private String symptomes;
    private String pills;
    private double temperature;

    public Illness(String babyId, String date, String symptomes, String pills, double temperature) {
        this.babyId = babyId;
        this.date = date;
        this.symptomes = symptomes;
        this.pills = pills;
        this.temperature = temperature;
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
