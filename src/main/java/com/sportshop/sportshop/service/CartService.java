package com.sportshop.sportshop.service;

import com.sportshop.sportshop.model.Cart;
import com.sportshop.sportshop.model.CartItem;
import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.repository.CartItemRepository;
import com.sportshop.sportshop.repository.CartRepository;
import com.sportshop.sportshop.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    private CartItemRepository cartItemRepository;

    private static final String SESSION_CART_KEY = "SESSION_CART";

    public CartService(CartRepository cartRepository, ProductService productService, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductService productService, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    public Cart getCurrentCart() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attrs.getRequest().getSession();

        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null) {
            return cartRepository.findByUser(authenticatedUser)
                    .orElseGet(Cart::new);
        } else {
            Cart cart = (Cart) session.getAttribute(SESSION_CART_KEY);
            if (cart == null) {
                cart = new Cart();
                session.setAttribute(SESSION_CART_KEY, cart);
            }
            return cart;
        }
    }

    public void addProductToCart(Long productId, int quantity) {
        // Get the current cart
        Cart cart = getCurrentCart();

        // Find the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // Check if the product is already in the cart
        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // If it exists, increase the quantity
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // If it doesn't exist, create a new CartItem
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getCartItems().add(newItem);
        }

        // Save the cart after adding or updating the item
        cartRepository.save(cart);
    }

    public void removeCartItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    public void updateCartItemQuantity(Long itemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
}
