package com.sportshop.sportshop.service;

import com.sportshop.sportshop.model.Category;
import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> searchProducts(String name, String brand, Category category, Double minPrice, Double maxPrice, String size, String color) {
        return productRepository.searchProducts(name, brand, category, minPrice, maxPrice, size, color);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        } else {
            throw new RuntimeException("Product not found for ID: " + id);
        }
    }

    public void updateProduct(Product product) {
        Product existingProduct = findById(product.getId());

        existingProduct.setName(product.getName());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setSize(product.getSize());
        existingProduct.setColor(product.getColor());
        existingProduct.setImageUrl(product.getImageUrl());

        productRepository.save(existingProduct);
    }
}
