package com.repina.anastasia.momandbaby.DataBase;


public class Vaccination  {
    private String babyId;
    private String date;
    private String vaccinationName;
    private String note;

    public Vaccination(){}

    public Vaccination(String babyId, String date, String vaccinationName, String note) {
        this.babyId = babyId;
        this.date = date;
        this.vaccinationName = vaccinationName;
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

    public String getVaccinationName() {
        return vaccinationName;
    }

    public void setVaccinationName(String vaccinationName) {
        this.vaccinationName = vaccinationName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
