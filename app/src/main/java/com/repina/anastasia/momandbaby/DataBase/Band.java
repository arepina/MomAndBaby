package com.repina.anastasia.momandbaby.DataBase;


public class Band {
    private String status;
    private String code;
    private int goal;
    private String momId;

    public Band(String status, String code, int goal, String momId) {
        this.status = status;
        this.code = code;
        this.goal = goal;
        this.momId = momId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getMomId() {
        return momId;
    }

    public void setMomId(String momId) {
        this.momId = momId;
    }
}
