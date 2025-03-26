package com.teerth.budget_app.auth.controllers;

import com.teerth.budget_app.auth.model.WebUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "LoginForm";
    }


    @GetMapping("/")
    public String index() {
        return "index";
    }
}
