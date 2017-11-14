package com.repina.anastasia.momandbaby.DataBase.DataBaseClasses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class User  {
    private String id;
    private Date date;
    private String name;
    private String gender;
    private String bandCode;
    private String bandStatus;
    private int momID;

    public User(String id, String name, Date date, String gender, String bandCode, String bandStatus, int momID)
    {
        this.id = id;
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

    public void setMomID(int momID) {
        this.momID = momID;
    }

    public int getMomID() {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public void setDate(Date dateOfBirth) {
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
