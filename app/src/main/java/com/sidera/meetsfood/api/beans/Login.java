package com.sidera.meetsfood.api.beans;



public class Login {
    public String username;
    public String password;
    public String operatingSystem;

    public Login(String user, String pwd,String operatingSystem) {
        this.username = user;
        this.password = pwd;
        this.operatingSystem = operatingSystem;
    }
}
