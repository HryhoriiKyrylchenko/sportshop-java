package com.sportshop.sportshop.service;

import com.sportshop.sportshop.model.*;
import com.sportshop.sportshop.repository.ProductRepository;
import com.sportshop.sportshop.repository.PurchaseHistoryRepository;
import com.sportshop.sportshop.repository.PurchaseItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final HttpSession session;

    public PurchaseService(PurchaseHistoryRepository purchaseHistoryRepository, ProductRepository productRepository, PurchaseItemRepository purchaseItemRepository, HttpSession session) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.purchaseItemRepository = purchaseItemRepository;
        this.session = session;
    }

    @Transactional
    public void savePurchase(Cart cart) {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new IllegalArgumentException("User is not logged in");
        }

        PurchaseHistory purchaseHistory = new PurchaseHistory();
        purchaseHistory.setPurchaseDate(LocalDateTime.now());
        purchaseHistory.setUser(user);

        for (CartItem cartItem : cart.getCartItems()) {
            PurchaseItem purchaseItem = new PurchaseItem();
            purchaseItem.setProduct(cartItem.getProduct());
            purchaseItem.setPrice(cartItem.getProduct().getPrice());
            purchaseItem.setQuantity(cartItem.getQuantity());
            purchaseItem.setPurchaseHistory(purchaseHistory);

            purchaseHistory.getItems().add(purchaseItem);
        }

        PurchaseHistory savedPurchaseHistory = purchaseHistoryRepository.save(purchaseHistory);

        for (PurchaseItem item : savedPurchaseHistory.getItems()) {
            item.setPurchaseHistory(savedPurchaseHistory);
            purchaseItemRepository.save(item);
        }
    }

    public List<PurchaseHistory> getPurchaseHistoryForUser(User user) {
        return purchaseHistoryRepository.findByUserId(user.getId());
    }
}
