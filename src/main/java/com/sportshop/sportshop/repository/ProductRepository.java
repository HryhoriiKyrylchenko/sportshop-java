package com.sportshop.sportshop.repository;

import com.sportshop.sportshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryAndBrandAndPriceBetweenAndSizeAndColor(String category, String brand, double minPrice, double maxPrice, String size, String color);
}
