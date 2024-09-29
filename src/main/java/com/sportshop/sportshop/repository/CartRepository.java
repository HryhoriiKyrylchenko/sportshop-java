package com.sportshop.sportshop.repository;

import com.sportshop.sportshop.model.Cart;
import com.sportshop.sportshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);
}
