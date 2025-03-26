package com.teerth.budget_app.auth.model;

public class IncomeDTO {

    private Income income;
    private String categoryName;

    public IncomeDTO(Income income,String categoryName){
        this.income = income;
        this.categoryName = categoryName;
    }

    public Income getIncome(){
        return income;
    }
    public String getCategoryName(){
        return categoryName;
    }
}
