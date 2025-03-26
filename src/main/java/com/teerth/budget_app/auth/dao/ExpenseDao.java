package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Expense;
import com.teerth.budget_app.auth.model.Income;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExpenseDao {

    UUID addExpense(Expense expense,UUID categoryId);

    List<Expense> getAllExpenseByAccountId(UUID account_id);

    List<Expense> getLastFiveExpenseByAccountId(UUID account_id);

    void deleteExpense(UUID expense_id);

    List<Object[]> getExpenseGroupByMonth(UUID account_id, LocalDate startDate, LocalDate endDate);

    void updateExpenseAmountByTitleAndAccountId(String title,double amount, UUID account_id);

    void updateExpenseStatusAndIntervalById(boolean status,String interval, UUID expense_id);

    List<Expense> getAutomatedExpense();

    List<Expense> getExpenseByTitleAndAccountId(String title, UUID account_id);

    List<Expense> getExpenseByIncomeId(UUID expense_id);

    List<Map<String,Object>> getAllExpenseCategoriesWithTotalExpense();

    public Map<UUID, Double> getCategoryExpensesByAccountId(UUID accountId);
}
