package com.sportshop.sportshop.repository;

import com.sportshop.sportshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE (:name IS NULL OR p.name LIKE %:name%) " +
            "AND (:brand IS NULL OR p.brand LIKE %:brand%) " +
            "AND (:category IS NULL OR p.category LIKE %:category%) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:size IS NULL OR p.size LIKE %:size%) " +
            "AND (:color IS NULL OR p.color LIKE %:color%)")
    List<Product> searchProducts(@Param("name") String name,
                                 @Param("brand") String brand,
                                 @Param("category") String category,
                                 @Param("minPrice") Double minPrice,
                                 @Param("maxPrice") Double maxPrice,
                                 @Param("size") String size,
                                 @Param("color") String color);
}
