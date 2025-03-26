package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Transactional
public class CategoryDaoImpl implements CategoryDao {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

//    public List<Category> getCategoryByType(String category_type){
//        String SQL = "select * from category where category_type=?";
//        return jdbcTemplate.query(SQL,new Object[]{category_type}, new CategoryMapper());
//    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    public UUID getCategoryIdByNameAndType(String name,String type){
        String SQL = "select category_id from category where name=? and type=?";

        return jdbcTemplate.queryForObject(SQL,new Object[]{name,type}, UUID.class);
    }

    @Override
    public List<Category> findByType(String type){
        String SQL = "select category_id, name, type from category where type=?";
        return jdbcTemplate.query(SQL,new Object[]{type},new CategoryMapper());
    }

    @Override
    public Category findById(UUID id){
        if(id == null){
            return  null;
        }
        String SQL = "select category_id, name, type from category where category_id=?";
        return jdbcTemplate.queryForObject(SQL,new Object[]{id,},new CategoryMapper());
    }

    @Override
    public Map<UUID, String> getAllCategoryNames() {
        String SQL = "SELECT category_id, name FROM category";

        return jdbcTemplate.query(SQL, rs -> {
            Map<UUID, String> categoryMap = new HashMap<>();
            while (rs.next()) {
                categoryMap.put(UUID.fromString(rs.getString("category_id")), rs.getString("name"));
            }
            return categoryMap;
        });
    }



    class CategoryMapper implements RowMapper<Category>{
        public Category mapRow(ResultSet rs,int rowNum) throws SQLException {
            Category category = new Category();
            category.setCategory_id(UUID.fromString(rs.getString("category_id")));
            category.setCategory_type(rs.getString("type"));
            category.setName(rs.getString("name"));
            return category;
        }
    }
}
