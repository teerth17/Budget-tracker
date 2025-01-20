package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Expense;
import com.teerth.budget_app.auth.model.Income;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseDao {

    UUID addExpense(Expense expense);

    List<Expense> getAllExpenseByAccountId(UUID account_id);

    void deleteExpense(UUID expense_id);

    List<Object[]> getExpenseGroupByMonth(UUID account_id, LocalDate startDate, LocalDate endDate);
}
