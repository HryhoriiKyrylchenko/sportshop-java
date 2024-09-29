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
    public String searchProducts(@RequestParam String category, @RequestParam String brand,
                                 @RequestParam double minPrice, @RequestParam double maxPrice,
                                 @RequestParam String size, @RequestParam String color, Model model) {
        List<Product> products = productService.searchProducts(category, brand, minPrice, maxPrice, size, color);
        model.addAttribute("products", products);
        return "product/list";
    }
}
