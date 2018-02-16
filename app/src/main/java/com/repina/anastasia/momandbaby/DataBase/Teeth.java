package com.repina.anastasia.momandbaby.DataBase;

import java.util.ArrayList;

/**
 * Teeth
 */
public class Teeth {

    private String babyId;
    private String date;
    private ArrayList<Boolean> doesHave;
    private ArrayList<Integer> whenHave;

    public Teeth() {
    }

    public Teeth(String babyId, ArrayList<Boolean> doesHave, ArrayList<Integer> whenHave, String date) {
        this.babyId = babyId;
        this.doesHave = doesHave;
        this.whenHave = whenHave;
        this.date = date;
    }

    public String getBabyId() {
        return babyId;
    }

    public void setBabyId(String babyId) {
        this.babyId = babyId;
    }

    public ArrayList<Boolean> getDoesHave() {
        return doesHave;
    }

    public void setDoesHave(ArrayList<Boolean> doesHave) {
        this.doesHave = doesHave;
    }

    public ArrayList<Integer> getWhenHave() {
        return whenHave;
    }

    public void setWhenHave(ArrayList<Integer> whenHave) {
        this.whenHave = whenHave;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
