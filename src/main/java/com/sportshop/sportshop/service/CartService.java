package com.sportshop.sportshop.service;

import com.sportshop.sportshop.model.Cart;
import com.sportshop.sportshop.model.CartItem;
import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.repository.CartItemRepository;
import com.sportshop.sportshop.repository.CartRepository;
import com.sportshop.sportshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCurrentCart() {
        // Здесь должен быть ваш код для получения текущей корзины пользователя
        return new Cart(); // Временный возврат
    }

    public void addProductToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Cart cart = getCurrentCart();
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    public void removeCartItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }
}
