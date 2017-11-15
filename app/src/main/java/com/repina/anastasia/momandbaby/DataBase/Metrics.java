package com.repina.anastasia.momandbaby.DataBase;

public class Metrics {
    private String babyId;
    private double weight;
    private double height;
    private String date;

    public Metrics(String babyId, double weight, double height, String date) {
        this.babyId = babyId;
        this.weight = weight;
        this.height = height;
        this.date = date;
    }

    public String getBabyId() {
        return babyId;
    }

    public void setBabyId(String babyId) {
        this.babyId = babyId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
