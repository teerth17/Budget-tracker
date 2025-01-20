package com.teerth.budget_app.auth.controllers;

import com.teerth.budget_app.auth.dao.ExpenseDao;
import com.teerth.budget_app.auth.dao.UserDao;
import com.teerth.budget_app.auth.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private UserDao userDao;

    @GetMapping("/userHome/addExpense")
    public String displayAddExpense(@RequestParam("id") UUID id, Model model){

        System.out.println("Entered...");
        model.addAttribute("expense", new Expense());
        model.addAttribute("userId",id);
        return "Expense";
    }

    @PostMapping("userHome/addExpense")
    public String processAddExpense(@ModelAttribute("expense") Expense expense, Principal principal, Model model) {

        if(expense != null){
            System.out.println("Didn't get id from params..");
            String email = principal.getName();
            UUID account_id = userDao.getUserAccountId(email);

            if(account_id != null){
                System.out.println("Id not null..");
                expense.setUserAccountId(account_id);
                System.out.println("account_id updated..");

                UUID expense_id = expenseDao.addExpense(expense);
                if(expense_id != null){
                    expense.setExpenseId(expense_id);
                    System.out.println("got expense_id... and expense is added");
                    return "redirect:/userHome?id=" + account_id;
                }
                else{
                    System.out.println("expense_id is null");
                }
            }
        }
        model.addAttribute("errorMessage", "some error ocurred...");
        return "Expense";
    }
}
