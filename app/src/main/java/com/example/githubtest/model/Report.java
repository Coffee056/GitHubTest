package com.example.githubtest.model;

public class Report {
    private String type;    //确诊 or 康复
    private String name;
    private String IDnumber;
    private String tel;
    private String region;
    private String hospital;
    private String date;
    private String status;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getIDnumber() {
        return IDnumber;
    }
    public void setIDnumber(String IDnumber) {
        this.IDnumber = IDnumber;
    }

    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }

    public String getHospital() {
        return hospital;
    }
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
