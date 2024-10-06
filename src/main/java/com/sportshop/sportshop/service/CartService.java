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

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    private static final String SESSION_CART_KEY = "SESSION_CART";

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart getCurrentCart() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attrs.getRequest().getSession();

        User authenticatedUser = (User) session.getAttribute("user");

        if (authenticatedUser != null) {
            return cartRepository.findByUser(authenticatedUser)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(authenticatedUser);
                        newCart.setCartItems(new ArrayList<>());
                        return cartRepository.save(newCart);
                    });
        } else {
            Cart cart = (Cart) session.getAttribute(SESSION_CART_KEY);
            if (cart == null) {
                cart = new Cart();
                cart.setCartItems(new ArrayList<>());
                session.setAttribute(SESSION_CART_KEY, cart);
            }
            return cart;
        }
    }

    public void addProductToCart(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Cart cart = getCurrentCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);

            if (cart.getUser() != null) {
                cartItemRepository.save(existingItem);
            }
        } else {
            String sessionId = UUID.randomUUID().toString();

            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            newItem.setSessionId(sessionId);

            cart.getCartItems().add(newItem);

            if (cart.getUser() != null) {
                cartItemRepository.save(newItem);
                cartRepository.save(cart);
            }
        }
    }

    public void removeCartItem(Long itemId, String itemSessionId) {
        Cart cart = getCurrentCart();

        CartItem cartItem;

        if(itemId != null) {
            cartItem = cart.getCartItems().stream()
                    .filter(item -> item.getId() != null && item.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CartItem not found"));
        }
        else {
            cartItem = cart.getCartItems().stream()
                    .filter(item -> item.getSessionId() != null && item.getSessionId().equals(itemSessionId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CartItem not found"));
        }


        if (!cart.getCartItems().contains(cartItem)) {
            throw new RuntimeException("Unauthorized cart item removal attempt.");
        }

        cart.getCartItems().remove(cartItem);

        if (cart.getUser() != null) {
            cartItemRepository.delete(cartItem);
            cartRepository.save(cart);
        } else {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attrs.getRequest().getSession();
            session.setAttribute("SESSION_CART", cart);
        }
    }

    public void updateCartItemQuantity(Long itemId, String itemSessionId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Cart cart = getCurrentCart();

        CartItem cartItem;

        if(itemId != null) {
            cartItem = cart.getCartItems().stream()
                    .filter(item -> item.getId() != null && item.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CartItem not found"));
        }
        else {
            cartItem = cart.getCartItems().stream()
                    .filter(item -> item.getSessionId() != null && item.getSessionId().equals(itemSessionId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("CartItem not found"));
        }

        if (!cart.getCartItems().contains(cartItem)) {
            throw new RuntimeException("Unauthorized cart item update attempt.");
        }

        cartItem.setQuantity(quantity);

        if (cart.getUser() != null) {
            cartItemRepository.save(cartItem);
            cartRepository.save(cart);
        } else {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attrs.getRequest().getSession();
            session.setAttribute("SESSION_CART", cart);
        }
    }

    public void clearCartForAuthenticatedUser(Cart cart) {
        cartItemRepository.deleteAll(cart.getCartItems());
        cartRepository.delete(cart);
    }

    public void clearCartForAnonymousUser() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attrs.getRequest().getSession();
        session.removeAttribute("SESSION_CART");
    }
}
