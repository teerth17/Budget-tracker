package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.dao.IncomeDao;
import com.teerth.budget_app.auth.model.Income;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AddUpdateIncomeService {

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private IncomeSchedulerService incomeSchedulerService;

    public UUID addOrUpdateIncome(Income income, String interval){
        income.setTitle(income.getTitle().trim().toLowerCase());

        List<Income> existingIncomes = incomeDao.getIncomeByTitle(income.getTitle());

        if(existingIncomes != null && !existingIncomes.isEmpty()){
            Income existingIncome = existingIncomes.get(0);
            double updatedAmount = existingIncome.getAmount() + income.getAmount();
//            income.setAmount(updatedAmount);


            incomeDao.updateIncomeAmountByTitle(existingIncome.getTitle().toLowerCase(),updatedAmount);

            if(interval != null && !interval.isEmpty()){
                incomeDao.updateIncomeStatusAndIntervalById(true,interval,existingIncome.getIncome_id());
                scheduleIncomeTask(existingIncome,interval,income.getAmount());
            }
            return existingIncome.getIncome_id();
        }else{
            UUID incomeId = incomeDao.addIncome(income);
            if(interval != null && !interval.isEmpty()){
                incomeDao.updateIncomeStatusAndIntervalById(true,interval,incomeId);
                scheduleIncomeTask(income,interval,income.getAmount());
            }
            return incomeId;
        }


    }

    private void scheduleIncomeTask(Income income,String interval,double incrementAmount){
        Runnable task = () -> {
            double updatedAmount = incomeDao.getIncomeByTitle(income.getTitle()).get(0).getAmount() + incrementAmount;
            incomeDao.updateIncomeAmountByTitle(income.getTitle(),updatedAmount);
        };
        incomeSchedulerService.schedulerIncomeAddition(income,task);
    }

    public void initializeScheduleIncomes() {
        List<Income> automatedIncomes = incomeDao.getAutomatedIncomes();
        for(Income income: automatedIncomes){
            scheduleIncomeTask(income,income.getInterval(),income.getAmount());
        }
    }

    public void stopAutomation(Income income){
        incomeDao.updateIncomeStatusAndIntervalById(false,null,income.getIncome_id());

        incomeSchedulerService.cancelScheduledIncomeTask(income.getIncome_id());
    }
}
