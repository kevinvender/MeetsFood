package com.sidera.meetsfood.data;

import java.util.Date;

/**
 * Created by cristian.stenico on 09/09/2015.
 */
public class Tariffa {
    private String service;
    private double cost;
    private Date from;
    private Date to;

    public Tariffa(String service, double cost, Date from, Date to) {
        this.setService(service);
        this.setCost(cost);
        this.setFrom(from);
        this.setTo(to);
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
}
