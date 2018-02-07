package com.repina.anastasia.momandbaby.DataBase;

import android.util.Pair;

import java.util.ArrayList;

public class Teeth {

    private String babyId;
    private ArrayList<Pair<Integer, Boolean>> doesHave;
    private ArrayList<Pair<Integer, String>> whenHave;

    public Teeth() {
    }

    public Teeth(String babyId, ArrayList<Pair<Integer, Boolean>> doesHave, ArrayList<Pair<Integer, String>> whenHave) {
        this.babyId = babyId;
        this.doesHave = doesHave;
        this.whenHave = whenHave;
    }

    public String getBabyId() {
        return babyId;
    }

    public void setBabyId(String babyId) {
        this.babyId = babyId;
    }

    public ArrayList<Pair<Integer, Boolean>> getDoesHave() {
        return doesHave;
    }

    public void setDoesHave(ArrayList<Pair<Integer, Boolean>> doesHave) {
        this.doesHave = doesHave;
    }

    public ArrayList<Pair<Integer, String>> getWhenHave() {
        return whenHave;
    }

    public void setWhenHave(ArrayList<Pair<Integer, String>> whenHave) {
        this.whenHave = whenHave;
    }
}
