package com.teerth.budget_app.auth.model;

import org.springframework.lang.NonNull;

import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class WebUser {

    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotEmpty(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
    private String firstName;

    @NotEmpty(message = "Password is requried")
    @Size(min = 4, message = "Password should be atleast 4 characters")
    private String password;

    @NotEmpty(message = "Can't be empty")
    private String repeatPassword;

    @NotEmpty(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name must contain only letters")
    private String lastName;

    private UUID account_id;

    private String user_role = "ROLE_USER";


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setRepeatPassword(String password) {
        this.repeatPassword = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public UUID getAccount_id(){
        return account_id;
    }

    public void setAccount_id(UUID account_id){
        this.account_id = account_id;
    }

    public String getUser_role(){
        return user_role;
    }

    public void setUser_role(String user_role){
        this.user_role = user_role;
    }



}
