package com.teerth.budget_app.auth.controllers;

import com.teerth.budget_app.auth.dao.ExpenseDao;
import com.teerth.budget_app.auth.dao.IncomeDao;
import com.teerth.budget_app.auth.dao.UserDao;
import com.teerth.budget_app.auth.model.Expense;
import com.teerth.budget_app.auth.model.Income;
import com.teerth.budget_app.auth.model.User;
import com.teerth.budget_app.auth.model.WebUser;
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

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Controller
@SessionAttributes("user")
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



    @GetMapping("/register")
    public String displayForm(WebUser webUser, Model model){
        model.addAttribute("webUser", new WebUser());
        return "RegistrationForm";
    }

    @PostMapping("/register")
    public String processFrom(@Valid@ModelAttribute("webUser") WebUser webUser, BindingResult result,Model model, HttpServletRequest request,
                              HttpServletResponse response){
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

        List<Income> incomeList = incomeDao.getAllIncomeByAccountId(id);
        List<Expense> expenseList = expenseDao.getAllExpenseByAccountId(id);
        System.out.println("list of incomes" + incomeList);
        System.out.println("list of expense " + expenseList);


        double totalIncome = 0;
        double totalExpense = 0;
        for (int i = 0; i < incomeList.size(); i++) {
            totalIncome += incomeList.get(i).getAmount();
        }
        for (int i = 0; i <expenseList.size() ; i++) {
            totalExpense += expenseList.get(i).getAmount();
        }

        System.out.println("Total income: " + totalIncome);
        System.out.println("Total expense: " + totalExpense);

        model.addAttribute("totalIncome",totalIncome);
        model.addAttribute("totalExpense",totalExpense);
        model.addAttribute("incomeList",incomeList);
        model.addAttribute("expenseList",expenseList);
        model.addAttribute("userId", id);
        return "userHome";
    }

    @PostMapping("/userHome")
    public String processHome(@RequestParam("id") UUID id, @RequestParam(value = "selectedIncome",required = false) UUID income_id, @RequestParam(value = "selectedExpense",required = false) UUID expense_id, Model model){
        System.out.println("enterd lsnfkl");
        System.out.println("we got this id: " + income_id);
        System.out.println("expense_id " + expense_id);
        System.out.println("end...");

        if(income_id != null && id != null){
            incomeDao.deleteIncome(income_id);
            return "redirect:/userHome?id=" + id;
        }

        if(expense_id != null && id!= null){
            expenseDao.deleteExpense(expense_id);
            return "redirect:/userHome?id=" + id;
        }
        model.addAttribute("errorMessage", "id or income_id is null");
        return "redirect:/userHome?id=" + id;
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
