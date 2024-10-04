package com.sportshop.sportshop.controller;

import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "product/list";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        user.setAdmin(false);
        userService.registerUser(user);
        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        return "user/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response, HttpSession session) {
        if (userService.isValidUser(username, password)) {
            User user = userService.findByEmail(username);

            if (user != null) {
                session.setAttribute("user", user);

                if (user.isAdmin()) {
                    return "redirect:/admin";
                } else {
                    return "redirect:/products";
                }
            }
        }

        System.out.println("Invalid username or password for: " + username);
        return "redirect:/user/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response, HttpSession session) {
        session.invalidate();
        return "redirect:/products";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        User user = userService.findById(sessionUser.getId());
        if (user == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user) {
        userService.updateUser(user);
        return "redirect:/user/profile";
    }
}
