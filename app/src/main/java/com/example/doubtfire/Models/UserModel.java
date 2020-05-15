package com.example.doubtfire.Models;

public class UserModel {
    public String uid;
    public String name;
    public String number;

    public UserModel(){}

    public UserModel(String uid, String name, String number) {
        this.uid = uid;
        this.name = name;
        this.number = number;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
