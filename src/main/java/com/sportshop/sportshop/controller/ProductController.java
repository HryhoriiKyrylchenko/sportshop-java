package com.sportshop.sportshop.controller;

import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "product/list";
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String color,
            Model model) {

        List<Product> products = productService.searchProducts(name, brand, category, minPrice, maxPrice, size, color);
        model.addAttribute("products", products);
        return "product/list"; // Change this to the name of your view for displaying products
    }

    @GetMapping("/details/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products?error=notfound";
        }
        model.addAttribute("product", product);
        return "product/details";
    }
}
