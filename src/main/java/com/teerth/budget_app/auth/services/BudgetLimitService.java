package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.dao.BudgetDao;
import com.teerth.budget_app.auth.dao.BudgetDaoImpl;
import com.teerth.budget_app.auth.dao.CategoryDao;
import com.teerth.budget_app.auth.model.Budget;
import com.teerth.budget_app.auth.model.Category;
import com.teerth.budget_app.auth.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// responsible for insert,update,retreive the budget limits and checking the limit got triggered or not?.
@Service
public class BudgetLimitService {

    @Autowired
    private BudgetDao budgetDao;

    @Autowired
    private CategoryService categoryService;

    public UUID insertBudgetLimit(Budget budget){
        if (budget != null && budget.getCategory_id() != null) {
            return budgetDao.insertBudgetLimit(budget);
        }
        return null;
    }

    public UUID insertGeneralBudgetLimit(Budget budget){
        if(budget != null){
            return budgetDao.insertGeneralBudgetLimit(budget);
        }
        return null;
    }
    public void updateBudgetLimit(Budget budget) {
        if (budget.getBudget_amount() != null && budget.getCategory_id() == null ){
            budgetDao.updateBudgetLimit(budget.getBudget_amount(), budget.getAccount_id(), null);
        }
        if (budget.getBudget_amount() != null && budget.getCategory_id() != null) {
            budgetDao.updateBudgetLimit(budget.getBudget_amount(), budget.getAccount_id(), budget.getCategory_id());
        }
    }

    public void deleteBudgetLimit(Budget budget){
        if(budget.getCategory_id() == null){
            System.out.println("inside service: " + budget.getBudget_id());
            budgetDao.deleteBudgetLimit(budget.getBudget_id(), null);
        }else {
            System.out.println("inside service else: " + budget.getBudget_id());
            budgetDao.deleteBudgetLimit(budget.getBudget_id(),budget.getCategory_id());
        }
    }

    public UUID getBudgetId(UUID accountId,UUID categoryId){
        System.out.println("inside getBudget service");
        return  budgetDao.getBudgetId(accountId,categoryId);
    }

    public Budget getBudgetWithAccountIdAndCategory(UUID account_id, UUID category_id, int month, int year) {
        if (account_id != null && category_id != null) {
            return budgetDao.retrieveBudgetWithAccountIdAndCategory(account_id, category_id, month, year);
        }
        return null;
    }

    public boolean checkBudgetLimitTriggered(Double currentTotalExpense, UUID account_id, UUID category_id, int month, int year) {
        Budget budget = budgetDao.retrieveBudgetWithAccountIdAndCategory(account_id, category_id, month, year);

        if (budget != null) {
            return currentTotalExpense >= budget.getBudget_amount() * 0.8; // 80% threshold
        }

        return false;
    }

    public List<Budget> getAllBudgetsForUser(UUID account_id) {
        return budgetDao.getAllBudgetsForUser(account_id);
    }

    public Map<String, Double> getCategoryBudgetsByAccountId(UUID accountId){

        System.out.println("entered in category budget service");
        Map<String,Double> catergoryIdAndAmount = budgetDao.getCategoryBudgetsByAccountId(accountId);

        System.out.println("get id and amount: " + catergoryIdAndAmount);
//        Map<String, Double> categoryNameAndAmount = new HashMap<>();


//        for(Map.Entry<String,Double> entry: catergoryIdAndAmount.entrySet()){
//            String categoryIdStr = entry.getKey();
//            Double amount = entry.getValue();
//
//            if("General".equals(categoryIdStr)){
//                categoryNameAndAmount.put("General",amount);
//            }else{
//                UUID categoryId = UUID.fromString(categoryIdStr);
//                Category category = categoryService.findByd(categoryId);
//                System.out.println("category from servise: " + category);
//                if(category != null){
//                    categoryNameAndAmount.put(category.getName(),amount);
//                }
//            }
//
//        }

//        System.out.println("category name and amount before return: " + categoryNameAndAmount);
//        return categoryNameAndAmount;
        return catergoryIdAndAmount;
    }

    public Double getTotalBudgetWithAccountId(UUID accountId) {
        Double totalBudget = budgetDao.getTotalBudgetByAccountId(accountId);
        return (totalBudget != null) ? totalBudget : 0.0;  // Return 0 if no budget is set
    }
}
