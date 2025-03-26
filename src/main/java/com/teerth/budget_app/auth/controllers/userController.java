package com.teerth.budget_app.auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teerth.budget_app.auth.dao.ExpenseDao;
import com.teerth.budget_app.auth.dao.IncomeDao;
import com.teerth.budget_app.auth.dao.UserDao;
import com.teerth.budget_app.auth.model.*;
import com.teerth.budget_app.auth.services.AddExpenseService;
import com.teerth.budget_app.auth.services.AddUpdateIncomeService;
import com.teerth.budget_app.auth.services.BudgetLimitService;
import com.teerth.budget_app.auth.services.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@Controller
//@SessionAttributes("user")
public class userController {
    @Autowired
    private UserDao userDao;

    @Autowired
    private HttpSession session;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Autowired
    private IncomeDao incomeDao;

    @Autowired
    private ExpenseDao expenseDao;

    @Autowired
    private BudgetLimitService budgetLimitService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AddUpdateIncomeService addUpdateIncomeService;

    @Autowired
    private AddExpenseService addExpenseService;

    @GetMapping("/register")
    public String displayForm(WebUser webUser, Model model){
        model.addAttribute("webUser", new WebUser());
        return "RegistrationForm";
    }

    @PostMapping("/register")
    public String processFrom(@Valid@ModelAttribute("webUser") WebUser webUser, BindingResult result,Model model, HttpServletRequest request, HttpServletResponse response){
        System.out.println("user details" + webUser.getEmail());

        if(result.hasErrors()){
            System.out.println("IN result.haserrros");
            return "RegistrationForm";
        }

        System.out.println(webUser.getPassword());
        System.out.println(webUser.getRepeatPassword());

        if(!webUser.getPassword().equals(webUser.getRepeatPassword())){
            System.out.println("Enter password and conf checking");
            model.addAttribute("errorMessage", "Password and Repeat password should match");
            return "RegistrationForm";
        }

        try{
            User existingUser = userDao.getUserByEmail(webUser.getEmail());
            if(existingUser != null) {
                System.out.println("user found" + existingUser);
                System.out.println("shceking" + existingUser.getAccount_id());

                model.addAttribute("errorMessage", "User already exists!!");
                return "RegistrationForm";
            }
        }catch (EmptyResultDataAccessException e){
            System.out.println("err: " + e);
        }

        if(webUser != null){

            System.out.println("Entered user not null");
            String encodedPswd = passwordEncoder.encode(webUser.getPassword());
            System.out.println("pswd encoded");
            webUser.setPassword(encodedPswd);
            webUser.setUser_role(webUser.getUser_role());
            System.out.println("role: " + webUser.getUser_role());
            System.out.println("encode pswd set");

            userDao.registerUser(webUser);
            System.out.println("user registered");
            UUID user_account_id = userDao.getUserAccountId(webUser.getEmail());

            if(user_account_id != null){
                User user = new User();
                user.setAccount_id(user_account_id);

                System.out.println("User id generated"  + user_account_id);
                webUser.setAccount_id(user_account_id);

                autologin(webUser, request, response);

                return "redirect:/userHome?id=" + user_account_id;
            }else{
                model.addAttribute("errorMessage","user not registered properly");
                return "RegistrationForm";
            }


//            session.setAttribute("webUser",webUser);


        }
        return "RegistrationForm";

    }


    @GetMapping("/userHome")
    public String displayHome(@RequestParam("id") UUID id,Model model) {

        System.out.println("got this id: " + id);
        String firstName = userDao.getFirstname(id);
        System.out.println("got this first name: " + firstName);
        List<IncomeDTO> allIncomeList = addUpdateIncomeService.getIncomeListWithCategoryName(id);
        List<ExpenseDTO> allExpenseList = addExpenseService.getExpenseListWithCategoryName(id);

        System.out.println("all incoems: " + allIncomeList);
        System.out.println("all expense: " + allExpenseList);

        List<IncomeDTO> lastFiveIncomeList = addUpdateIncomeService.getLastFiveIncomeByAccountId(id);
        List<ExpenseDTO> lastFiveExpenseList = addExpenseService.getLastFiveExpenseByAccountId(id);
        System.out.println("list of last 5 incomes" + lastFiveIncomeList);
        System.out.println("list of last 5expense " + lastFiveExpenseList);


        double totalIncome = 0;
        double totalExpense = 0;
        for (int i = 0; i < allIncomeList.size(); i++) {
            totalIncome += allIncomeList.get(i).getIncome().getAmount();
        }
        for (int i = 0; i <allExpenseList.size() ; i++) {
            totalExpense += allExpenseList.get(i).getExpense().getAmount();
        }

        double generalBudgetLimit = budgetLimitService.getTotalBudgetWithAccountId(id);

        Map<String,Double> categoryBudgets = budgetLimitService.getCategoryBudgetsByAccountId(id);
        Map<UUID, Double> categoryExpenses = addExpenseService.getCategoryWiseExpenses(id);

        Map<UUID,String> categoryNames = categoryService.getCategoryNames();

        System.out.println("cb: " + categoryBudgets);

        Map<String,Double> categoryExpense = new HashMap<>();
        for (ExpenseDTO expenseDTO: allExpenseList){
            System.out.println("expense category id: " + expenseDTO.getExpense().getCategoryId());

            String categoryName = "General";
            if (expenseDTO.getExpense().getCategoryId() != null) {
                Category category = categoryService.findByd(expenseDTO.getExpense().getCategoryId());
                if (category != null) {
                    categoryName = category.getName();
                }
            }

//            String category = expense.getCategory().getName();
            System.out.println("category: " + categoryName);
            categoryExpense.put(categoryName,categoryExpense.getOrDefault(categoryName,0.0) + expenseDTO.getExpense().getAmount());
        }

        System.out.println("bbbbbbbb" + generalBudgetLimit);
        model.addAttribute("categoryBudgets", categoryBudgets);
        model.addAttribute("categoryExpenses", categoryExpense);



        System.out.println("Total income: " + totalIncome);
        System.out.println("Total expense: " + totalExpense);

        if(lastFiveIncomeList == null){
            lastFiveIncomeList = new ArrayList<>();
        }
        if (lastFiveExpenseList == null){
            lastFiveExpenseList = new ArrayList<>();
            System.out.println("expense is null here");
        }

        System.out.println("general budget limit: " + generalBudgetLimit);
        System.out.println("category Budgets: " + categoryBudgets);
        System.out.println("categoryExpenses: " + categoryExpenses);

//        Gson gson = new Gson();
//        String categoryBudgetsJson = gson.toJson(categoryBudgets);
//        String categoryExpensesJson = gson.toJson(categoryExpenses);

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String categoryBudgetsJson = objectMapper.writeValueAsString(categoryBudgets);
            String categoryExpensesJson = objectMapper.writeValueAsString(categoryExpenses);
            String categoryNamesJson = objectMapper.writeValueAsString(categoryNames);

            System.out.println("json version: " + categoryBudgetsJson);
            System.out.println("json expense: " + categoryExpensesJson);
            System.out.println("json names: " + categoryNamesJson);

            model.addAttribute("budgetLimit",generalBudgetLimit);
            model.addAttribute("totalIncome",totalIncome);
            model.addAttribute("totalExpense",totalExpense);
            model.addAttribute("incomeList",lastFiveIncomeList);
            model.addAttribute("expenseList",lastFiveExpenseList);
            model.addAttribute("userId", id);
            model.addAttribute("firstName", firstName);
            model.addAttribute("categoryBudgetsJson", categoryBudgetsJson);
            model.addAttribute("categoryExpensesJson", categoryExpensesJson);
            model.addAttribute("categoryNamesJson",categoryNamesJson);
            return "userHome";
        }catch (Exception e){
            e.printStackTrace();
        }
       return "userHome";
    }

    @PostMapping("/userHome")
    public String processHome(@RequestParam("id") UUID id,
@RequestParam(value = "selectedIncome",required = false) UUID income_id,
                              @RequestParam(value = "automatedStatus", required = false) boolean automatedStatus,
                              @RequestParam(value = "selectedExpense",required = false) UUID expense_id, Model model){
        System.out.println("enterd lsnfkl");
        System.out.println("we got this id: " + income_id);
        System.out.println("expense_id " + expense_id);
        System.out.println("end...");
        System.out.println("got status from list: " + automatedStatus);

        if(income_id != null && id != null){
            List<Income> incomeAllAttributes = incomeDao.getIncomeByIncomeId(income_id);
            Income income = incomeAllAttributes.get(0);
            System.out.println("got income before delete: " + income);

            if(automatedStatus){
                System.out.println("entered with true AS...");
                addUpdateIncomeService.stopAutomation(income);
                System.out.println("stopped auto income");
            }
            System.out.println("entered to delete");
            incomeDao.deleteIncome(income_id);
            System.out.println("deleted");
            return "redirect:/userHome?id=" + id;
        }

        if(expense_id != null && id!= null){
            expenseDao.deleteExpense(expense_id);
            return "redirect:/userHome?id=" + id;
        }
        model.addAttribute("errorMessage", "id or income_id is null");
        return "redirect:/userHome?id=" + id;
    }

    @GetMapping("/userHome/income-expense-list")
    public String showIncomeExpenseList(@RequestParam("id") UUID id, Model model,
                                        Principal principal){
        System.out.println("Enter income-expense-list route");
        System.out.println("id in income-expense: " + id);

        List<IncomeDTO> incomes = addUpdateIncomeService.getIncomeListWithCategoryName(id);
        System.out.println("incomes from income-expense: " + incomes.size());
        List<ExpenseDTO> expenses = addExpenseService.getExpenseListWithCategoryName(id);
        System.out.println("expesnes from income-expense: " + expenses.size());
        if(incomes == null){
            incomes = new ArrayList<>();
        }
        if(expenses == null){
            expenses = new ArrayList<>();
            System.out.println("entered in expense null");
        }
        System.out.println("list income: " + incomes);
        System.out.println("list expesnes: " + expenses);

        model.addAttribute("userId",id);
        model.addAttribute("incomes",incomes);
        model.addAttribute("expenses",expenses);

        return "income-expense-list";

    }


//    @GetMapping("/login")
//    public String displayLogin(User user, Model model){
//        model.addAttribute("user", new User());
//        return "LoginForm";
//    }
//    @PostMapping("/login")
//    public String processLogin(User user){
//        System.out.println("User: "+ user.getEmail());
//        System.out.println("User:"+ user.getPassword());
//
//
//
//        if(user != null){
//            User validateUser = userDao.getUserByEmail(user.getEmail());
//            System.out.println("validator: " + validateUser.getEmail());
//            System.out.println("validator" + validateUser.getPassword());
//
//
//            if(validateUser.getEmail().equals(user.getEmail()) && validateUser.getPassword().contains(user.getPassword())){
//                System.out.println("entered...");
//                UUID user_account = userDao.getUserAccountId(user.getEmail());
//                user.setAccountId(user_account);
//                session.setAttribute("user",user);
//                System.out.println("user_id " + user.getAccountId());
//
//                return "userHome";
//            }
//            return "LoginForm";
//        }
//        return "LoginForm";
//    }

    private void autologin(WebUser webUser, HttpServletRequest request,
                           HttpServletResponse response) {
        Collection<GrantedAuthority> authorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList(String.join(", ", webUser.getUser_role()));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                webUser.getAccount_id(), null, authorities);
        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
                .getContextHolderStrategy();

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(auth);
        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, request, response);
    }
}
