package com.sportshop.sportshop.controller;

import com.sportshop.sportshop.model.Cart;
import com.sportshop.sportshop.model.CartItem;
import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.service.CartService;
import com.sportshop.sportshop.service.PurchaseService;
import com.sportshop.sportshop.service.UserHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final PurchaseService purchaseService;
    private final UserHistoryService userHistoryService;

    public CartController(CartService cartService, PurchaseService purchaseService, UserHistoryService userHistoryService) {
        this.cartService = cartService;
        this.purchaseService = purchaseService;
        this.userHistoryService = userHistoryService;
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
    public String addToCart(@RequestParam Long productId) {
        cartService.addProductToCart(productId, 1);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam(required = false) Long itemId,
                                 @RequestParam String itemSessionId) {
        cartService.removeCartItem(itemId, itemSessionId);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam(required = false) Long itemId,
                                 @RequestParam String itemSessionId,
                                 @RequestParam int quantity) {
        cartService.updateCartItemQuantity(itemId, itemSessionId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        Cart cart = cartService.getCurrentCart();
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("total", calculateTotal(cart.getCartItems()));
        return "cart/checkout";
    }

    @PostMapping("/checkout/confirm")
    public String confirmCheckout(Model model, HttpSession session) {
        Cart cart = cartService.getCurrentCart();

        if (cart.getUser() != null) {
            purchaseService.savePurchase(cart);
            cartService.clearCartForAuthenticatedUser(cart);
        } else {
            cartService.clearCartForAnonymousUser();
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            userHistoryService.logUserAction(null, "Purchased item(s)");
        }
        else {
            userHistoryService.logUserAction(user.getId(), "Purchased item(s)");
        }

        model.addAttribute("message", "Your purchase was successful!");
        return "cart/confirm";
    }

    private double calculateTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }
}