package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Expense;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class ExpenseDaoImpl implements ExpenseDao {

    @Autowired
//    private DataSourceTransactionManager transactionManager;

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @Override
    public UUID addExpense(Expense expense){

        String SQL = "insert into expense(title,description,amount,account_id) values (?,?,?,?) returning expense_id";
        UUID account_id=  jdbcTemplate.queryForObject(SQL, new Object[]{expense.getTitle(), expense.getDescription(),expense.getAmount(),expense.getUserAccountId()},UUID.class);

        return account_id;
    }

    @Override
    public List<Expense> getAllExpenseByAccountId(UUID account_id){
        try{
            String SQL = "select expense_id,title,description,amount from expense where account_id=?";
            return jdbcTemplate.query(SQL,new Object[]{account_id}, new UserMapper());
        }catch (DataAccessException e){
            throw e;
        }
    }

    class UserMapper implements RowMapper<Expense>{
        public Expense mapRow(ResultSet rs, int rowNum) throws SQLException {
            Expense expense = new Expense();
            expense.setExpenseId(UUID.fromString(rs.getString("expense_id")));
            expense.setTitle(rs.getString("title"));
            expense.setDescription(rs.getString("description"));
            expense.setAmount(rs.getDouble("amount"));
            return expense;
        }
    }


    @Override
    public void deleteExpense(UUID expense_id){
        try {
            String SQL = "delete from expense where expense_id=?";
            jdbcTemplate.update(SQL,expense_id);
        }catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public List<Object[]> getExpenseGroupByMonth(UUID account_id, LocalDate startDate, LocalDate endDate){
        String SQL = "select to_char(date, 'YYYY-MM') as month, coalesce(sum(amount),0) from expense where account_id=? and date >= ? and date <= ? group by month order by month";

        return jdbcTemplate.query(SQL,new Object[]{account_id,startDate,endDate},(rs,rowNum) -> new Object[]{rs.getString(1),rs.getDouble(2)});
    }

}
