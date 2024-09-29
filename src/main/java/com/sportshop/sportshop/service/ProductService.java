package com.sportshop.sportshop.service;

import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> searchProducts(String category, String brand, double minPrice, double maxPrice, String size, String color) {
        return productRepository.findByCategoryAndBrandAndPriceBetweenAndSizeAndColor(category, brand, minPrice, maxPrice, size, color);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
