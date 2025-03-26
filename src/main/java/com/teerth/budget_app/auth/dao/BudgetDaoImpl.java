package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Budget;
import com.teerth.budget_app.auth.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.validation.ObjectError;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Transactional
public class BudgetDaoImpl implements BudgetDao {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;



    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @Override
    public UUID insertBudgetLimit(Budget budget){
        String SQL = "insert into budget(amount,account_id,category_id,month,year) values(?,?,?,?,?) returning budget_id";

        return jdbcTemplate.queryForObject(SQL, new Object[]{budget.getBudget_amount(),budget.getAccount_id(),budget.getCategory_id(), budget.getMonth(), budget.getYear()}, UUID.class);
    }

    @Override
    public UUID insertGeneralBudgetLimit(Budget budget){
        System.out.println("budget details inside dao: " + budget.getBudget_amount() + " " + budget.getAccount_id());
        String SQL = "insert into budget(amount,account_id,category_id,month,year) values(?,?,?,?,?) returning budget_id";

        return jdbcTemplate.queryForObject(SQL,new Object[]{budget.getBudget_amount(),budget.getAccount_id(),null,budget.getMonth(),budget.getYear()}, UUID.class);
    }

    @Override
    public void updateBudgetLimit(Double amount, UUID account_id, UUID category_id){
        String SQL;
        Object[] params;

        if (category_id == null) {
            SQL = "update budget set amount=? where account_id=? AND category_id IS NULL";
            params = new Object[]{amount, account_id};
        } else {
            SQL = "update budget set amount=? where account_id=? AND category_id=?";
            params = new Object[]{amount, account_id, category_id};
        }

        jdbcTemplate.update(SQL, params);
    }

    @Override
    public void deleteBudgetLimit(UUID budget_id, UUID category_id){
        String SQL;
        Object[] params;
        if(category_id == null ){
            System.out.println("inside null...");
            SQL = "delete from budget where budget_id=?";
            params = new Object[]{budget_id};
        }else{
            System.out.println("outside null with category: "  +category_id);
            SQL = "delete from budget where budget_id=? and category_id=?";
            params = new Object[]{budget_id,category_id};
        }

        jdbcTemplate.update(SQL,params);
    }

    @Override
    public Budget retrieveBudgetWithAccountIdAndCategory(UUID account_id, UUID category_id, int month, int year) {
        String SQL = "SELECT * FROM budget WHERE account_id = ? AND category_id = ? AND month = ? AND year = ?";

        try {
            return jdbcTemplate.queryForObject(SQL, new BudgetMapper(), account_id, category_id, month, year);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Budget> getAllBudgetsForUser(UUID account_id) {
        String SQL = "SELECT * FROM budget WHERE account_id = ?";
        return jdbcTemplate.query(SQL, new BudgetMapper(), account_id);
    }

    class BudgetMapper implements RowMapper<Budget> {
        public Budget mapRow(ResultSet rs, int rowNum) throws SQLException {
            Budget budget = new Budget();
            budget.setBudget_id(UUID.fromString(rs.getString("budget_id")));
            budget.setBudget_amount(rs.getDouble("amount"));
            budget.setAccount_id(UUID.fromString(rs.getString("account_id")));
            String categoryIdStr = rs.getString("category_id");
            if (categoryIdStr != null) {
                budget.setCategory_id(UUID.fromString(categoryIdStr));
            } else {
                budget.setCategory_id(null);  // Handle NULL category_id
            }
            budget.setMonth(rs.getInt("month"));
            budget.setYear(rs.getInt("year"));
            return budget;
        }
    }

    public Map<String, Double> getCategoryBudgetsByAccountId(UUID accountId) {
        String SQL = "SELECT category_id, amount FROM budget WHERE account_id = ?";

        return jdbcTemplate.query(SQL, new Object[]{accountId}, rs -> {
            Map<String, Double> categoryBudgets = new HashMap<>();
            while (rs.next()) {
                String categoryIdStr = rs.getString("category_id");
                Double amount = rs.getDouble("amount");

                if (categoryIdStr == null) {
                    categoryBudgets.put("General", amount);
                } else {
                    categoryBudgets.put(categoryIdStr, amount);
                }
            }
            return categoryBudgets;
        });
    }

    public Double getTotalBudgetByAccountId(UUID accountId){
        String SQL = "SELECT COALESCE(SUM(amount), 0) FROM budget WHERE account_id = ? AND category_id IS NULL";
        return jdbcTemplate.queryForObject(SQL, new Object[]{accountId}, Double.class);
    }

    @Override
    public UUID getBudgetId(UUID accountId,UUID categoryId){
        String SQL;
        Object[] params;

        if (categoryId == null) {
            SQL = "SELECT budget_id FROM budget WHERE account_id = ? AND category_id IS NULL";
            params = new Object[]{accountId};
        } else {
            SQL = "SELECT budget_id FROM budget WHERE account_id = ? AND category_id = ?";
            params = new Object[]{accountId, categoryId};        }

        try {
            return jdbcTemplate.queryForObject(SQL, UUID.class, params);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
