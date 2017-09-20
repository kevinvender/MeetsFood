package com.sidera.meetsfood.api.beans;



public class JWTPayload {

    public User user;
    public String role;
    public String exp;
    public String iat;

    public JWTPayload() {}
}
