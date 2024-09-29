package com.sportshop.sportshop.controller;

import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final HttpSession session;

    public AdminController(ProductService productService, HttpSession session) {
        this.productService = productService;
        this.session = session;
    }

    @GetMapping
    public String listProducts(Model model) {
        if(isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "admin/list";
    }

    @PostMapping("/product")
    public String addProduct(@ModelAttribute  Product product, HttpSession session) {
        if(isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        productService.saveProduct(product);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        if(isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        if(isNotAdmin(session)) return "redirect:/user/login";
        model.addAttribute("product", new Product());
        return "admin/add";
    }

    private boolean isNotAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user == null || !user.isAdmin();
    }
}

