package com.teerth.budget_app.auth.services;

import com.teerth.budget_app.auth.dao.CategoryDao;
import com.teerth.budget_app.auth.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    public List<Category> findByType(String type){

        return categoryDao.findByType(type);
    }

    public Category findByd(UUID id){
        return categoryDao.findById(id);
    }

    public Map<UUID, String> getCategoryNames() {
        return categoryDao.getAllCategoryNames();
    }

}
