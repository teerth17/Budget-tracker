package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Expense;
import com.teerth.budget_app.auth.model.Income;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public UUID addExpense(Expense expense, UUID categoryId){

        String SQL = "insert into expense(title,description,amount,account_id,category_id) values (?,?,?,?,?) returning expense_id";
        UUID account_id=  jdbcTemplate.queryForObject(SQL, new Object[]{expense.getTitle(), expense.getDescription(),expense.getAmount(),expense.getUserAccountId(),categoryId},UUID.class);

        return account_id;
    }

    @Override
    public List<Expense> getAllExpenseByAccountId(UUID account_id){
        try{
            String SQL = "select expense_id,title,description,amount,date,automated,interval,category_id from expense where account_id=?";
            return jdbcTemplate.query(SQL,new Object[]{account_id}, new UserMapper());
        }catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public List<Expense> getLastFiveExpenseByAccountId(UUID account_id){
        try {
            String SQL = "select expense_id,title,description,amount,date,automated,interval,category_id from expense where account_id=? " +
                    "order by date desc limit 5";
            return jdbcTemplate.query(SQL,new Object[]{account_id},new UserMapper());
        }
        catch (DataAccessException e){
            throw  e;
        }
    }

    class UserMapper implements RowMapper<Expense>{
        public Expense mapRow(ResultSet rs, int rowNum) throws SQLException {
            Expense expense = new Expense();
            expense.setExpenseId(UUID.fromString(rs.getString("expense_id")));
            expense.setTitle(rs.getString("title"));
            expense.setDescription(rs.getString("description"));
            expense.setAmount(rs.getDouble("amount"));
            expense.setDate(rs.getString("date"));
            expense.setAutomatedStatus(rs.getBoolean("automated"));
            expense.setInterval(rs.getString("interval"));
            expense.setCategoryId(UUID.fromString(rs.getString("category_id")));
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

    @Override
    public void updateExpenseAmountByTitleAndAccountId(String title,double amount,UUID account_id){
        try {
            String SQL = "update expense set amount=? where title=? and account_id=?";
            jdbcTemplate.update(SQL,amount,title,account_id);
        }
        catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public List<Expense> getExpenseByTitleAndAccountId(String title,UUID account_id){
        try {
            String SQL = "select * from expense where title=? and account_id=?";
            return jdbcTemplate.query(SQL,new Object[]{title,account_id},new UserMapper());

        }
        catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public List<Expense> getExpenseByIncomeId(UUID expense_id){
        try{
            String SQL = "select expense_id,title,description,amount,date,automated,interval,category_id from expense where expense_id=?";
            return jdbcTemplate.query(SQL,new Object[]{expense_id}, new UserMapper());
        }catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public List<Expense> getAutomatedExpense(){
        String SQL = "select * from expense where automated=true";
        return jdbcTemplate.query(SQL,new UserMapper());
    }

    @Override
    public void updateExpenseStatusAndIntervalById(boolean status,String interval, UUID expense_id){
        try {
            String SQL = "update expense set automated=?,interval=? where expense_id=?";
            jdbcTemplate.update(SQL,status,interval,expense_id);
        }
        catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public List<Map<String,Object>> getAllExpenseCategoriesWithTotalExpense(){
        String SQL = "select c.name as category, sum(e.amount) as total_spent from expense e "+
                "join category c on e.category_id=c.category_id group by c.name order by total_spent desc";
        return jdbcTemplate.queryForList(SQL);
    }

    @Override
    public Map<UUID, Double> getCategoryExpensesByAccountId(UUID accountId) {
        String SQL = "SELECT category_id, COALESCE(SUM(amount), 0) as total_expense " +
                "FROM expense WHERE account_id = ? GROUP BY category_id";

        return jdbcTemplate.query(SQL, new Object[]{accountId}, rs -> {
            Map<UUID, Double> categoryExpenses = new HashMap<>();
            while (rs.next()) {
                UUID categoryId = (UUID) rs.getObject("category_id");
                Double totalExpense = rs.getDouble("total_expense");
                categoryExpenses.put(categoryId, totalExpense);
            }
            return categoryExpenses;
        });
    }


}
