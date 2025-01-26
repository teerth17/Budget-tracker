package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.dao.BudgetDao;
import com.teerth.budget_app.auth.dao.BudgetDaoImpl;
import com.teerth.budget_app.auth.model.Budget;
import com.teerth.budget_app.auth.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// responsible for insert,update,retreive the budget limits and checking the limit got triggered or not?.
@Service
public class BudgetLimitService {

    @Autowired
    private BudgetDao budgetDao;

    public UUID insertBudgetLimit(Budget budget){
        if(budget != null){
            return budgetDao.insertBudgetLimit(budget);
        }

        return null;
    }
    public void updateBudgetLimit(Double amount, UUID budget_id){
        if(amount != null && budget_id != null){
            budgetDao.updateBudgetLimit(amount,budget_id);
        }
    }

    public Budget getBudgetWithAccountId(UUID account_id){
        if(account_id != null){
            return budgetDao.retreiveBudgetWithAccountId(account_id);
        }
        return null;
    }

    public boolean checkBudgetLimitTriggered(Double currentTotalExpense,UUID account_id){
        Budget budget =budgetDao.retreiveBudgetWithAccountId(account_id);

        if(budget != null){
            if(budget.getBudget_amount() >= currentTotalExpense){
                System.out.println("limit triggered" + budget.getBudget_amount());
                return true;
            }
        }

        return false;
    }

    public void budgetlimitTriggered(){

    }
}
