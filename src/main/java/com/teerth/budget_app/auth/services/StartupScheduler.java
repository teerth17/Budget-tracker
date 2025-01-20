package com.teerth.budget_app.auth.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartupScheduler {

    @Autowired
    private AddUpdateIncomeService addUpdateIncomeService;

    @PostConstruct
    public void initializeSchedulers(){
        addUpdateIncomeService.initializeScheduleIncomes();
    }
}
