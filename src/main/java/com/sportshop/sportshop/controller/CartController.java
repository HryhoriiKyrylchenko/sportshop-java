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
        Cart cart = cartService.getCurrentCart(); // Метод для получения текущей корзины
        List<CartItem> cartItems = cart.getCartItems();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", calculateTotal(cartItems)); // Метод для расчета общей стоимости
        return "cart/view"; // Шаблон для отображения корзины
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addProductToCart(productId, quantity); // Метод для добавления товара в корзину
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long itemId) {
        cartService.removeCartItem(itemId); // Метод для удаления товара из корзины
        return "redirect:/cart";
    }

    private double calculateTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}
