package com.teerth.budget_app.auth.model;

import java.util.UUID;

public class Category {

    private UUID category_id;
    private String name;
    private String category_type;

    public UUID getCategory_id(){
        return category_id;
    }
    public void setCategory_id(UUID category_id){
        this.category_id = category_id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name= name;
    }

    public String getCategory_type(){
        return category_type;
    }
    public void setCategory_type(String category_type){
        this.category_type = category_type;
    }

    public boolean isGeneralCategory() {
        return this.category_id == null;
    }
}
