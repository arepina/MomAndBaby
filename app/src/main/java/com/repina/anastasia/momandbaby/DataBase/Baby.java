package com.repina.anastasia.momandbaby.DataBase;

/**
 * Baby
 */
public class Baby {
    private String momId;
    private String birthDay;
    private String gender;
    private String name;

    public Baby() {
    }

    public Baby(String momId, String birthDay, String name, String gender) {
        this.momId = momId;
        this.birthDay = birthDay;
        this.gender = gender;
        this.name = name;
    }

    public String getMomId() {
        return momId;
    }

    public void setMomId(String momId) {
        this.momId = momId;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String note) {
        this.name = note;
    }
}
