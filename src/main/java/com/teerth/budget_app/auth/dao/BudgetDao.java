package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Budget;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BudgetDao {

    public UUID insertBudgetLimit(Budget budget);

    public UUID insertGeneralBudgetLimit(Budget budget);

    public void updateBudgetLimit(Double amount, UUID budget_id, UUID category_id);

    public void deleteBudgetLimit(UUID budget_id, UUID category_id);

    public UUID getBudgetId(UUID accountId,UUID categoryId);

    public Budget retrieveBudgetWithAccountIdAndCategory(UUID account_id, UUID category_id, int month, int year);

    public List<Budget> getAllBudgetsForUser(UUID account_id);

    public Map<String, Double> getCategoryBudgetsByAccountId(UUID accountId);

    public Double getTotalBudgetByAccountId(UUID accountId);
}
