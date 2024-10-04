package com.sportshop.sportshop.controller;

import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.service.ProductService;
import com.sportshop.sportshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final HttpSession session;
    private final UserService userService;

    public AdminController(ProductService productService, HttpSession session, UserService userService) {
        this.productService = productService;
        this.session = session;
        this.userService = userService;
    }

    @GetMapping
    public String mainAdminPage() {
        if(isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        return "admin/main";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        if(isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("title", "Product List");
        return "admin/products-list";
    }

    @GetMapping("/product/add")
    public String showAddProductForm(Model model) {
        if(isNotAdmin(session)) return "redirect:/user/login";
        model.addAttribute("product", new Product());
        return "admin/add-product";
    }

    @PostMapping("/product/add")
    public String addProduct(@ModelAttribute  Product product, HttpSession session) {
        if(isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/product/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "admin/edit-product";
    }

    @PostMapping("/product/edit")
    public String updateProduct(@ModelAttribute Product product, HttpSession session) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        productService.updateProduct(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        if(isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        if(isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        List<User> users = userService.findAllNonAdmins();
        model.addAttribute("users", users);
        model.addAttribute("title", "User List");
        return "admin/users-list";
    }

    @PostMapping("/user/block/{id}")
    public String blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/user/unblock/{id}")
    public String unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);  // разблокируем пользователя
        return "redirect:/admin/users";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    private boolean isNotAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user == null || !user.isAdmin();
    }
}

