package com.sportshop.sportshop.repository;

import com.sportshop.sportshop.model.Cart;
import com.sportshop.sportshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
