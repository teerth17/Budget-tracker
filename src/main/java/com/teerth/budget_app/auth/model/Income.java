package com.teerth.budget_app.auth.model;

import jakarta.persistence.Id;

import java.util.UUID;

public class Income {


    private String date;
    private boolean automatedStatus;
    private String title;
    private String description;
    private double amount;
    private UUID income_id;
    private UUID account_id;
    private String interval;


//    public Income(String description,int amount){
//        this.description = description;
//        this.amount = amount;
//    }

    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public boolean getAutomatedStatus(){
        return automatedStatus;
    }
    public void setAutomatedStatus(boolean automatedStatus){
        this.automatedStatus = automatedStatus;
    }

    public String getInterval(){
        return interval;
    }
    public void setInterval(String interval){
        this.interval = interval;
    }

    public String getTitle(){
        return title;
    }
    public  void setTitle(String title){
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount){
        this.amount= amount;
    }

    public UUID getIncome_id(){
        return income_id;
    }
    public void setIncome_id(UUID income_id){
        this.income_id = income_id;
    }

    public UUID getUserAccountId(){
        return account_id;
    }
    public void setUserAccountId(UUID account_id){
        this.account_id = account_id;
    }

    @Override
    public String toString() {
        return "Income{"
                +
                "income_id='" + income_id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }

}
