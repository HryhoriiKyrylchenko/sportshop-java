package com.sportshop.sportshop.controller;

import com.sportshop.sportshop.model.Category;
import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.service.CategoryService;
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
    private final CategoryService categoryService;

    public AdminController(ProductService productService, HttpSession session, UserService userService, CategoryService categoryService) {
        this.productService = productService;
        this.session = session;
        this.userService = userService;
        this.categoryService = categoryService;
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

        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);

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
        if (product == null) {
            return "redirect:/admin/products";
        }

        List<Category> categories = categoryService.findAllCategories();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
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
        userService.unblockUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAllCategories());
        return "admin/categories-list";
    }

    @GetMapping("/category/add")
    public String showCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/add-category";
    }

    @PostMapping("/category/save")
    public String saveCategory(@ModelAttribute("category") Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.findCategoryById(id);
        model.addAttribute("category", category);
        return "admin/edit-category";
    }

    @PostMapping("/category/update/{id}")
    public String updateCategory(@PathVariable("id") Long id, @ModelAttribute("category") Category category) {
        category.setId(id);
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    private boolean isNotAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user == null || !user.isAdmin();
    }
}

