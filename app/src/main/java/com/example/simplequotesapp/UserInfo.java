package com.example.simplequotesapp;

//사용자 데이터모델
public class UserInfo {
    private String Uid;
    private String email;
    private String password;

    public UserInfo(){}

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
