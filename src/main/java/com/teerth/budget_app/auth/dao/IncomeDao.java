package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Income;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IncomeDao {

    UUID addIncome(Income income, UUID categoryId);

    List<Income> getAllIncomeByAccountId(UUID account_id);

    List<Income> getLastFiveIncomeByAccountId(UUID account_id);

    void deleteIncome(UUID income_id);

    List<Income> getIncomeByTitleAndAccountID(String title,UUID account_id);

    List<Income> getIncomeByIncomeId(UUID income_id);

    void updateIncomeAmountByTitleAndAccountId(String title,double amount,UUID account_id);

    void updateIncomeStatusAndIntervalById(boolean status,String interval, UUID income_id);

    List<Income> getAutomatedIncomes();

    List<Object[]> getIncomeGroupByMonth(UUID account_id, LocalDate startDate, LocalDate endDate);

    UUID getCategoryByIncomeId(UUID income_id);


}
