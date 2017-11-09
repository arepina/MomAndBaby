package com.repina.anastasia.momandbaby.DataBase.DataBaseClasses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class User {
    private int id;
    private Date date;
    private String name;
    private String gender;
    private String bandCode;
    private String bandStatus;

    public User(int id, String name, Date date, String gender, String bandCode, String bandStatus)
    {
        this.id = id;
        this.name = name;
        this.date = date;
        this.gender = gender;
        this.bandCode = bandCode;
        this.bandStatus = bandStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
