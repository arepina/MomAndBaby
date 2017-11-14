package com.repina.anastasia.momandbaby.DataBase;

public class User  {
    private String id;
    private String date;
    private String name;
    private String gender;
    private String bandCode;
    private String bandStatus;
    private String momID;

    public User(){}

    public User(String id, String name, String date, String gender, String bandCode, String bandStatus, String momID)
    {
        this.id = id;
        this.name = name;
        this.date = date;
        this.gender = gender;
        this.bandCode = bandCode;
        this.bandStatus = bandStatus;
        this.momID = momID;
    }

    public User(String name, String date, String gender, String bandCode, String bandStatus, String momID)
    {
        this.name = name;
        this.date = date;
        this.gender = gender;
        this.bandCode = bandCode;
        this.bandStatus = bandStatus;
        this.momID = momID;
    }

    public String getId() {
        return id;
    }

    public void setMomID(String momID) {
        this.momID = momID;
    }

    public String getMomID() {
        return momID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String dateOfBirth) {
        this.date = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBandCode() {
        return bandCode;
    }

    public void setBandCode(String bandCode) {
        this.bandCode = bandCode;
    }

    public String getBandStatus() {
        return bandStatus;
    }

    public void setBandStatus(String bandStatus) {
        this.bandStatus = bandStatus;
    }
}
