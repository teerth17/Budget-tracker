package com.teerth.budget_app.auth.model;

import java.util.UUID;

public class Budget {

    private UUID budget_id;
    private Double budget_amount;
    private UUID account_id;
    private UUID category_id;
    private int month;
    private int year;

    public UUID getBudget_id(){
        return budget_id;
    }
    public void setBudget_id(UUID budgetId){
        this.budget_id = budgetId;
    }

    public Double getBudget_amount(){
        return budget_amount;
    }
    public void setBudget_amount(Double amount){
        this.budget_amount = amount;
    }

    public UUID getAccount_id() {
        return account_id;
    }
    public void setAccount_id(UUID accountId){
        this.account_id = accountId;
    }

    public UUID getCategory_id() {
        return category_id;
    }
    public void setCategory_id(UUID categoryId) {
        this.category_id = categoryId;
    }

    public boolean isGeneralBudget() {
        return this.category_id == null;
    }

    public int getMonth() {
        return month;
    }
    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
}
