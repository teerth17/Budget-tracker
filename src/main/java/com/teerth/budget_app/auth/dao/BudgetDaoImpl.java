package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Budget;
import com.teerth.budget_app.auth.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.validation.ObjectError;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
        String SQL = "insert into budget(amount,account_id) values(?,?) returning budget_id";

        return jdbcTemplate.queryForObject(SQL, new Object[]{budget.getBudget_amount(),budget.getAccount_id()}, UUID.class);
    }

    @Override
    public void updateBudgetLimit(Double amount, UUID account_id){
        String SQL = "update budget set amount=? where account_id=?";
        jdbcTemplate.update(SQL,amount,account_id);
    }

    @Override
    public Budget retreiveBudgetWithAccountId(UUID account_id){
        String SQL = "select budget_id,amount from budget where account_id = ?";

        Budget budget =  jdbcTemplate.queryForObject(SQL,new BudgetMapper(),account_id);
        return budget;
    }

    class BudgetMapper implements RowMapper<Budget> {
        public Budget mapRow(ResultSet rs, int rowNum) throws SQLException {
            Budget budget= new Budget();
            budget.setBudget_id(UUID.fromString(rs.getString("budget_id")));
            budget.setBudget_amount(rs.getDouble("amount"));
            return budget;
        }
    }

}
