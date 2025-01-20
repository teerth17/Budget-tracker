package com.teerth.budget_app.auth.model;

import jakarta.persistence.Id;

import java.util.UUID;

public class Expense {


    private String title;
    private String description;
    private double amount;
    private UUID expense_id;
    private UUID account_id;



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

    public UUID getExpense_id(){
        return expense_id;
    }
    public void setExpenseId(UUID expense_id){
        this.expense_id = expense_id;
    }

    public UUID getUserAccountId(){
        return account_id;
    }
    public void setUserAccountId(UUID account_id){
        this.account_id = account_id;
    }

    @Override
    public String toString() {
        return "Expense{"
                +
                "expense_id='" + expense_id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }

}
