package com.teerth.budget_app.auth.controllers;

import com.teerth.budget_app.auth.dao.IncomeDao;
import com.teerth.budget_app.auth.dao.UserDao;
import com.teerth.budget_app.auth.model.Income;
import com.teerth.budget_app.auth.services.AddUpdateIncomeService;
import com.teerth.budget_app.auth.services.IncomeSchedulerService;
import com.teerth.budget_app.auth.services.MonthlyIncomeExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

@Controller
public class IncomeController {

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private IncomeSchedulerService incomeSchedulerService;

    @Autowired
    private AddUpdateIncomeService addUpdateIncomeService;

    @Autowired
    private MonthlyIncomeExpenseService monthlyIncomeExpenseService;

    @Autowired
    private UserDao userDao;

    @GetMapping("/userHome/addIncome")
    public String displayAddIncome(@RequestParam("id") UUID id, Model model){

        System.out.println("Entered...");
        model.addAttribute("income", new Income());
        model.addAttribute("userId",id);
        return "Income";
    }

//    @PostMapping("userHome/addIncome")
//    public String processAddIncome(@ModelAttribute("income") Income income, @RequestParam(name = "interval", required = false) String interval,Principal principal, Model model) {
//        if(income != null){
//            System.out.println("Didn't get id from params..");
//            String email = principal.getName();
//            UUID account_id = userDao.getUserAccountId(email);
//            System.out.println("got interval: " + interval);
//            income.setTitle(income.getTitle().trim().toLowerCase());
//
//            if(account_id != null){
//                System.out.println("Id not null..");
//                income.setUserAccountId(account_id);
//                System.out.println("account_id updated..");
//
//                List<Income> existingIncomes = incomeDao.getIncomeByTitle(income.getTitle());
//
//                if(income.getInterval() != null){
//
//                    Income incomeAtzero = incomeDao.getIncomeByTitle(income.getTitle()).get(0);
//                    String incomeTitle = incomeAtzero.getTitle().trim().toLowerCase();
//                    double currentAmount = incomeAtzero.getAmount();
//                    double newAmount = income.getAmount();
//
//                    System.out.println("current amt: " + currentAmount);
//                    System.out.println("newAmount: " + newAmount);
//
//
//                    String enterdTtile = income.getTitle().toLowerCase();
//                    System.out.println("income at zer: "+ incomeAtzero);
//                    System.out.println("title" + enterdTtile);
//                    System.out.println("incomeTitle" + incomeTitle);
//
//
//                        if(incomeTitle.equals(enterdTtile)){
//                            System.out.println("criteria of same title enterd: ");
//                            Runnable task = () -> {
//                                double updatedAmount = currentAmount + newAmount;
//                                System.out.println("updated one: " + updatedAmount);
////                                incomeDao.addIncome(income);
//                                incomeDao.updateIncomeAmountByTitle(incomeTitle,updatedAmount);
//                            };
//                            incomeSchedulerService.schedulerIncomeAddition(income,task);
//                        }
//
//
//                }
//                UUID income_id = incomeDao.addIncome(income);
//                if(income_id != null){
//                    income.setIncome_id(income_id);
//                    System.out.println("got income_id... and income is added");
//                    return "redirect:/userHome?id=" + account_id;
//                }
//                else{
//                    System.out.println("income_id is null");
//                }
//            }
//        }
//        model.addAttribute("errorMessage", "some error ocurred...");
//        return "Income";
//    }

    @PostMapping("userHome/addIncome")
    public String processAddIncome(
            @ModelAttribute("income") Income income,
            @RequestParam(name = "interval", required = false) String interval,
            Principal principal,
            Model model) {

        try{
            String email = principal.getName();
            UUID accountId = userDao.getUserAccountId(email);

            if(accountId != null){
                income.setUserAccountId(accountId);
                UUID incomeId = addUpdateIncomeService.addOrUpdateIncome(income,interval);
                if(incomeId != null){
                    return "redirect:/userHome?id=" + accountId;
                }
            }

        }catch (Exception e){
            model.addAttribute("error message", "An error occured...");
            e.printStackTrace();
        }
        return "Income";
    }

    @GetMapping("userHome/toggleAutomation")
    public String getToogle(){
        System.out.println("entered");
        return "hello";
    }

    @PostMapping("userHome/toggleAutomation")
    public String processToggleAutomation(@RequestParam("incomeId") String incomeIdStr, @RequestParam("isAutomated") boolean isAutomated, @RequestParam(name = "interval", required = false) String interval, Principal principal, Model model){

        System.out.println("Entered here");
        System.out.println("got id: "+incomeIdStr);
        System.out.println("auto" + isAutomated);
        UUID incomeId = UUID.fromString(incomeIdStr);

        UUID accountId = userDao.getUserAccountId(principal.getName());
        System.out.println("account: .." + accountId);

        List<Income> incomeList = incomeDao.getIncomeByIncomeId(incomeId);
        Income income = incomeList.get(0);

        if(income == null){
            throw new IllegalArgumentException("Income not found");
        }

        System.out.println("income status: "+ income.getAutomatedStatus());
        income.setAutomatedStatus(!isAutomated);

//        if (!isAutomated) {
//            // Enable automation with interval
//            if (interval != null && !interval.isEmpty()) {
//                income.setInterval(interval);
//            }
//            addUpdateIncomeService.addOrUpdateIncome(income, interval);
//        } else {
//            // Disable automation
//            income.setInterval(null); // Clear interval
//            addUpdateIncomeService.stopAutomation(income); // Call a dedicated method to stop automation
//        }

        if(income.getAutomatedStatus()){
            if (interval != null && !interval.isEmpty()) {
                System.out.println("interval.. " + interval);
                income.setInterval(interval);
            }
            System.out.println("interval got: " + interval);
            System.out.println("got ");
            addUpdateIncomeService.addOrUpdateIncome(income, interval);

        }else{
            System.out.println("in else");
            income.setInterval(null); // Clear interval
            addUpdateIncomeService.stopAutomation(income);
        }

        return "redirect:/userHome?id=" + accountId;
    }

    @GetMapping("userHome/income-expense-trend-data")
    @ResponseBody
    public List<List<Object>> getGraphRepresentation(@RequestParam("id") UUID id,@RequestParam("range") String range, Model model){
        List<List<Object>> chartData = new ArrayList<>();

        chartData.add(Arrays.asList("Time Period","Income","Expense"));

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();

        switch (range){
            case "30days":
                startDate = endDate.minusDays(30);
                endDate = endDate.plusDays(1);
                break;
            case "month" :
                startDate = LocalDate.of(endDate.getYear(),endDate.getMonth(),1);
                endDate = endDate.plusDays(1);
                break;
            case "lastMonth":
                startDate = endDate.minusMonths(1).withDayOfMonth(1);
                endDate = startDate.plusMonths(1);
            case "year":
                startDate = LocalDate.of(endDate.getYear(),1,1);
                endDate = endDate.plusDays(1);
                break;
        }

        System.out.println("start date; " + startDate);
        System.out.println("end date: " + endDate);

        List<Object[]> incomeData = monthlyIncomeExpenseService.getMonthlyIncome(id,startDate,endDate);
        List<Object[]> expenseData = monthlyIncomeExpenseService.getMonthlyExpense(id,startDate,endDate);

        double totalIncome = incomeData.stream().mapToDouble(obj -> ((Number) obj[1]).doubleValue()).sum();
        double totalExpense = expenseData.stream().mapToDouble(obj -> ((Number) obj[1]).doubleValue()).sum();

        System.out.println("income total: " + totalIncome);
        System.out.println("expense total" + totalExpense);

        chartData.add(Arrays.asList(startDate.toString(), 0.0, 0.0));  // Start from 0
        chartData.add(Arrays.asList(endDate.toString(), totalIncome, totalExpense));
        model.addAttribute("userId", id);
        return chartData;
    }

    @GetMapping("userHome/income-expense-trend")
    public String showGraph(@RequestParam("id") UUID id, Model model){
        model.addAttribute("userId",id);
        return "IncomeExpenseGraph";
    }

}

// post userHome/addIncome

//if (income != null) {
//String email = principal.getName();
//UUID account_id = userDao.getUserAccountId(email);
//            income.setTitle(income.getTitle().trim().toLowerCase());
//        income.setInterval(interval);
//
//            if (account_id != null) {
//        income.setUserAccountId(account_id);
//
//// Check if income with the same title exists
//List<Income> existingIncomes = incomeDao.getIncomeByTitle(income.getTitle());
//                if (existingIncomes != null && !existingIncomes.isEmpty()) {
//Income existingIncome = existingIncomes.get(0); // Assume the first match
//String existingIncomeInterval = existingIncome.getInterval();
//                    System.out.println("existing interval "+ existingIncomeInterval);
//
//double currentAmount = existingIncome.getAmount();
//double newAmount = income.getAmount();
//double updatedAmount = currentAmount + newAmount;
//
//                    System.out.println("Updating existing income: " + existingIncome.getTitle());
//        System.out.println("Current amount: " + currentAmount + ", Adding: " + newAmount + ", Updated: " + updatedAmount);
//
//// Update existing income amount
//                    incomeDao.updateIncomeAmountByTitle(existingIncome.getTitle(), updatedAmount);
//
//        // Schedule recurring updates if interval is provided
//        if (interval != null && !interval.isEmpty()) {
//        System.out.println("income-id cehck: " + existingIncome.getIncome_id());
//        incomeDao.updateIncomeStatusAndIntervalById(true,interval,existingIncome.getIncome_id());
//        income.setAutomatedStatus(true);
//                        System.out.println("interval" + income.getAutomatedStatus());
//Runnable task = () -> {
//    double updatedRecurringAmount = incomeDao.getIncomeByTitle(existingIncome.getTitle()).get(0).getAmount() + newAmount;
//    incomeDao.updateIncomeAmountByTitle(existingIncome.getTitle(), updatedRecurringAmount);
//};
//                        incomeSchedulerService.schedulerIncomeAddition(income, task);
//                    }
//
//                            // Redirect to home after update
//                            return "redirect:/userHome?id=" + account_id;
//                }
//
//// Add new income if no existing entry is found
//UUID income_id = incomeDao.addIncome(income);
//List<Income> newIncome=  incomeDao.getIncomeByIncomeId(income_id);
//                income.setDate(newIncome.get(0).getDate());
//
//
//        System.out.println("date: " + income.getDate());
//
//        // Schedule recurring additions if interval is provided
//        if (interval != null && !interval.isEmpty()) {
//        System.out.println("income-id cehck: " + income_id);
//                    incomeDao.updateIncomeStatusAndIntervalById(true,interval,income_id);
//                    income.setAutomatedStatus(true);
//                    System.out.println("status" + income.getAutomatedStatus());
//Runnable task = () -> {
//    double updatedRecurringAmount = incomeDao.getIncomeByTitle(income.getTitle()).get(0).getAmount() + income.getAmount();
//    incomeDao.updateIncomeAmountByTitle(income.getTitle(), updatedRecurringAmount);
//};
//                    incomeSchedulerService.schedulerIncomeAddition(income, task);
//                }
//
//                        if (income_id != null) {
//        income.setIncome_id(income_id);
//                    return "redirect:/userHome?id=" + account_id;
//                }
//                        }
//                        }
//
//                        // Error scenario
//                        model.addAttribute("errorMessage", "An error occurred while adding income.");
//        return "Income";
//                }