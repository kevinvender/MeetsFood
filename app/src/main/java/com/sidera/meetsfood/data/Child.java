package com.sidera.meetsfood.data;

import com.sidera.meetsfood.data.Tariffa;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cristian.stenico on 09/09/2015.
 */
public class Child {
    private int id;
    private String firstName;
    private String lastName;
    private String taxNumber;
    private String school;
    private int schoolClass;
    private String schoolYear;
    private Date subscriptionDate;
    private ArrayList<String> intolerances;
    private ArrayList<Tariffa> tariffe;
    private double saldo;

    public Child(int id) {
        this.id = id;
    }

    public Child(int id, String firstName, String lastName, String taxNumber, double saldo,
                 String school, int schoolClass, String schoolYear, Date subscriptionDate,
                 ArrayList<String> intolerances, ArrayList<Tariffa> tariffe){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.taxNumber = taxNumber;
        this.saldo = saldo;
        this.school = school;
        this.schoolClass = schoolClass;
        this.schoolYear = schoolYear;
        this.subscriptionDate = subscriptionDate;
        this.intolerances = intolerances;
        this.tariffe = tariffe;
    }

    public void setSaldo(double saldo){
        this.saldo = saldo;
    }

    public double getSaldo(){
        return saldo;
    }

    public ArrayList<String> getIntolerances(){
        return intolerances;
    }

    public ArrayList<Tariffa> getTariffe(){
        return tariffe;
    }

    public void addIntolerance(String intolerance){
        if(intolerances == null)
            intolerances = new ArrayList<String>();
        intolerances.add(intolerance);
    }

    public void addTariffa(Tariffa tariffa){
        if(tariffe == null)
            tariffe = new ArrayList<Tariffa>();
        tariffe.add(tariffa);
    }

    public int getID(){
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(int schoolClass) {
        this.schoolClass = schoolClass;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }
}
