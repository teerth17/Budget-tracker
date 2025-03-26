package com.teerth.budget_app.auth.model;

public class ExpenseDTO {
    private Expense expense;
    private String categoryName;

    public ExpenseDTO(Expense expense,String categoryName){
        this.expense = expense;
        this.categoryName = categoryName;
    }

    public Expense getExpense(){
        return expense;
    }
    public String getCategoryName(){
        return categoryName;
    }
}
