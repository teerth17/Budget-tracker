package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.dao.ExpenseDao;
import com.teerth.budget_app.auth.model.*;
import com.teerth.budget_app.auth.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AddExpenseService {

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private ExpenseSchedulerService expenseSchedulerService;

    @Autowired
    private CategoryService categoryService;

    public UUID addOrUpdateExpense(Expense expense, String interval, UUID categoryId){
        expense.setTitle(expense.getTitle().trim().toLowerCase());
        System.out.println("her eis the interval" + interval );
        System.out.println("got this id user: " + expense.getUserAccountId());

        Category category = categoryService.findByd(categoryId);
        expense.setCategoryId(category.getCategory_id());

        List<Expense> existingExpenses = expenseDao.getExpenseByTitleAndAccountId(expense.getTitle(),expense.getUserAccountId());

        if(existingExpenses != null && !existingExpenses.isEmpty()){
            Expense existingExpense = existingExpenses.get(0);
            System.out.println("existing: " + existingExpense);
            System.out.println("got this interval: "+ interval);
            System.out.println("interval from interval: " + expense.getInterval());
            double updatedAmount = existingExpense.getAmount() + expense.getAmount();
//            expense.setAmount(updatedAmount);


            expenseDao.updateExpenseAmountByTitleAndAccountId(existingExpense.getTitle().toLowerCase(),updatedAmount,expense.getUserAccountId());

            if(interval != null && !interval.isEmpty()){
                System.out.println("passing this interva; :" + interval);
                System.out.println("exisiting expense id: " + existingExpense.getExpense_id());
                expenseDao.updateExpenseStatusAndIntervalById(true,interval,existingExpense.getExpense_id());
                System.out.println("interval before schcedule" + expense.getInterval());
                scheduleExpenseTask(existingExpense,interval,expense.getAmount());
            }
            return existingExpense.getExpense_id();
        }else{
            System.out.println("entered in unique: ");
            UUID expenseId = expenseDao.addExpense(expense,categoryId);
            expense.setExpenseId(expenseId);
            System.out.println("here is the expense-id: " + expenseId);
            if(interval != null && !interval.isEmpty()){

                try{
                    expenseDao.updateExpenseStatusAndIntervalById(true,interval,expenseId);
                    System.out.println("Successfully updated expense status");
                }catch (Exception e){
                    System.out.println("Error updating the expense status" + e.getMessage());
                    e.printStackTrace();
                }

                System.out.println("Scheduling income task...");

                try{
                    scheduleExpenseTask(expense,interval,expense.getAmount());
                    System.out.println("Successfully scheduled expense task..");
                }catch (Exception e){
                    System.out.println("Error schedling expense task: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("expense_id: " + expenseId);
            return expenseId;
        }


    }

    private void scheduleExpenseTask(Expense expense,String interval,double incrementAmount){

        System.out.println("Scheduling task for expense: " + (expense != null? expense.getTitle(): "NULL") +
                "with interval: "+ interval+
                "and increment amount: " + incrementAmount);

        if(expense == null){
            System.out.println("ERROR: Income object is NULL!");
            return;
        }
        if (expense.getTitle() == null) {
            System.out.println("ERROR: Expense title is NULL!");
            return;
        }

        if (interval == null || interval.isEmpty()) {
            System.out.println("ERROR: Interval is NULL or empty!");
            return;
        }

        try{
            Runnable task = () -> {
                try {
                    List<Expense> expenseList = expenseDao.getExpenseByTitleAndAccountId(expense.getTitle(),expense.getUserAccountId());
                    if(expenseList == null || expenseList.isEmpty()){
                        System.out.println("ERROR: No expense found with titel" + expense.getTitle());

                        expenseSchedulerService.cancelScheduledExpenseTask(expense.getExpense_id());
                        return;
                    }

                    double updateAmount = expenseList.get(0).getAmount() + incrementAmount;
                    System.out.println("Updating expense: " +expense.getTitle() + "with new amount: " + updateAmount);
                    expenseDao.updateExpenseAmountByTitleAndAccountId(expense.getTitle(),updateAmount,expense.getUserAccountId());
                    System.out.println("Scheduled task executed successfully.");
                }catch (Exception e){
                    System.out.println("Error schdeuling expense task: "+ e.getMessage());
                    e.printStackTrace();
                }
            };

            try {
                System.out.println("Before called scheduleExpenseAddition...");
                expenseSchedulerService.schedulerExpenseAddition(expense,interval,task);
                System.out.println("Schedule task added successfully..");
            }catch (Exception e){
                System.out.println("error adding task" + e.getMessage());
                e.printStackTrace();
            }
        }catch (Exception e){
            System.out.println("Error adding schedule task: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initializeScheduleExpenses() {
        List<Expense> automatedExpenses = expenseDao.getAutomatedExpense();
        for(Expense expense: automatedExpenses){
            scheduleExpenseTask(expense,expense.getInterval(),expense.getAmount());
        }
    }

    public void stopAutomation(Expense expense){
        expenseDao.updateExpenseStatusAndIntervalById(false,null,expense.getExpense_id());

        expenseSchedulerService.cancelScheduledExpenseTask(expense.getExpense_id());
        System.out.println("after stop automation");
    }

    public List<ExpenseDTO> getExpenseListWithCategoryName(UUID accountId) {
        List<Expense> expenseList = expenseDao.getAllExpenseByAccountId(accountId);
        List<ExpenseDTO> expenseDTOList = new ArrayList<>();

        for (Expense expense : expenseList){
            System.out.println("categoryId: " + expense.getCategoryId());

            if(expense.getCategoryId() != null){
                String categoryName = categoryService.findByd(expense.getCategoryId()).getName();
                expenseDTOList.add(new ExpenseDTO(expense,categoryName));
            }
            else {
                expenseDTOList.add(new ExpenseDTO(expense,null));
            }


        }
        return  expenseDTOList;
    }

    public List<ExpenseDTO> getLastFiveExpenseByAccountId(UUID account_id){
        System.out.println("getting last five incomes");
        List<Expense> expenseList = expenseDao.getLastFiveExpenseByAccountId(account_id);

        if(expenseList.isEmpty() && expenseList == null){
            System.out.println("error getting last five incomes: " + expenseList);
        }
        List<ExpenseDTO>  expenseDTOList= new ArrayList<>();

        for (Expense expense: expenseList){
            System.out.println("categoryId: " + expense.getCategoryId());

            if(expense.getCategoryId() != null){
                String categoryName = categoryService.findByd(expense.getCategoryId()).getName();
                expenseDTOList.add(new ExpenseDTO(expense,categoryName));
            }
            else {
                expenseDTOList.add(new ExpenseDTO(expense,null));
            }
        }

        return expenseDTOList;
    }

    public Map<UUID, Double> getCategoryWiseExpenses(UUID accountId) {
        return expenseDao.getCategoryExpensesByAccountId(accountId);
    }

}
