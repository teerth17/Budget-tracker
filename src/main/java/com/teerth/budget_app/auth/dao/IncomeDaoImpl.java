package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.dao.IncomeDao;
import com.teerth.budget_app.auth.model.Income;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;
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
public class IncomeDaoImpl implements IncomeDao {

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
    public UUID addIncome(Income income){
        String SQL = "insert into income(title,description,amount,account_id) values (?,?,?,?) returning income_id";
        UUID account_id=  jdbcTemplate.queryForObject(SQL, new Object[]{income.getTitle(), income.getDescription(),income.getAmount(),income.getUserAccountId()},UUID.class);

        return account_id;
    }



    @Override
    public List<Income> getAllIncomeByAccountId(UUID account_id){
        try{
            String SQL = "select income_id,title,description,amount,date,automated,interval from income where account_id=?";
            return jdbcTemplate.query(SQL,new Object[]{account_id}, new UserMapper());
        }catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public List<Income> getIncomeByIncomeId(UUID income_id){
        try{
            String SQL = "select income_id,title,description,amount,date,automated,interval from income where income_id=?";
            return jdbcTemplate.query(SQL,new Object[]{income_id}, new UserMapper());
        }catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public List<Income> getAutomatedIncomes(){
        String SQL = "select * from income where automated=true";
        return jdbcTemplate.query(SQL,new UserMapper());
    }

    @Override
    public void updateIncomeStatusAndIntervalById(boolean status,String interval, UUID income_id){
        try {
            String SQL = "update income set automated=?,interval=? where income_id=?";
            jdbcTemplate.update(SQL,status,interval,income_id);
        }
        catch (DataAccessException e){
            throw e;
        }
    }

    class UserMapper implements RowMapper<Income>{
        public Income mapRow(ResultSet rs, int rowNum) throws SQLException {
            Income income = new Income();
            income.setIncome_id(UUID.fromString(rs.getString("income_id")));
            income.setTitle(rs.getString("title"));
            income.setDescription(rs.getString("description"));
            income.setAmount(rs.getDouble("amount"));
            income.setDate(rs.getString("date"));
            income.setAutomatedStatus(rs.getBoolean("automated"));
            income.setInterval(rs.getString("interval"));
            return income;
        }
    }

    @Override
    public void deleteIncome(UUID income_id){
        try {
            String SQL = "delete from income where income_id=?";
            jdbcTemplate.update(SQL,income_id);
        }catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public List<Income> getIncomeByTitle(String title){
        try {
            String SQL = "select * from income where title=?";
            return jdbcTemplate.query(SQL,new Object[]{title},new UserMapper());

        }
        catch (DataAccessException e){
            throw e;
        }
    }

    @Override
    public void updateIncomeAmountByTitle(String title,double amount){
        try {
            String SQL = "update income set amount=? where title=?";
            jdbcTemplate.update(SQL,amount,title);
        }
        catch (DataAccessException e){
            throw e;
        }
    }

    // get all Income(monthly) of sum  and month sorted for userid
    @Override
    public List<Object[]> getIncomeGroupByMonth(UUID account_id, LocalDate startDate, LocalDate endDate){
        String SQL = "select to_char(date, 'YYYY-MM') as month, coalesce(sum(amount),0) from income where account_id=? and date >= ? and date <= ? group by month order by month";

        return jdbcTemplate.query(SQL,new Object[]{account_id,startDate,endDate},(rs,rowNum) -> new Object[]{rs.getString(1),rs.getDouble(2)});
    }

}
