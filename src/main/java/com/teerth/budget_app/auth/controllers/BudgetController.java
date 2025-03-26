package com.teerth.budget_app.auth.controllers;

import com.teerth.budget_app.auth.dao.BudgetDao;
import com.teerth.budget_app.auth.model.Budget;
import com.teerth.budget_app.auth.services.BudgetLimitService;
import com.teerth.budget_app.auth.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.UUID;

@Controller
public class BudgetController{

    @Autowired
    public BudgetLimitService budgetLimitService;

    @Autowired
    public CategoryService categoryService;

    @GetMapping("userHome/budget/setLimit")
    public String getSetLimit(Model model, @RequestParam("id") UUID id){

        model.addAttribute("userId",id);
        model.addAttribute("budget", new Budget());

        model.addAttribute("categories", categoryService.findByType("expense"));

        return "SetBudget";
    }

    @PostMapping("userHome/budget/setLimit")
    public String processSetLimit(@RequestParam("id") UUID id,
                                  @ModelAttribute("budget") Budget budget,
                                  @RequestParam(name="category_id",required = false) UUID categoryId,
                                  @RequestParam("action") String action,Model model){
        try{
            System.out.println("got this actuon: " + action);
            System.out.println("id from budget: " +id);
            System.out.println("got this category_id: " + categoryId);
            budget.setAccount_id(id);
            budget.setCategory_id(categoryId);
            budget.setMonth(LocalDate.now().getMonthValue());
            budget.setYear(LocalDate.now().getYear());
            System.out.println("amount: " + budget.getBudget_amount());

            UUID budget_id;

            if("add".equals(action)){

                budget_id = budgetLimitService.getBudgetId(budget.getAccount_id(),budget.getCategory_id());

                if(budget_id == null){
                    if(categoryId == null){
                        System.out.println("add entered in null..");
                        budget_id = budgetLimitService.insertGeneralBudgetLimit(budget);
                    }else {
                        budget_id = budgetLimitService.insertBudgetLimit(budget);
                    }
                    if(budget_id != null){
                        System.out.println("set limit successfully");
                        budget.setBudget_id(budget_id);
                        return "redirect:/userHome?id=" + id;
                    }else {
                        model.addAttribute("errorMessage", "insert limit error");
                        return "SetBudget";
                    }
                }else{
                    model.addAttribute("errorMessage", "Limit already added ones");
                    return "SetBudget";
                }

            }

            if("update".equals(action)){
                UUID budgetId = budgetLimitService.getBudgetId(budget.getAccount_id(),budget.getCategory_id());
                if(budgetId != null){
                    System.out.println("before updating budget...");
                    budgetLimitService.updateBudgetLimit(budget);
                    System.out.println("budget updated....");
                }else{
                    model.addAttribute("errorMessage", "budget not exist..");
                    return "SetBudget";
                }

            }

            if("delete".equals(action)){
                UUID budgetId = budgetLimitService.getBudgetId(budget.getAccount_id(),budget.getCategory_id());
                budget.setBudget_id(budgetId);
                System.out.println("inside delete: " + budgetId);
                if(budgetId != null){;
                    System.out.println("before deleting budget....");
                    budgetLimitService.deleteBudgetLimit(budget);
                    System.out.println("budget deleted....");
                }else{
                    model.addAttribute("errorMessage", "budget not exist..");
                    return "SetBudget";
                }

            }

        }catch (Exception e){
            model.addAttribute("errorMessage", "An error occured...");
            e.printStackTrace();
        }
        return "SetBudget";
    }

}