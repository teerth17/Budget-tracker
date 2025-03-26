package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.dao.IncomeDao;
import com.teerth.budget_app.auth.model.Category;
import com.teerth.budget_app.auth.model.Income;
import com.teerth.budget_app.auth.model.IncomeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AddUpdateIncomeService {

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private IncomeSchedulerService incomeSchedulerService;

    @Autowired
    private CategoryService categoryService;

    public UUID addOrUpdateIncome(Income income, String interval, UUID categoryId){
        income.setTitle(income.getTitle().trim().toLowerCase());
        System.out.println("her eis the interval" + interval );
        System.out.println("got this userId: " + income.getUserAccountId());

        Category category = categoryService.findByd(categoryId);
        income.setCategoryId(category.getCategory_id());

        List<Income> existingIncomes = incomeDao.getIncomeByTitleAndAccountID(income.getTitle(), income.getUserAccountId());

        if(existingIncomes != null && !existingIncomes.isEmpty()){
            Income existingIncome = existingIncomes.get(0);
            System.out.println("existing: " + existingIncome);
            System.out.println("got this interval: "+ interval);
            System.out.println("interval from interval: " + income.getInterval());

//            income.setAmount(updatedAmount);

            if(interval == null || interval.isEmpty()){
                double updatedAmount = existingIncome.getAmount() + income.getAmount();
                incomeDao.updateIncomeAmountByTitleAndAccountId(existingIncome.getTitle().toLowerCase(),updatedAmount,income.getUserAccountId());
            }


            if(interval != null && !interval.isEmpty()){
                System.out.println("passing this interva; :" + interval);
                System.out.println("exisiting income id: " + existingIncome.getIncome_id());
                incomeDao.updateIncomeStatusAndIntervalById(true,interval,existingIncome.getIncome_id());
                System.out.println("interval before schcedule" + income.getInterval());
                scheduleIncomeTask(existingIncome,interval,income.getAmount());
            }
            return existingIncome.getIncome_id();
        }else{
            System.out.println("entered in unique: ");
            UUID incomeId = incomeDao.addIncome(income,categoryId);
            income.setIncome_id(incomeId);
            System.out.println("here is the income-id: " + incomeId);
            System.out.println("here us the account: " + income.getUserAccountId());
            if(interval != null && !interval.isEmpty()){
//                incomeDao.updateIncomeStatusAndIntervalById(true,interval,incomeId);
//                scheduleIncomeTask(income,interval,income.getAmount());

                try {
                    incomeDao.updateIncomeStatusAndIntervalById(true, interval, incomeId);
                    System.out.println("Successfully updated income status.");
                } catch (Exception e) {
                    System.out.println("Error updating income status: " + e.getMessage());
                    e.printStackTrace();
                }

                System.out.println("Scheduling income task...");

                try {
                    scheduleIncomeTask(income, interval, income.getAmount());
                    System.out.println("Successfully scheduled income task.");
                } catch (Exception e) {
                    System.out.println("Error scheduling income task: " + e.getMessage());
                    e.printStackTrace();
                }

            }
            System.out.println("income Id...: " + incomeId);
            return incomeId;
        }


    }

//    private void scheduleIncomeTask(Income income,String interval,double incrementAmount){
//
//        Runnable task = () -> {
//            double updatedAmount = incomeDao.getIncomeByTitle(income.getTitle()).get(0).getAmount() + incrementAmount;
//            incomeDao.updateIncomeAmountByTitle(income.getTitle(),updatedAmount);
//        };
//        incomeSchedulerService.schedulerIncomeAddition(income,interval,task);
//    }
private void scheduleIncomeTask(Income income, String interval, double incrementAmount) {
    System.out.println("Scheduling task for income: " + (income != null ? income.getTitle() : "NULL") +
            " with interval: " + interval +
            " and increment amount: " + incrementAmount);

    if (income == null) {
        System.out.println("ERROR: Income object is NULL!");
        return;
    }

    if (income.getTitle() == null) {
        System.out.println("ERROR: Income title is NULL!");
        return;
    }

    if (interval == null || interval.isEmpty()) {
        System.out.println("ERROR: Interval is NULL or empty!");
        return;
    }

    try {
        Runnable task = () -> {
            try {
                System.out.println("got this account: " + income.getUserAccountId());
                List<Income> incomeList = incomeDao.getIncomeByTitleAndAccountID(income.getTitle(),income.getUserAccountId());
                if (incomeList == null || incomeList.isEmpty()) {
                    System.out.println("ERROR: No income found with title: " + income.getTitle());

                    incomeSchedulerService.cancelScheduledIncomeTask(income.getIncome_id());
                    return;
                }

                double updatedAmount = incomeList.get(0).getAmount() + incrementAmount;
                System.out.println("Updating income: " + income.getTitle() + " with new amount: " + updatedAmount);
                incomeDao.updateIncomeAmountByTitleAndAccountId(income.getTitle(), updatedAmount,income.getUserAccountId());
                System.out.println("Scheduled task executed successfully.");
            } catch (Exception e) {
                System.out.println("Error executing scheduled task: " + e.getMessage());
                e.printStackTrace();
            }
        };

        try {
            System.out.println("Before calling schedulerIncomeAddition...");
            incomeSchedulerService.schedulerIncomeAddition(income, interval, task);
            System.out.println("Scheduler task successfully added.");
        } catch (Exception e) {
            System.out.println("❌ ERROR adding scheduled task: " + e.getMessage());
            e.printStackTrace();
        }


    } catch (Exception e) {
        System.out.println("Error adding scheduled task: " + e.getMessage());
        e.printStackTrace();
    }
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
        System.out.println("after stop automation");
    }

    public List<IncomeDTO> getIncomeListWithCategoryName(UUID accountId) {
        List<Income> incomeList = incomeDao.getAllIncomeByAccountId(accountId);
        List<IncomeDTO> incomeDTOList = new ArrayList<>();

        for (Income income : incomeList){
            System.out.println("categoryId: " + income.getCategoryId());

            if(income.getCategoryId() != null){
                String categoryName = categoryService.findByd(income.getCategoryId()).getName();
                incomeDTOList.add(new IncomeDTO(income,categoryName));
            }
            else {
                incomeDTOList.add(new IncomeDTO(income,null));
            }


        }
        return  incomeDTOList;
    }

    public List<IncomeDTO> getLastFiveIncomeByAccountId(UUID account_id){
            System.out.println("getting last five incomes");
            List<Income> incomesList = incomeDao.getLastFiveIncomeByAccountId(account_id);

        if(incomesList.isEmpty() && incomesList == null){
            System.out.println("error getting last five incomes: " + incomesList);
        }
        List<IncomeDTO> incomeDTOList = new ArrayList<>();

        for (Income income : incomesList){
            System.out.println("categoryId: " + income.getCategoryId());

            if(income.getCategoryId() != null){
                String categoryName = categoryService.findByd(income.getCategoryId()).getName();
                incomeDTOList.add(new IncomeDTO(income,categoryName));
            }
            else {
                incomeDTOList.add(new IncomeDTO(income,null));
            }
        }

        return incomeDTOList;
    }
}
