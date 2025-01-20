//package com.teerth.budget_app.auth.controllers;
//
//import com.teerth.budget_app.auth.dao.IncomeDao;
//import com.teerth.budget_app.auth.model.Income;
//import com.teerth.budget_app.auth.model.User;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.Banner;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.SessionAttributes;
//
//import java.util.List;
//
//@Controller
//@SessionAttributes("user")
//public class IncomeController {
//    @Autowired
//    private IncomeDao incomeDao;
//
//    @Autowired
//    private HttpSession session;
//
//    @GetMapping("/userHome/income")
//    public String getIncome(Model model){
////        List<Income> income = incomeDao.getALlIncome();
//        model.addAttribute("income",new Income());
//        return "Income";
//    }
//
//    @PostMapping("/userHome/income")
//    public String addIncome(Income income, User user, Model model){
//        System.out.println("Income: "+income.getTitle());
//         user = (User) session.getAttribute("user");
//        if(income != null){
//            incomeDao.addIncome(income,user);
//
//            System.out.println("Account Id: "+ user.getAccountId());
//            List<Income> incomeList = incomeDao.getAllIncome(user.getAccountId());
//            model.addAttribute("incomeList",incomeList);
//            System.out.println("Income List: " + incomeList);
////            System.out.println("kjsdfn "+incomeList.get(0));
//
//
//            return "userHome";
//        }
//    return "Income";
//
//    }
//
//    @GetMapping("/userHome/income/list")
//    public String listIncome(){
//
//        return "IncomeList";
//    }
////
////    @GetMapping("/income/add")
////    public String addIncomeFields(){
////        return "IncomeField";
////    }
////    @GetMapping("/helllo")
////    public String gretting(){
////        return "Income";
////    }
//
//}
