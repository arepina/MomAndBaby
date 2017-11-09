package com.repina.anastasia.momandbaby.DataBase.DataBaseClasses;

import java.util.Date;

public class User {
    private int id;
    private String name;
    private Date dateOfBirth;
    private String gender;
    private String bandCode;
    private String bandStatus;

    User(int id, String name, Date dateOfBirth, String gender, String bandCode, String bandStatus)
    {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
