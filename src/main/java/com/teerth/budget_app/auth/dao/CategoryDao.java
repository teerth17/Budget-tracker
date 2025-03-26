package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.Category;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CategoryDao {

//    public UUID insertCategory(String name,String type);

//    public UUID getCategoryIdByNameAndType(String name,String type);

    public List<Category> findByType(String type);

    public Category findById(UUID id);

    public Map<UUID, String> getAllCategoryNames();



}
