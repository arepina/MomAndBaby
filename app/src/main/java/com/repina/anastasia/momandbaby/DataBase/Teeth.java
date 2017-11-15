package com.repina.anastasia.momandbaby.DataBase;


public class Teeth {
    private String babyId;
    private String date;
    private int teethNum;
    private String note;

    public Teeth(String babyId, String date, int teethNum, String note) {
        this.babyId = babyId;
        this.date = date;
        this.teethNum = teethNum;
        this.note = note;
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

    public int getTeethNum() {
        return teethNum;
    }

    public void setTeethNum(int teethNum) {
        this.teethNum = teethNum;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
