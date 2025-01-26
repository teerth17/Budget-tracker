package com.teerth.budget_app.auth.controllers;

import com.teerth.budget_app.auth.dao.BudgetDao;
import com.teerth.budget_app.auth.model.Budget;
import com.teerth.budget_app.auth.services.BudgetLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class BudgetController{

    @Autowired
    public BudgetLimitService budgetLimitService;

    @GetMapping("userHome/budget/setLimit")
    public String getSetLimit(Model model, @RequestParam("id") UUID id){

        model.addAttribute("userId",id);
        model.addAttribute("budget", new Budget());
        return "SetBudget";
    }

    @PostMapping("userHome/budget/setLimit")
    public String processSetLimit(@RequestParam("id") UUID id, @ModelAttribute("budget") Budget budget, Model model){
        try{
            System.out.println("id from budget: " +id);
            budget.setAccount_id(id);
            System.out.println("amount: " + budget.getBudget_amount());

            UUID budget_id = budgetLimitService.insertBudgetLimit(budget);
            if(budget_id != null){
                System.out.println("set limit successfully");
                budget.setBudget_id(budget_id);
                return "redirect:/userHome?id=" + id;
            }else {
                model.addAttribute("errorMessage", "insert limit error");
                return "SetBudget";
            }
        }catch (Exception e){
            model.addAttribute("errorMessage", "An error occured...");
            e.printStackTrace();
        }
        return "SetBudget";
    }

}