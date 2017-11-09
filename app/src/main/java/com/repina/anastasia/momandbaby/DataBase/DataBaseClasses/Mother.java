package com.repina.anastasia.momandbaby.DataBase.DataBaseClasses;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Mother {
    private int id;
    private Date date;
    private double sleepLength;
    private int steps;
    private int calories;
    private double weight;

    public Mother(int id, Date date, double sleepLength, int steps, int calories, double weight) {
        this.id = id;
        this.date = date;
        this.sleepLength = sleepLength;
        this.steps = steps;
        this.calories = calories;
        this.weight = weight;
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

    public double getSleepLength() {
        return sleepLength;
    }

    public void setSleepLength(double sleepLength) {
        this.sleepLength = sleepLength;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
