package com.sportshop.sportshop.controller;

import com.sportshop.sportshop.model.Category;
import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.model.UserHistory;
import com.sportshop.sportshop.service.CategoryService;
import com.sportshop.sportshop.service.ProductService;
import com.sportshop.sportshop.service.UserHistoryService;
import com.sportshop.sportshop.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final HttpSession session;
    private final UserService userService;
    private final CategoryService categoryService;
    private final UserHistoryService userHistoryService;

    public AdminController(ProductService productService, HttpSession session, UserService userService, CategoryService categoryService, UserHistoryService userHistoryService) {
        this.productService = productService;
        this.session = session;
        this.userService = userService;
        this.categoryService = categoryService;
        this.userHistoryService = userHistoryService;
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
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        userService.blockUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/user/unblock/{id}")
    public String unblockUser(@PathVariable Long id) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        userService.unblockUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        model.addAttribute("categories", categoryService.findAllCategories());
        return "admin/categories-list";
    }

    @GetMapping("/category/add")
    public String showCategoryForm(Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        model.addAttribute("category", new Category());
        return "admin/add-category";
    }

    @PostMapping("/category/save")
    public String saveCategory(@ModelAttribute("category") Category category) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        Category category = categoryService.findCategoryById(id);
        model.addAttribute("category", category);
        return "admin/edit-category";
    }

    @PostMapping("/category/update/{id}")
    public String updateCategory(@PathVariable("id") Long id, @ModelAttribute("category") Category category) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        category.setId(id);
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/user-history")
    public String viewUserHistory(Model model) {
        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }
        List<UserHistory> historyList = userHistoryService.getAllUserHistory();
        model.addAttribute("userHistory", historyList);
        return "admin/user-history";
    }

    @GetMapping("/export/history/csv")
    @ResponseBody
    public void exportUserHistoryToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"user_history.csv\"");

        List<UserHistory> userHistoryList = userHistoryService.getAllUserHistory();

        PrintWriter writer = response.getWriter();
        writer.println("ID,User ID,Action,Timestamp");

        for (UserHistory history : userHistoryList) {
            writer.println(history.getId() + "," +
                    (history.getUserId() != null ? history.getUserId() : "Unauthenticated user") + "," +
                    history.getAction() + "," +
                    history.getTimestamp());
        }
    }

    @GetMapping("/user-history/filter")
    public String filterUserHistory(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            Model model) {

        if (isNotAdmin(session)) {
            return "redirect:/user/login";
        }

        LocalDateTime fromDateTime = (dateFrom != null && !dateFrom.isEmpty()) ? LocalDateTime.parse(dateFrom + "T00:00:00") : null;
        LocalDateTime toDateTime = (dateTo != null && !dateTo.isEmpty()) ? LocalDateTime.parse(dateTo + "T23:59:59") : null;

        List<UserHistory> filteredHistory = userHistoryService.filterUserHistory(userId, action, fromDateTime, toDateTime);
        model.addAttribute("userHistory", filteredHistory);
        return "admin/user-history";
    }

    private boolean isNotAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user == null || !user.isAdmin();
    }
}

