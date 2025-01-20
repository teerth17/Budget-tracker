package com.teerth.budget_app.auth.model;

import java.util.UUID;

public class User {
    private UUID account_id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;


    public UUID getAccount_id(){
        return account_id;
    }

    public void setAccount_id(UUID account_id){
        this.account_id = account_id;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }


    public void setLastName(String lastName){
        this.lastName= lastName;
    }
    public String getLastName(){
        return lastName;
    }

    public String getFirstName(){
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUserRole(){
        return role;
    }
    public void setUserRole(String role){
        this.role = role;
    }

    
}
