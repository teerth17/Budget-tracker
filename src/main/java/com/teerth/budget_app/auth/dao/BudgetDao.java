package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Budget;

import java.util.List;
import java.util.UUID;

public interface BudgetDao {

    public UUID insertBudgetLimit(Budget budget);

    public void updateBudgetLimit(Double amount, UUID budget_id);

    public Budget retreiveBudgetWithAccountId(UUID account_id);
}
