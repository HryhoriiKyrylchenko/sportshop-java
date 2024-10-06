package com.sportshop.sportshop.controller;

import com.sportshop.sportshop.model.Category;
import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.service.CategoryService;
import com.sportshop.sportshop.service.ProductService;
import com.sportshop.sportshop.service.UserHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserHistoryService userHistoryService;

    public ProductController(ProductService productService, CategoryService categoryService, UserHistoryService userHistoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userHistoryService = userHistoryService;
    }

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);

        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);

        return "product/list";
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String color,
            Model model,
            HttpSession session) {

        Category category = null;
        if (categoryId != null) {
            category = categoryService.findCategoryById(categoryId);
        }

        List<Product> products = productService.searchProducts(name, brand, category, minPrice, maxPrice, size, color);

        model.addAttribute("products", products);

        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);

        User user = (User) session.getAttribute("user");
        if (user == null) {
            userHistoryService.logUserAction(null, "Searched products");
        }
        else {
            userHistoryService.logUserAction(user.getId(), "Searched products");
        }

        return "product/list";
    }

    @GetMapping("/details/{id}")
    public String getProductDetails(@PathVariable Long id, Model model, HttpSession session) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);

        User user = (User) session.getAttribute("user");
        if (user == null) {
            userHistoryService.logUserAction(null, "Viewed product with id " + id);
        }
        else {
            userHistoryService.logUserAction(user.getId(), "Viewed product with id " + id);
        }

        return "product/details";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        return "category/list";
    }
}
