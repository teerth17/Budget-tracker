package com.teerth.budget_app.auth.dao;

import com.teerth.budget_app.auth.model.User;
import com.teerth.budget_app.auth.model.WebUser;

import java.util.UUID;

public interface UserDao {

    void registerUser(WebUser webUser);

    User getUserByEmail(String email);

    UUID getUserAccountId(String email);

    User getUserByAccountId(UUID account_id);

    public String getFirstname(UUID account_id);

}
