package com.teerth.budget_app.auth.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartupScheduler {

    @Autowired
    private AddUpdateIncomeService addUpdateIncomeService;

    @Autowired
    private AddExpenseService addExpenseService;

    @PostConstruct
    public void initializeSchedulers(){
        try {
            System.out.println("Initializing income schedulers...");
            addUpdateIncomeService.initializeScheduleIncomes();

            System.out.println("Initializing expense schedulers...");
            addExpenseService.initializeScheduleExpenses();

            System.out.println("Schedulers initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing schedulers: " + e.getMessage());
        }
    }
}
