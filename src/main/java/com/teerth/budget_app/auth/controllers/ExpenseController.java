package com.teerth.budget_app.auth.controllers;

import com.teerth.budget_app.auth.dao.ExpenseDao;
import com.teerth.budget_app.auth.dao.UserDao;
import com.teerth.budget_app.auth.model.Category;
import com.teerth.budget_app.auth.model.Expense;
import com.teerth.budget_app.auth.model.ExpenseDTO;
import com.teerth.budget_app.auth.model.Income;
import com.teerth.budget_app.auth.services.AddExpenseService;
import com.teerth.budget_app.auth.services.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AddExpenseService addExpenseService;

    @GetMapping("/userHome/addExpense")
    public String displayAddExpense(@RequestParam("id") UUID id, Model model){

        System.out.println("Entered...");
        model.addAttribute("expense", new Expense());
        model.addAttribute("userId",id);
        List<Category> expenseCategories = categoryService.findByType("expense");
        model.addAttribute("categories",expenseCategories);
        return "Expense";
    }

    @PostMapping("userHome/addExpense")
    public String processAddExpense(@ModelAttribute("expense") Expense expense, @RequestParam(name = "id",required = false) UUID account_id, @RequestParam(name = "interval", required = false) String interval, @RequestParam("categoryId") UUID categoryId,
                                    Principal principal,
                                    Model model, HttpServletRequest request) {

        try{
            String params1=request.getParameter("id");
            System.out.println("here is the params" + params1 );
            System.out.println(principal.getName().endsWith(".com"));
            System.out.println("here is the account_id from model: " + account_id);
            String email = principal.getName();
            UUID accountId;
            if(email.endsWith(".com")){
                System.out.println("got email from principal: " + email);
                accountId = userDao.getUserAccountId(email);
            }else{
                accountId = UUID.fromString(email);
            }
            System.out.println("got this account_id from principal:" + accountId);
            System.out.println("from expense this interval: " + interval);
            System.out.println("from expense controller userId: " + accountId);

            if(accountId != null){
                expense.setUserAccountId(accountId);

                Category category = categoryService.findByd(categoryId);
                expense.setCategoryId(category.getCategory_id());

                UUID expenseId = addExpenseService.addOrUpdateExpense(expense,interval,categoryId);
                if(expenseId != null){
                    return "redirect:/userHome?id=" + accountId;
                }
            }

        }catch (Exception e){
            model.addAttribute("error message", "An error occured...");
            e.printStackTrace();
        }
        System.out.println("before going to expense page...");
        return "Expense";
    }

    @GetMapping("userHome/toggleExpenseAutomation")
    public String getToogle(){
        System.out.println("entered");
        return "hello";
    }

    @PostMapping("userHome/toggleExpenseAutomation")
    public String processToggleAutomation(@RequestParam("expenseId") String expenseIdStr, @RequestParam("automatedStatus") boolean isAutomated, Principal principal, Model model){

        System.out.println("Entered here");
        System.out.println("got id: "+expenseIdStr);
        System.out.println("auto" + isAutomated);
        UUID expenseId = UUID.fromString(expenseIdStr);

        UUID accountId = userDao.getUserAccountId(principal.getName());
        System.out.println("account: .." + accountId);

        List<Expense> incomeList = expenseDao.getExpenseByIncomeId(expenseId);
        Expense expense = incomeList.get(0);


        if(expense == null){
            throw new IllegalArgumentException("Expense not found");
        }

        System.out.println("income status: "+ expense.getAutomatedStatus());

        if(isAutomated){
            expense.setInterval(null);
            System.out.println("before stop automation");
            addExpenseService.stopAutomation(expense);
            System.out.println("Automation stopped..");
        }

        return "redirect:/userHome?id=" + accountId;
    }
}
