package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.dao.ExpenseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExpenseAnalysisService {

    @Autowired
    private ExpenseDao expenseDao;

    public List<Map<String,Object>> getExpenseSummaryByCategory(){

        return expenseDao.getAllExpenseCategoriesWithTotalExpense();
    }
}
