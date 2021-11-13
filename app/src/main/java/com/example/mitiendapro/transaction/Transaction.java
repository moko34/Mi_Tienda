package com.example.mitiendapro.transaction;

import java.io.Serializable;

public class Transaction  implements Serializable {
    private int date;
    private String description;
    private int amount;
    private String month;
    private  int year;


    public Transaction(int date, String description, int amount, String month,int year) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.month=month;
        this.year=year;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}


