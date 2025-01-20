package com.teerth.budget_app.auth.services;


import com.teerth.budget_app.auth.dao.ExpenseDao;
import com.teerth.budget_app.auth.dao.IncomeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MonthlyIncomeExpenseService {

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private ExpenseDao expenseDao;

    public List<Object[]> getMonthlyIncome(UUID account_id, LocalDate startDate, LocalDate endDate){
        return incomeDao.getIncomeGroupByMonth(account_id,startDate,endDate);
    }

    public List<Object[]> getMonthlyExpense(UUID account_id,LocalDate startDate, LocalDate endDate){
        return expenseDao.getExpenseGroupByMonth(account_id, startDate,endDate);
    }
}
