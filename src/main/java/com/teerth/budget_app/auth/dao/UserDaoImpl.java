package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.User;
import com.teerth.budget_app.auth.model.WebUser;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;
import javax.xml.transform.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Repository
@Transactional
public class UserDaoImpl implements UserDao {

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
    public void registerUser(WebUser webUser){
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
//        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            String SQL = "insert into account(email,password) " + " values (?,?) returning account_id";
            UUID account_id=  jdbcTemplate.queryForObject(SQL, new Object[]{webUser.getEmail(), webUser.getPassword()}, UUID.class);

            SQL = "insert into profile(account_id, firstName,lastName) " + " values (?,?,?)";
            jdbcTemplate.update(SQL,account_id, webUser.getFirstName(),webUser.getLastName());

            SQL = "insert into authority(account_id, role) " + " values (?,?)";
            jdbcTemplate.update(SQL,account_id, webUser.getUser_role());

//            transactionManager.commit(status);
        }catch(DataAccessException e){
            System.out.println("Error in creating user account");
//            transactionManager.rollback(status);
            throw e;
        }
    }

    @Override
    public UUID getUserAccountId(String email){

        try{
            String SQL = "select account_id from account " + "where email = ?";
            UUID account_id = jdbcTemplate.queryForObject(SQL,new Object[]{email},UUID.class);
            return account_id;
        }catch (DataAccessException e) {
            // Handle exception (e.g., return null, log the error, or rethrow the exception)
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email){
        String SQL = "select a.account_id,a.email from account a " + "where a.email = ?";
        User user = jdbcTemplate.queryForObject(SQL,new UserMapper(),email);
        return user;
    }

    class UserMapper implements  RowMapper<User>{
        public User mapRow(ResultSet rs,int rowNum) throws SQLException{
            User user = new User();
            user.setAccount_id(UUID.fromString(rs.getString("account_id")));
            user.setEmail(rs.getString("email"));
            return user;
        }
    }

    @Override
    public User getUserByAccountId(UUID account_id){
        String SQL = "select distinct p.firstname,p.lastname,a.email,au.role from profile p join " +
                "account a on p.account_id=a.account_id join authority au on a.account_id=au.account_id " +
                "where a.account_id=?";
        User user = jdbcTemplate.queryForObject(SQL,new getUserMapper(),account_id);
        return user;

    }

    class getUserMapper implements RowMapper<User>{
        public User mapRow(ResultSet rs,int rowNum) throws SQLException{
            User user  = new User();
            user.setFirstName(rs.getString("firstname"));
            user.setLastName(rs.getString("lastname"));
            user.setEmail(rs.getString("email"));
            user.setUserRole(rs.getString("role"));

            return user;
        }
    }

}
