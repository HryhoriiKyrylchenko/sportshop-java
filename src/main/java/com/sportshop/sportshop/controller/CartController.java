package com.sportshop.sportshop.controller;

import com.sportshop.sportshop.model.Cart;
import com.sportshop.sportshop.model.CartItem;
import com.sportshop.sportshop.model.Product;
import com.sportshop.sportshop.service.CartService;
import com.sportshop.sportshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(Model model) {
        Cart cart = cartService.getCurrentCart();
        List<CartItem> cartItems = cart.getCartItems();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", calculateTotal(cartItems));
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addProductToCart(productId, 1);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long itemId) {
        cartService.removeCartItem(itemId);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long itemId, @RequestParam int quantity) {
        cartService.updateCartItemQuantity(itemId, quantity);
        return "redirect:/cart";
    }

    private double calculateTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}
